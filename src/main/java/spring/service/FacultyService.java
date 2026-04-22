package spring.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import spring.dto.FacultyAddEditDto;
import spring.dto.FacultyReadDto;
import spring.exception.DeleteException;
import spring.mapper.FacultyAddEditMapper;
import spring.mapper.FacultyReadMapper;
import spring.model.Faculty;
import spring.repository.FacultyRepository;
import spring.repository.UniversityRepository;

@Service
@AllArgsConstructor
public class FacultyService {

	private final FacultyRepository facultyRepository;
	private final FacultyReadMapper facultyReadMapper;
	private final FacultyAddEditMapper facultyAddEditMapper;
	private final UniversityRepository universityRepository;

	@Transactional
	public FacultyReadDto addFaculty(FacultyAddEditDto faculty) {
		return Optional.of(faculty).map(dto -> {
			Faculty facultys = facultyAddEditMapper.toEntity(dto);
			universityRepository.findById(dto.getUniversityId()).ifPresent(facultys::setUniversity);
			return facultys;
		}).map(facultyRepository::save).map(facultyReadMapper::toDto).orElseThrow(() -> new RuntimeException());

	}

	@Transactional(readOnly = true)
	public Optional<FacultyReadDto> getFacultyById(UUID id) {
		return Optional.ofNullable(facultyRepository.findById(id).map(facultyReadMapper::toDto)
				.orElseThrow(() -> new RuntimeException("Такого id нет. " + id)));
	}

	@Transactional(readOnly = true)
	public Optional<FacultyReadDto> getFacultyByName(String name) {
		return facultyRepository.findByName(name).map(facultyReadMapper::toDto);
	}

	@Transactional(readOnly = true)
	public List<FacultyReadDto> getAllFaculty() {
		return facultyRepository.findAll().stream().map(facultyReadMapper::toDto).collect(Collectors.toList());
	}

	@Transactional
	public Optional<FacultyReadDto> updateFaculty(UUID id, FacultyAddEditDto updateFaculty) {
		return facultyRepository.findById(id).map(entity -> {
			boolean repeatingName = entity.getName().equals(updateFaculty.getName());
			boolean reccuringUniversity = entity.getUniversity().getId().equals(updateFaculty.getUniversityId());
			boolean changed = false;
			if (!repeatingName) {
				changed = true;
			}
			if (!reccuringUniversity) {
				universityRepository.findById(updateFaculty.getUniversityId()).ifPresent(entity::setUniversity);
				changed = true;
			}
			if (!changed) {
				return entity;
			}
			facultyAddEditMapper.updateEntityFromDto(updateFaculty, entity);
			return facultyRepository.saveAndFlush(entity);
		}).map(facultyReadMapper::toDto);

	}

	@Transactional
	public void deleteFacultyById(UUID id) {
		Faculty faculty = facultyRepository.findById(id)
				.orElseThrow(() -> new DeleteException("Faculty с id " + id + " не найден."));
		if (!faculty.getDepartments().isEmpty()) {
			throw new DeleteException("Удалите сначало сущность Department, этого объекта Faculty");
		}
		facultyRepository.delete(faculty);
		facultyRepository.flush();
	}
}

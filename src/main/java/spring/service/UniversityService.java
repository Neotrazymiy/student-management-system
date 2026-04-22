package spring.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import spring.dto.UniversityAddEditDto;
import spring.dto.UniversityReadDto;
import spring.exception.DeleteException;
import spring.mapper.UniversityAddEditMapper;
import spring.mapper.UniversityReadMapper;
import spring.model.University;
import spring.repository.UniversityRepository;

@Service
@AllArgsConstructor
public class UniversityService {

	private final UniversityRepository universityRepository;
	private final UniversityAddEditMapper universityAddEditMapper;
	private final UniversityReadMapper universityReadMapper;

	@Transactional
	public UniversityReadDto addUniversity(UniversityAddEditDto university) {
		return Optional.of(university).map(universityAddEditMapper::toEntity).map(universityRepository::save)
				.map(universityReadMapper::toDto).orElseThrow(() -> new RuntimeException());
	}

	@Transactional(readOnly = true)
	public Optional<UniversityReadDto> getUniversityById(UUID id) {
		return Optional.ofNullable(universityRepository.findById(id).map(universityReadMapper::toDto)
				.orElseThrow(() -> new RuntimeException("Такого id нет. " + id)));
	}

	@Transactional(readOnly = true)
	public Optional<UniversityReadDto> getUniversityByName(String name) {
		return universityRepository.findByName(name).map(universityReadMapper::toDto);
	}

	@Transactional(readOnly = true)
	public List<UniversityReadDto> getAllUniversitys() {
		return universityRepository.findAll().stream().map(universityReadMapper::toDto).collect(Collectors.toList());
	}

	@Transactional
	public Optional<UniversityReadDto> updateUniversity(UUID id, UniversityAddEditDto updateUniversity) {
		return universityRepository.findById(id).map(entity -> {
			if (entity.getName().equals(updateUniversity.getName())) {
				return entity;
			}
			universityAddEditMapper.updateEntityFromDto(updateUniversity, entity);
			return universityRepository.saveAndFlush(entity);
		}).map(universityReadMapper::toDto);
	}

	@Transactional
	public void deleteUniversityById(UUID id) {
		University university = universityRepository.findById(id)
				.orElseThrow(() -> new DeleteException("University с id " + id + " не найден."));
		if (!university.getFacultis().isEmpty()) {
			throw new DeleteException("Удалите сначало сущность Faculty, этого объекта University.");
		}
		universityRepository.delete(university);
		universityRepository.flush();
	}
}

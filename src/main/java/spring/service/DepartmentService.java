package spring.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import spring.dto.DepartmentAddEditDto;
import spring.dto.DepartmentReadDto;
import spring.exception.DeleteException;
import spring.mapper.DepartmentAddEditMapper;
import spring.mapper.DepartmentReadMapper;
import spring.model.Department;
import spring.repository.DepartmentRepository;
import spring.repository.FacultyRepository;

@Service
@AllArgsConstructor
public class DepartmentService {

	private final DepartmentRepository departmentRepository;
	private final DepartmentAddEditMapper departmentAddEditMapper;
	private final DepartmentReadMapper departmentReadMapper;
	private final FacultyRepository facultyRepository;

	@Transactional
	public DepartmentReadDto addDepartment(DepartmentAddEditDto department) {
		return Optional.of(department).map(dto -> {
			Department departments = departmentAddEditMapper.toEntity(dto);
			facultyRepository.findById(department.getFacultyId()).ifPresent(departments::setFaculty);
			return departments;
		}).map(departmentRepository::save).map(departmentReadMapper::toDto).orElseThrow(() -> new RuntimeException());
	}

	@Transactional(readOnly = true)
	public Optional<DepartmentReadDto> getDepartmentById(UUID id) {
		return Optional.ofNullable(departmentRepository.findById(id).map(departmentReadMapper::toDto)
				.orElseThrow(() -> new RuntimeException("Такого id нет. " + id)));
	}

	@Transactional(readOnly = true)
	public Optional<DepartmentReadDto> getDepartmentByName(String name) {
		return departmentRepository.findByName(name).map(departmentReadMapper::toDto);
	}

	@Transactional(readOnly = true)
	public List<DepartmentReadDto> getAllDepartments() {
		return departmentRepository.findAll().stream().map(departmentReadMapper::toDto).collect(Collectors.toList());
	}

	@Transactional
	public Optional<DepartmentReadDto> updateDepartment(UUID id, DepartmentAddEditDto updateDepartment) {
		return departmentRepository.findById(id).map(entity -> {
			boolean repeatingName = entity.getName().equals(updateDepartment.getName());
			boolean recurringFaculty = entity.getFaculty().getId().equals(updateDepartment.getFacultyId());
			boolean changed = false;
			if (!repeatingName) {
				changed = true;
			}
			if (!recurringFaculty) {
				facultyRepository.findById(updateDepartment.getFacultyId()).ifPresent(entity::setFaculty);
				changed = true;
			}
			if (!changed) {
				return entity;
			}
			departmentAddEditMapper.updateEntityFromDto(updateDepartment, entity);
			return departmentRepository.saveAndFlush(entity);
		}).map(departmentReadMapper::toDto);
	}

	@Transactional
	public void deleteDepartmentById(UUID id) {
		Department department = departmentRepository.findById(id)
				.orElseThrow(() -> new DeleteException("Department с id " + id + " не найден."));
		if (!department.getCourses().isEmpty()) {
			throw new DeleteException("Удалите сначало сущность Course, этого объекта Department");
		}
		if (!department.getGroups().isEmpty()) {
			throw new DeleteException("Удалите сначало сущность Group, этого объекта Deaprtment");
		}
		if (!department.getTeachers().isEmpty()) {
			throw new DeleteException("Удалите сначало сущность Teacher, этого объекта Department");
		}
		departmentRepository.delete(department);
		departmentRepository.flush();
	}
}

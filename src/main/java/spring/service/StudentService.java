package spring.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import spring.dto.StudentAddEditDto;
import spring.dto.StudentReadDto;
import spring.mapper.StudentAddEditMapper;
import spring.mapper.StudentReadMapper;
import spring.model.Group;
import spring.model.Role;
import spring.repository.GroupRepository;
import spring.repository.RoleRepository;
import spring.repository.StudentRepository;

@Service
@AllArgsConstructor
public class StudentService {

	private final StudentRepository studentRepository;
	private final StudentAddEditMapper studentAddEditMapper;
	private final GroupRepository groupRepository;
	private final RoleRepository roleRepository;
	private final StudentReadMapper studentReadMapper;

	@Transactional
	public StudentReadDto addStudent(StudentAddEditDto student) {
		return Optional.of(student).map(studentAddEditMapper::toEntity).map(studentRepository::save)
				.map(studentReadMapper::toDto).orElseThrow(() -> new RuntimeException());
	}

	@Transactional(readOnly = true)
	public Optional<StudentReadDto> getStudentById(UUID id) {
		return Optional.ofNullable(studentRepository.findById(id).map(studentReadMapper::toDto)
				.orElseThrow(() -> new RuntimeException("Такого id нет. " + id)));
	}

	@Transactional(readOnly = true)
	public StudentReadDto getStudentByUserId(UUID userId) {
		return studentRepository.findByUserId(userId).map(studentReadMapper::toDto).get();
	}

	public List<StudentReadDto> getStudentByIds(List<UUID> studentIds) {
		return studentIds != null ? studentRepository.findByIdIn(studentIds).stream().map(studentReadMapper::toDto)
				.collect(Collectors.toList()) : null;
	}

	public Page<StudentReadDto> getPageStudentByIds(List<UUID> studentIds, Pageable pageable) {
		if (studentIds == null || studentIds.isEmpty()) {
			return Page.empty(pageable);
		}
		return studentRepository.findByIdIn(studentIds, pageable).map(studentReadMapper::toDto);
	}

	@Transactional(readOnly = true)
	public Optional<StudentReadDto> getStudentByName(String firstName, String lastName) {
		return studentRepository.findByUserFirstNameAndUserLastName(firstName, lastName).map(studentReadMapper::toDto);
	}

	@Transactional(readOnly = true)
	public Optional<StudentReadDto> getStudentByUserName(String userName) {
		return studentRepository.findByUserUserName(userName).map(studentReadMapper::toDto);
	}

	@Transactional(readOnly = true)
	public List<StudentReadDto> getAllStudents() {
		return studentRepository.findAll().stream().map(studentReadMapper::toDto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public Page<StudentReadDto> getAllPageStudents(Pageable pageable, UUID groupId) {
		if (groupId != null) {
			return studentRepository.findAllByGroup_Id(pageable, groupId).map(studentReadMapper::toDto);
		}
		return studentRepository.findAll(pageable).map(studentReadMapper::toDto);
	}

	@Transactional
	public Optional<StudentReadDto> updateStudent(UUID id, StudentAddEditDto updateStudent) {
		return studentRepository.findById(id).map(entity -> {
			if (updateStudent.getGroupId() != null) {
				Group group = entity.getGroup();
				if (group == null || !group.getId().equals(updateStudent.getGroupId())) {
					groupRepository.findById(updateStudent.getGroupId()).ifPresent(entity::setGroup);
				}
			}
			List<Role> roles = roleRepository.findAllById(updateStudent.getUser().getRoleIds());
			entity.getUser().getRoles().clear();
			entity.getUser().setRoles(roles);
			studentAddEditMapper.updateEntityFromDto(updateStudent, entity);
			return studentRepository.saveAndFlush(entity);
		}).map(studentReadMapper::toDto);
	}

	@Transactional
	public boolean deleteStudentById(UUID id) {
		return studentRepository.findById(id).map(entity -> {
			studentRepository.delete(entity);
			return true;
		}).orElse(false);
	}

	@Transactional(readOnly = true)
	public List<StudentReadDto> getStudentsWithoutLesson(List<UUID> lessonIds) {
		List<StudentReadDto> dtos = studentRepository.findStudentsNotInLessons(lessonIds).stream()
				.map(studentReadMapper::toDto).collect(Collectors.toList());
		if (lessonIds == null || dtos == null || lessonIds.isEmpty() || dtos.isEmpty()) {
			return getAllStudents();
		}
		return dtos;
	}

}

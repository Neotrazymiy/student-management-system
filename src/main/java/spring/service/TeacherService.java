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
import spring.dto.TeacherAddEditDto;
import spring.dto.TeacherReadDto;
import spring.mapper.TeacherAddEditMapper;
import spring.mapper.TeacherReadMapper;
import spring.model.Course;
import spring.model.Lesson;
import spring.model.Role;
import spring.repository.CourseRepository;
import spring.repository.DepartmentRepository;
import spring.repository.LessonRepository;
import spring.repository.RoleRepository;
import spring.repository.TeacherRepository;

@Service
@AllArgsConstructor
public class TeacherService {

	private final TeacherRepository teacherRepository;
	private final TeacherAddEditMapper teacherAddEditMapper;
	private final TeacherReadMapper teacherReadMapper;
	private final DepartmentRepository departmentRepository;
	private final CourseRepository courseRepository;
	private final LessonRepository lessonRepository;
	private final RoleRepository roleRepository;

	@Transactional
	public TeacherReadDto addTeacher(TeacherAddEditDto teacher) {
		return Optional.of(teacher).map(teacherAddEditMapper::toEntity).map(teacherRepository::save)
				.map(teacherReadMapper::toDto).orElseThrow(() -> new RuntimeException());
	}

	@Transactional(readOnly = true)
	public Optional<TeacherReadDto> getTeacherById(UUID id) {
		return Optional.ofNullable(teacherRepository.findById(id).map(teacherReadMapper::toDto)
				.orElseThrow(() -> new RuntimeException("Такого id нет. " + id)));
	}

	@Transactional(readOnly = true)
	public TeacherReadDto getTeacherByUserId(UUID userId) {
		return teacherRepository.findByUserId(userId).map(teacherReadMapper::toDto)
				.orElseThrow(() -> new RuntimeException("Такого id нет." + userId));
	}

	@Transactional(readOnly = true)
	public Optional<TeacherReadDto> getTeacherByName(String firstName, String lastName) {
		return teacherRepository.findByUserFirstNameAndUserLastName(firstName, lastName).map(teacherReadMapper::toDto);
	}

	@Transactional(readOnly = true)
	public List<TeacherReadDto> getAllTeachers() {
		return teacherRepository.findAll().stream().map(teacherReadMapper::toDto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public Page<TeacherReadDto> getAllPageTeachers(Pageable pageable) {
		return teacherRepository.findAll(pageable).map(teacherReadMapper::toDto);
	}

	@Transactional
	public Optional<TeacherReadDto> updateTeacher(UUID id, TeacherAddEditDto updateTeacher) {
		return teacherRepository.findById(id).map(entity -> {
			departmentRepository.findById(updateTeacher.getDepartmentId()).ifPresent(entity::setDepartment);

			List<Course> courses = courseRepository.findAllById(updateTeacher.getCourseIds());
			entity.getCourses().clear();
			entity.setCourses(courses);

			List<Lesson> lessons = lessonRepository.findAllById(updateTeacher.getLessonIds());
			entity.getLessons().clear();
			entity.setLessons(lessons);

			List<Role> roles = roleRepository.findAllById(updateTeacher.getUser().getRoleIds());
			entity.getUser().getRoles().clear();
			entity.getUser().setRoles(roles);

			teacherAddEditMapper.updateEntityFromDto(updateTeacher, entity);
			return teacherRepository.saveAndFlush(entity);
		}).map(teacherReadMapper::toDto);
	}

	@Transactional
	public boolean deleteTeacherById(UUID id) {
		return teacherRepository.findById(id).map(entity -> {
			teacherRepository.delete(entity);
			return true;
		}).orElse(false);
	}

}

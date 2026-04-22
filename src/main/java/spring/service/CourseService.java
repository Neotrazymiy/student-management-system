package spring.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import spring.dto.CourseAddEditDto;
import spring.dto.CourseReadDto;
import spring.mapper.CourseAddEditMapper;
import spring.mapper.CourseReadMapper;
import spring.model.Course;
import spring.model.Group;
import spring.repository.CourseRepository;
import spring.repository.DepartmentRepository;
import spring.repository.GroupRepository;

@Service
@AllArgsConstructor
public class CourseService {

	private final CourseRepository courseRepository;
	private final CourseReadMapper courseReadMapper;
	private final CourseAddEditMapper courseAddEditMapper;
	private final GroupRepository groupRepository;
	private final DepartmentRepository departmentRepository;

	@Transactional
	public CourseReadDto addCourse(CourseAddEditDto courseAddEditDto) {
		return Optional.of(courseAddEditDto).map(dto -> {
			Course course = courseAddEditMapper.toEntity(courseAddEditDto);
			departmentRepository.findById(dto.getDepartmentId()).ifPresent(course::setDepartment);
			Group group = groupRepository.findById(dto.getGroupId())
					.orElseThrow(() -> new RuntimeException("Группы под таким ид - " + dto.getGroupId() + ", нет."));
			course.setGroups(Arrays.asList(group));
			group.setCourses(Arrays.asList(course));
			return course;
		}).map(courseRepository::save).map(courseReadMapper::toDto).orElseThrow(() -> new RuntimeException());
	}

	@Transactional(readOnly = true)
	public Optional<CourseReadDto> getCourseById(UUID id) {
		return Optional.ofNullable(courseRepository.findById(id).map(courseReadMapper::toDto)
				.orElseThrow(() -> new RuntimeException("Такого id нет. " + id)));
	}

	@Transactional(readOnly = true)
	public Optional<CourseReadDto> getCourseByName(String name) {
		return courseRepository.findByCourseName(name).map(courseReadMapper::toDto);
	}

	@Transactional(readOnly = true)
	public List<CourseReadDto> getAllCourses() {
		return courseRepository.findAll().stream().map(courseReadMapper::toDto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public Page<CourseReadDto> getAllPageCourses(Pageable pageable) {
		return courseRepository.findAll(pageable).map(courseReadMapper::toDto);
	}

	@Transactional
	public Optional<CourseReadDto> updateCourse(UUID id, CourseAddEditDto updateCourse) {
		return courseRepository.findById(id).map(entity -> {
			boolean repeatingName = entity.getCourseName().equals(updateCourse.getCourseName());
			boolean recurringDepartment = entity.getDepartment().getId().equals(updateCourse.getDepartmentId());
			boolean newGroup = entity.getGroups().stream().noneMatch(g -> g.getId().equals(updateCourse.getGroupId()));
			boolean changed = false;
			if (!repeatingName) {
				changed = true;
			}
			if (!recurringDepartment) {
				departmentRepository.findById(updateCourse.getDepartmentId()).ifPresent(entity::setDepartment);
				changed = true;
			}
			if (newGroup && updateCourse.getGroupId() != null) {
				Group group = groupRepository.findById(updateCourse.getGroupId())
						.orElseThrow(() -> new RuntimeException("id группы не найден."));
				entity.getGroups().add(group);
				group.getCourses().add(entity);
				changed = true;
			}
			if (!changed) {
				return entity;
			}
			courseAddEditMapper.updateEntityFromDto(updateCourse, entity);
			return courseRepository.saveAndFlush(entity);
		}).map(courseReadMapper::toDto);
	}

	@Transactional
	public boolean deleteCourseById(UUID id) {
		return courseRepository.findById(id).map(entity -> {
			courseRepository.delete(entity);
			return true;
		}).orElse(false);
	}

}

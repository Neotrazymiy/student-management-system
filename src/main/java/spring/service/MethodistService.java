package spring.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import spring.dto.GroupReadDto;
import spring.dto.LessonAddEditDto;
import spring.dto.StudentReadDto;
import spring.model.Course;
import spring.model.Teacher;
import spring.repository.CourseRepository;
import spring.repository.TeacherRepository;

@Service
@AllArgsConstructor
public class MethodistService {

	private final TeacherRepository teacherRepository;
	private final CourseRepository courseRepository;
	private final StudentService studentService;
	private final GroupService groupService;
	private final LessonService lessonService;

	@Transactional
	public void updateTeacherCourses(UUID teacherId, UUID courseId) {
		Teacher teacher = teacherRepository.findById(teacherId)
				.orElseThrow(() -> new RuntimeException("Такого id нет. " + teacherId));
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new RuntimeException("Такого id нет. " + courseId));
		teacher.getCourses().add(course);
		course.getTeachers().add(teacher);
		teacherRepository.save(teacher);
	}

	@Transactional
	public void deleteTeacherCourses(UUID teacherId, UUID courseId) {
		Teacher teacher = teacherRepository.findById(teacherId)
				.orElseThrow(() -> new RuntimeException("Такого id нет. " + teacherId));
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new RuntimeException("Такого id нет. " + courseId));
		teacher.getCourses().remove(course);
		course.getTeachers().remove(teacher);
		teacherRepository.save(teacher);
	}

	@Transactional(readOnly = true)
	public List<UUID> availableStudentsUpdate(LessonAddEditDto dto) {
		List<UUID> uuids = lessonService.getLessonIdsByDate(dto.getFrom(), dto.getStartTime());
		List<StudentReadDto> currentStudents = studentService.getStudentByIds(dto.getStudentIds());
		if (uuids.size() == 1) {
			uuids.clear();
		}
		List<StudentReadDto> availableStudents = studentService.getStudentsWithoutLesson(uuids);
		availableStudents.removeAll(currentStudents);
		return availableStudents.stream().map(s -> s.getId()).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<UUID> availableStudentsCreate(LessonAddEditDto dto) {
		List<UUID> uuids = lessonService.getLessonIdsByDate(dto.getFrom(), dto.getStartTime());
		if (uuids.size() == 1) {
			uuids.clear();
		}
		return studentService.getStudentsWithoutLesson(uuids).stream().map(s -> s.getId()).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<UUID> availableGroupsUpdate(LessonAddEditDto dto) {
		List<UUID> uuids = lessonService.getLessonIdsByDate(dto.getFrom(), dto.getStartTime());
		List<GroupReadDto> currentGroups = groupService.getGroupsByIds(dto.getGroupIds());
		if (uuids.size() == 1) {
			uuids.clear();
		}
		List<GroupReadDto> availableGroups = groupService.getGroupsWithoutLesson(uuids);
		availableGroups.removeAll(currentGroups);
		return availableGroups.stream().map(g -> g.getId()).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<UUID> availableGroupsCreate(LessonAddEditDto dto) {
		List<UUID> uuids = lessonService.getLessonIdsByDate(dto.getFrom(), dto.getStartTime());
		if (uuids.size() == 1) {
			uuids.clear();
		}
		return groupService.getGroupsWithoutLesson(uuids).stream().map(g -> g.getId()).collect(Collectors.toList());
	}

}

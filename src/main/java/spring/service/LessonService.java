package spring.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import spring.auxiliaryObjects.DateRange;
import spring.dto.LessonAddEditDto;
import spring.dto.LessonReadDto;
import spring.event.ChangeLessonEvent;
import spring.filter.lesson.LessonDateSpecific;
import spring.filter.lesson.LessonModelSpecific;
import spring.mapper.LessonAddEditMapper;
import spring.mapper.LessonReadMapper;
import spring.model.DateFilter;
import spring.model.Group;
import spring.model.Lesson;
import spring.model.Student;
import spring.repository.CourseRepository;
import spring.repository.GroupRepository;
import spring.repository.LessonRepository;
import spring.repository.RoomRepository;
import spring.repository.StudentRepository;
import spring.repository.TeacherRepository;

@Service
@AllArgsConstructor
public class LessonService {

	private final LessonRepository lessonRepository;
	private final LessonAddEditMapper lessonAddEditMapper;
	private final LessonReadMapper lessonReadMapper;
	private final StudentRepository studentRepository;
	private final TeacherRepository teacherRepository;
	private final CourseRepository courseRepository;
	private final RoomRepository roomRepository;
	private final GroupRepository groupRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public LessonReadDto addLessonForStudents(LessonAddEditDto lessonAddEditDto) {
		return Optional.of(lessonAddEditDto).map(dto -> {
			Lesson lesson = lessonAddEditMapper.toEntity(dto);
			List<Student> students = studentRepository.findByIdIn(dto.getStudentIds());
			lesson.setStudents(students);
			students.forEach(s -> s.getLessons().add(lesson));
			courseRepository.findById(dto.getCourseId()).ifPresent(lesson::setCourse);
			roomRepository.findById(dto.getRoomId()).ifPresent(lesson::setRoom);
			return lesson;
		}).map(lessonRepository::save).map(lessonReadMapper::toDto).orElseThrow(() -> new RuntimeException());
	}

	@Transactional
	public LessonReadDto addLessonForGroups(LessonAddEditDto lessonAddEditDto) {
		return Optional.of(lessonAddEditDto).map(dto -> {
			Lesson lesson = lessonAddEditMapper.toEntity(dto);
			List<Group> groups = groupRepository.findByIdIn(dto.getGroupIds());
			lesson.setGroups(groups);
			groups.forEach(s -> s.getLessons().add(lesson));
			courseRepository.findById(dto.getCourseId()).ifPresent(lesson::setCourse);
			roomRepository.findById(dto.getRoomId()).ifPresent(lesson::setRoom);
			return lesson;
		}).map(lessonRepository::save).map(lessonReadMapper::toDto).orElseThrow(() -> new RuntimeException());
	}

	@Transactional(readOnly = true)
	public Optional<LessonReadDto> getLessonById(UUID id) {
		return Optional.ofNullable(lessonRepository.findById(id).map(lessonReadMapper::toDto)
				.orElseThrow(() -> new RuntimeException("Такого id нет. " + id)));
	}

	@Transactional(readOnly = true)
	public Optional<LessonReadDto> getLessonByNameAndDate(String userName, LocalDate date, LocalTime startTime) {
		return lessonRepository.findByTeachersUserUserNameAndDateAndStartTime(userName, date, startTime)
				.map(lessonReadMapper::toDto);
	}

	@Transactional(readOnly = true)
	public List<LessonReadDto> getAllLessons() {
		return lessonRepository.findAll().stream().map(lessonReadMapper::toDto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public Page<LessonReadDto> getAllPageLessons(Pageable pageable) {
		return lessonRepository.findAll(pageable).map(lessonReadMapper::toDto);
	}

	@Transactional(readOnly = true)
	public Map<UUID, LessonReadDto> getAllMapLessons(Pageable pageable) {
		return getAllPageLessons(pageable).stream().collect(Collectors.toMap(ls -> ls.getId(), Function.identity()));
	}

	@Transactional(readOnly = true)
	public Page<LessonReadDto> getPageLessonsfilter(Pageable pageable, LessonAddEditDto dto) {
		DateRange dateRange = getRangeTime(dto.getFrom(), dto.getTo(), dto.getDateFilter());

		Specification<Lesson> spec = Specification.where(null);
		spec = spec.and(LessonModelSpecific.hasCourse(dto.getCourseId()))
				.and(LessonModelSpecific.hasGroup(dto.getGroupId())).and(LessonModelSpecific.hasRoom(dto.getRoomId()))
				.and(LessonModelSpecific.hasTeacher(dto.getTeacherId()));
		if (dateRange != null) {
			spec = spec.and(LessonDateSpecific.dateBetween(dateRange.getDate(), dateRange.getTo()));
		}

		return lessonRepository.findAll(spec, pageable).map(lessonReadMapper::toDto);
	}

	@Transactional
	public Page<LessonReadDto> getPageStudentSchedule(Pageable pageable, String studentName, LessonAddEditDto dto) {
		DateRange dateRange = getRangeTime(dto.getFrom(), dto.getTo(), dto.getDateFilter());
		Student student = studentRepository.findByUserUserName(studentName).orElseThrow(() -> new RuntimeException());
		if (student.getGroup() == null) {
			throw new RuntimeException("Вы без группы, обратитесь к администратору.");
		}

		Specification<Lesson> spec = Specification.where(LessonModelSpecific.hasGroup(student.getGroup().getId())
				.and(LessonModelSpecific.hasCourse(dto.getCourseId())).and(LessonModelSpecific.hasRoom(dto.getRoomId()))
				.and(LessonModelSpecific.hasTeacher(dto.getTeacherId()))
				.and(LessonModelSpecific.hasDepartment(dto.getDepartmentId())));
		if (dateRange != null) {
			spec = spec.and(LessonDateSpecific.dateBetween(dateRange.getDate(), dateRange.getTo()));
		}

		return lessonRepository.findAll(spec, pageable).map(lessonReadMapper::toDto);
	}

	@Transactional
	public List<LessonReadDto> getStudentScheduleForExport(String studentName, LessonAddEditDto dto) {
		DateRange dateRange = getRangeTime(dto.getFrom(), dto.getTo(), dto.getDateFilter());
		Student student = studentRepository.findByUserUserName(studentName).orElseThrow(() -> new RuntimeException());
		if (student.getGroup() == null) {
			throw new RuntimeException("Вы без группы, обратитесь к администратору.");
		}

		Specification<Lesson> spec = Specification.where(LessonModelSpecific.hasGroup(student.getGroup().getId())
				.and(LessonModelSpecific.hasCourse(dto.getCourseId())).and(LessonModelSpecific.hasRoom(dto.getRoomId()))
				.and(LessonModelSpecific.hasTeacher(dto.getTeacherId()))
				.and(LessonModelSpecific.hasDepartment(dto.getDepartmentId())));
		if (dateRange != null) {
			spec = spec.and(LessonDateSpecific.dateBetween(dateRange.getDate(), dateRange.getTo()));
		}

		return lessonRepository.findAll(spec).stream().map(lessonReadMapper::toDto).collect(Collectors.toList());
	}

	@Transactional
	public Page<LessonReadDto> getPageTeacherSchedule(Pageable pageable, String teacherName, LessonAddEditDto dto) {
		DateRange dateRange = getRangeTime(dto.getFrom(), dto.getTo(), dto.getDateFilter());

		Specification<Lesson> spec = Specification
				.where(LessonModelSpecific.hasTeacher(teacherRepository.findByUserUserName(teacherName)
						.orElseThrow(() -> new RuntimeException()).getId()))
				.and(LessonModelSpecific.hasDepartment(dto.getDepartmentId()));
		if (dateRange != null) {
			spec = spec.and(LessonDateSpecific.dateBetween(dateRange.getDate(), dateRange.getTo()));
		}

		return lessonRepository.findAll(spec, pageable).map(lessonReadMapper::toDto);
	}

	@Transactional
	public List<LessonReadDto> getTeacherScheduleForExport(String teacherName, LessonAddEditDto dto) {
		DateRange dateRange = getRangeTime(dto.getFrom(), dto.getTo(), dto.getDateFilter());

		Specification<Lesson> spec = Specification
				.where(LessonModelSpecific.hasTeacher(teacherRepository.findByUserUserName(teacherName)
						.orElseThrow(() -> new RuntimeException()).getId()))
				.and(LessonModelSpecific.hasDepartment(dto.getDepartmentId()));
		if (dateRange != null) {
			spec = spec.and(LessonDateSpecific.dateBetween(dateRange.getDate(), dateRange.getTo()));
		}

		return lessonRepository.findAll(spec).stream().map(lessonReadMapper::toDto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public DateRange getRangeTime(LocalDate date, LocalDate to, DateFilter dateFilter) {
		if (date == null && to == null && dateFilter == null) {
			return null;
		}
		if (date == null) {
			date = LocalDate.now();
		}
		if (dateFilter == null) {
			dateFilter = DateFilter.DAY;
		}
		if (dateFilter == DateFilter.DAY) {
			return new DateRange(date, date);
		} else if (dateFilter == DateFilter.WEEK) {
			return new DateRange(date, date.plusDays(6));
		} else if (dateFilter == DateFilter.MONTH) {
			return new DateRange(date, date.plusMonths(1).minusDays(1));
		} else if (dateFilter == DateFilter.CURRENT_MONTH) {
			return new DateRange(date.withDayOfMonth(1), date.withDayOfMonth(date.lengthOfMonth()));
		} else if (dateFilter == DateFilter.CASTOM && to != null) {
			return new DateRange(date, to);
		}
		return null;
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Transactional
	public Optional<LessonReadDto> updateLesson(UUID id, LessonAddEditDto updateLesson) {
		return lessonRepository.findById(id).map(entity -> {
			boolean changed = false;
			changed = chackRecurringEntity(entity, updateLesson) || checkRecurringObject(entity, updateLesson);
			if (!changed) {
				return entity;
			}

			lessonAddEditMapper.updateEntityFromDto(updateLesson, entity);
			Lesson save = lessonRepository.saveAndFlush(entity);
			eventPublisher.publishEvent(new ChangeLessonEvent(save));
			return save;
		}).map(lessonReadMapper::toDto);
	}

	private boolean chackRecurringEntity(Lesson entity, LessonAddEditDto updateLesson) {
		boolean recurringCourse = entity.getCourse().getId().equals(updateLesson.getCourseId());
		boolean recurringGroups = entity.getGroups().stream().map(Group::getId).collect(Collectors.toSet())
				.equals(new HashSet<>(updateLesson.getGroupIds()));
		boolean recurringRoom = entity.getRoom().getId().equals(updateLesson.getRoomId());
		boolean recurringStudents = entity.getStudents().stream().map(Student::getId).collect(Collectors.toSet())
				.equals(new HashSet<>(updateLesson.getStudentIds()));
		boolean changed = false;

		if (!recurringCourse) {
			courseRepository.findById(updateLesson.getCourseId()).ifPresent(entity::setCourse);
			changed = true;
		}
		if (!recurringRoom) {
			roomRepository.findById(updateLesson.getRoomId()).ifPresent(entity::setRoom);
			changed = true;
		}
		if (!recurringGroups && updateLesson.getGroupIds().size() > 0) {
			addGroupsInLesson(entity, updateLesson);
			changed = true;
		}
		if (!recurringStudents && updateLesson.getStudentIds().size() > 0) {
			addStudentsInLesson(entity, updateLesson);
			changed = true;
		}
		return changed;
	}

	private boolean checkRecurringObject(Lesson entity, LessonAddEditDto updateLesson) {
		boolean recurringDate = entity.getDate().equals(updateLesson.getFrom());
		boolean recurringStartTime = entity.getStartTime().equals(updateLesson.getStartTime());
		boolean recurringEndTime = entity.getEndTime().equals(updateLesson.getEndTime());
		boolean recurringStatus = entity.getStatus().equals(updateLesson.getStatus());
		boolean changed = false;

		if (!recurringDate) {
			entity.setDate(updateLesson.getFrom());
			changed = true;
		}
		if (!recurringStartTime) {
			entity.setStartTime(updateLesson.getStartTime());
			changed = true;
		}
		if (!recurringEndTime) {
			entity.setEndTime(updateLesson.getEndTime());
			changed = true;
		}
		if (!recurringStatus) {
			entity.setStatus(updateLesson.getStatus());
			changed = true;
		}
		return changed;
	}

	private void addGroupsInLesson(Lesson entity, LessonAddEditDto updateLesson) {
		if (!entity.getStudents().isEmpty()) {
			for (Student student : entity.getStudents()) {
				student.getLessons().remove(entity);
			}
			entity.getStudents().clear();
		}
		List<Group> currentGroups = entity.getGroups();
		List<Group> newGroups = groupRepository.findByIdIn(updateLesson.getGroupIds());
		Set<Group> currentSet = new HashSet<>(currentGroups);
		Set<Group> newSet = new HashSet<>(newGroups);

		currentSet.stream().filter(s -> !newSet.contains(s)).peek(s -> s.getLessons().remove(entity))
				.collect(Collectors.toSet());

		newSet.stream().filter(s -> !currentSet.contains(s)).peek(s -> s.getLessons().add(entity))
				.collect(Collectors.toSet());

		entity.setGroups(newGroups);
	}

	private void addStudentsInLesson(Lesson entity, LessonAddEditDto updateLesson) {
		if (!entity.getGroups().isEmpty()) {
			for (Group group : entity.getGroups()) {
				group.getLessons().remove(entity);
			}
			entity.getGroups().clear();
		}
		List<Student> currentStudents = entity.getStudents();
		List<Student> newStudents = studentRepository.findByIdIn(updateLesson.getStudentIds());
		Set<Student> currentSet = new HashSet<>(currentStudents);
		Set<Student> newSet = new HashSet<>(newStudents);

		currentSet.stream().filter(s -> !newSet.contains(s)).peek(s -> s.getLessons().remove(entity))
				.collect(Collectors.toSet());

		newSet.stream().filter(s -> !currentSet.contains(s)).peek(s -> s.getLessons().add(entity))
				.collect(Collectors.toSet());

		entity.setStudents(newStudents);
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Transactional
	public boolean deleteLessonById(UUID id) {
		return lessonRepository.findById(id).map(entity -> {
			lessonRepository.delete(entity);
			return true;
		}).orElse(false);
	}

	@Transactional(readOnly = true)
	public List<UUID> getLessonIdsByDate(LocalDate date, LocalTime time) {
		return lessonRepository.findByDateAndStartTime(date, time).stream().map(Lesson::getId)
				.collect(Collectors.toList());
	}

}

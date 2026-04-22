package spring.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import spring.dto.TeacherAddEditDto;
import spring.model.Course;
import spring.model.Lesson;
import spring.model.Teacher;

@Mapper(componentModel = "spring", uses = UserAddEditMapper.class)
public interface TeacherAddEditMapper {

	@Mapping(target = "department", ignore = true)
	@Mapping(target = "courses", ignore = true)
	@Mapping(target = "lessons", ignore = true)
	@Mapping(target = "id", ignore = true)
	Teacher toEntity(TeacherAddEditDto dto);

	@Mapping(target = "departmentId", source = "department.id")
	@Mapping(target = "courseIds", source = "courses", qualifiedByName = "coursesToId")
	@Mapping(target = "lessonIds", source = "lessons", qualifiedByName = "lessonsToId")
	TeacherAddEditDto toDto(Teacher entity);

	@Mapping(target = "department", ignore = true)
	@Mapping(target = "courses", ignore = true)
	@Mapping(target = "lessons", ignore = true)
	@Mapping(target = "user", qualifiedByName = "updateUser")
	@Mapping(target = "id", ignore = true)
	void updateEntityFromDto(TeacherAddEditDto dto, @MappingTarget Teacher entity);

	@Named("coursesToId")
	default UUID map(Course course) {
		return course == null ? null : course.getId();
	}

	@Named("lessonsToId")
	default UUID map(Lesson lesson) {
		return lesson == null ? null : lesson.getId();
	}
}
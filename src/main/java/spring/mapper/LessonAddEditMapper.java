package spring.mapper;

import java.util.UUID;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import spring.dto.LessonAddEditDto;
import spring.model.Lesson;
import spring.model.Student;

@Mapper(componentModel = "spring")
public interface LessonAddEditMapper {

	@Mapping(target = "course", ignore = true)
	@Mapping(target = "room", ignore = true)
	@Mapping(target = "groups", ignore = true)
	@Mapping(target = "teachers", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "students", ignore = true)
	@Mapping(target = "date", source = "from")
	Lesson toEntity(LessonAddEditDto dto);

	@InheritInverseConfiguration(name = "toEntity")
	@Mapping(target = "studentIds", source = "students", qualifiedByName = "studentToId")
	@Mapping(target = "courseId", source = "course.id")
	@Mapping(target = "roomId", source = "room.id")
	@Mapping(target = "from", source = "date")
	@Mapping(target = "teacherId", ignore = true)
	@Mapping(target = "dateFilter", ignore = true)
	@Mapping(target = "groupId", ignore = true)
	@Mapping(target = "departmentId", ignore = true)
	@Mapping(target = "to", ignore = true)
	@Mapping(target = "lessonId", ignore = true)
	LessonAddEditDto toDto(Lesson entity);

	@Mapping(target = "course", ignore = true)
	@Mapping(target = "room", ignore = true)
	@Mapping(target = "groups", ignore = true)
	@Mapping(target = "teachers", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "students", ignore = true)
	@Mapping(target = "date", source = "from")
	void updateEntityFromDto(LessonAddEditDto dto, @MappingTarget Lesson entity);

	@Named("studentToId")
	default UUID map(Student student) {
		return student == null ? null : student.getId();
	}
}

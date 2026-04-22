package spring.mapper;

import org.mapstruct.Mapper;

import spring.dto.TeacherReadDto;
import spring.model.Teacher;

@Mapper(componentModel = "spring", uses = { UserReadMapper.class, LessonReadMapper.class, CourseReadMapper.class,
		DepartmentReadMapper.class })
public interface TeacherReadMapper {

	TeacherReadDto toDto(Teacher teacher);

}

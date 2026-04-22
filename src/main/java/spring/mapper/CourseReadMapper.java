package spring.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import spring.dto.CourseReadDto;
import spring.model.Course;

@Mapper(componentModel = "spring", uses = { GroupReadMapper.class, DepartmentReadMapper.class })
public interface CourseReadMapper {

	CourseReadDto toDto(Course course);

	List<CourseReadDto> toDtoList(List<Course> courses);

}

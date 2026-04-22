package spring.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import spring.dto.CourseAddEditDto;
import spring.model.Course;

@Mapper(componentModel = "spring")
public interface CourseAddEditMapper {

	@Mapping(target = "department", ignore = true)
	@Mapping(target = "groups", ignore = true)
	@Mapping(target = "enrollments", ignore = true)
	@Mapping(target = "teachers", ignore = true)
	@Mapping(target = "lessons", ignore = true)
	@Mapping(target = "id", ignore = true)
	Course toEntity(CourseAddEditDto dto);

	@InheritInverseConfiguration(name = "toEntity")
	@Mapping(target = "departmentId", source = "department.id")
	@Mapping(target = "groupId", ignore = true)
	CourseAddEditDto toDto(Course entity);

	@Mapping(target = "department", ignore = true)
	@Mapping(target = "groups", ignore = true)
	@Mapping(target = "enrollments", ignore = true)
	@Mapping(target = "teachers", ignore = true)
	@Mapping(target = "lessons", ignore = true)
	@Mapping(target = "id", ignore = true)
	void updateEntityFromDto(CourseAddEditDto dto, @MappingTarget Course entity);

}

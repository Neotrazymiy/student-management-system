package spring.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import spring.dto.DepartmentAddEditDto;
import spring.model.Department;

@Mapper(componentModel = "spring")
public interface DepartmentAddEditMapper {

	@Mapping(target = "faculty", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "courses", ignore = true)
	@Mapping(target = "groups", ignore = true)
	@Mapping(target = "teachers", ignore = true)
	Department toEntity(DepartmentAddEditDto dto);

	@InheritInverseConfiguration(name = "toEntity")
	@Mapping(target = "facultyId", source = "faculty.id")
	DepartmentAddEditDto toDto(Department entity);

	@Mapping(target = "faculty", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "courses", ignore = true)
	@Mapping(target = "groups", ignore = true)
	@Mapping(target = "teachers", ignore = true)
	void updateEntityFromDto(DepartmentAddEditDto dto, @MappingTarget Department entity);
}

package spring.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import spring.dto.GroupAddEditDto;
import spring.model.Group;

@Mapper(componentModel = "spring")
public interface GroupAddEditMapper {

	@Mapping(target = "department", ignore = true)
	@Mapping(target = "courses", ignore = true)
	@Mapping(target = "lessons", ignore = true)
	@Mapping(target = "students", ignore = true)
	@Mapping(target = "id", ignore = true)
	Group toEntity(GroupAddEditDto dto);

	@InheritInverseConfiguration(name = "toEntity")
	@Mapping(target = "departmentId", source = "department.id")
	GroupAddEditDto toDto(Group entity);

	@Mapping(target = "department", ignore = true)
	@Mapping(target = "courses", ignore = true)
	@Mapping(target = "lessons", ignore = true)
	@Mapping(target = "students", ignore = true)
	@Mapping(target = "id", ignore = true)
	void updateEntityFromDto(GroupAddEditDto dto, @MappingTarget Group entity);

}

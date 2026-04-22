package spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import spring.dto.RoleAddEditDto;
import spring.model.Role;

@Mapper(componentModel = "spring")
public interface RoleAddEditMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "users", ignore = true)
	Role toEntity(RoleAddEditDto dto);

	RoleAddEditDto toDto(Role entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "users", ignore = true)
	void updateEntityFromDto(RoleAddEditDto dto, @MappingTarget Role entity);

}

package spring.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import spring.dto.RoleReadDto;
import spring.model.Role;

@Mapper(componentModel = "spring")
public interface RoleReadMapper {

	RoleReadDto toDto(Role role);

	List<RoleReadDto> toDtoList(List<Role> roles);

}

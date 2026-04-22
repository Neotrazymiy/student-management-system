package spring.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import spring.dto.UserReadDto;
import spring.model.User;

@Mapper(componentModel = "spring", uses = RoleReadMapper.class)
public interface UserReadMapper {

	UserReadDto toDto(User user);

	List<UserReadDto> toDtoList(List<User> users);

}

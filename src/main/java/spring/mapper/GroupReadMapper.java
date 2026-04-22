package spring.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import spring.dto.GroupReadDto;
import spring.model.Group;

@Mapper(componentModel = "spring", uses = DepartmentReadMapper.class)
public interface GroupReadMapper {

	GroupReadDto toDto(Group group);

	List<GroupReadDto> toDtoList(List<GroupReadDto> groups);
}

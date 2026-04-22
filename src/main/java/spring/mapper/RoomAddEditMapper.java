package spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import spring.dto.RoomAddEditDto;
import spring.model.Room;

@Mapper(componentModel = "spring")
public interface RoomAddEditMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "lessons", ignore = true)
	Room toEntity(RoomAddEditDto dto);

	RoomAddEditDto toDto(Room entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "lessons", ignore = true)
	void updateEntityFromDto(RoomAddEditDto dto, @MappingTarget Room entity);
}

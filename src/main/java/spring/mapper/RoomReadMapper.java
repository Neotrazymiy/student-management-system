package spring.mapper;

import org.mapstruct.Mapper;

import spring.dto.RoomReadDto;
import spring.model.Room;

@Mapper(componentModel = "spring")
public interface RoomReadMapper {

	RoomReadDto toDto(Room room);
}

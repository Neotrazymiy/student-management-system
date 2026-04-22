package spring.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import spring.dto.RoomAddEditDto;
import spring.dto.RoomReadDto;
import spring.exception.DeleteException;
import spring.mapper.RoomAddEditMapper;
import spring.mapper.RoomReadMapper;
import spring.model.Room;
import spring.repository.RoomRepository;

@Service
@AllArgsConstructor
public class RoomService {

	private final RoomRepository roomRepository;
	private final RoomAddEditMapper roomAddEditMapper;
	private final RoomReadMapper roomReadMapper;

	@Transactional
	public RoomReadDto addRoom(RoomAddEditDto room) {
		return Optional.of(room).map(roomAddEditMapper::toEntity).map(roomRepository::save).map(roomReadMapper::toDto)
				.orElseThrow(() -> new RuntimeException());
	}

	@Transactional(readOnly = true)
	public Optional<RoomReadDto> getRoomById(UUID id) {
		return Optional.ofNullable(roomRepository.findById(id).map(roomReadMapper::toDto)
				.orElseThrow(() -> new RuntimeException("Такого id нет. " + id)));
	}

	@Transactional(readOnly = true)
	public Optional<RoomReadDto> getRoomByName(String number) {
		return roomRepository.findByNumber(number).map(roomReadMapper::toDto);
	}

	@Transactional(readOnly = true)
	public List<RoomReadDto> getAllRooms() {
		return roomRepository.findAll().stream().map(roomReadMapper::toDto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public Page<RoomReadDto> getAllPageRooms(Pageable pageable) {
		return roomRepository.findAll(pageable).map(roomReadMapper::toDto);
	}

	@Transactional
	public Optional<RoomReadDto> updateRoom(UUID id, RoomAddEditDto updateRoom) {
		return roomRepository.findById(id).map(entity -> {
			if (entity.getNumber().equals(updateRoom.getNumber())) {
				return entity;
			}
			roomAddEditMapper.updateEntityFromDto(updateRoom, entity);
			return roomRepository.saveAndFlush(entity);
		}).map(roomReadMapper::toDto);
	}

	@Transactional
	public boolean deleteRoomById(UUID id) {
		Room room = roomRepository.findById(id)
				.orElseThrow(() -> new DeleteException("Room с id " + id + " не найден."));
		if (!room.getLessons().isEmpty()) {
			throw new DeleteException("Удалите сначало сущность(и) Lesson, этого объекта Room.");
		}
		roomRepository.delete(room);
		roomRepository.flush();
		return true;
	}

}

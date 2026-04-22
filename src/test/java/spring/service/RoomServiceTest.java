package spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import spring.auxiliaryObjects.CreateObjects;
import spring.dto.RoomAddEditDto;
import spring.dto.RoomReadDto;
import spring.mapper.RoomAddEditMapper;
import spring.mapper.RoomReadMapper;
import spring.model.Room;
import spring.repository.RoomRepository;

@SpringBootTest
class RoomServiceTest {

	@Autowired
	private RoomService roomService;

	@Autowired
	private RoomAddEditMapper roomAddEditMapper;

	@Autowired
	private RoomReadMapper roomReadMapper;

	@MockBean
	private RoomRepository roomRepository;

	private static final String NUMBER = "namenamename";
	private CreateObjects createObjects = new CreateObjects();

	@Test
	void testAddRoom() {
		Room room = createObjects.createRoom();
		RoomAddEditDto roomEditDto = createObjects.createRoomEditDto();

		when(roomRepository.save(any(Room.class))).thenReturn(room);

		RoomReadDto result = roomService.addRoom(roomEditDto);

		assertThat(result).isNotNull();
		assertTrue(NUMBER.equals(result.getNumber()));
		assertThat(result.getNumber()).isEqualTo(NUMBER);

		verify(roomRepository).save(any(Room.class));
	}

	@Test
	void testGetRoomById_exists() {
		Room room = createObjects.createRoom();

		when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));

		RoomReadDto result = roomService.getRoomById(room.getId()).get();

		assertTrue(NUMBER.equals(result.getNumber()));

		verify(roomRepository).findById(room.getId());
	}

	@Test
	void testGetRoomById_NotExists() {
		UUID random = UUID.randomUUID();

		when(roomRepository.findById(random)).thenReturn(Optional.empty());
		RuntimeException exception = assertThrows(RuntimeException.class, () -> roomService.getRoomById(random));
		assertTrue(exception.getMessage().equals("Такого id нет. " + random));
	}

	@Test
	void testGetRoomByNumber() {
		Room room = createObjects.createRoom();
		RoomAddEditDto roomEditDto = createObjects.createRoomEditDto();

		when(roomRepository.findByNumber(room.getNumber())).thenReturn(Optional.of(room));

		RoomReadDto result = roomService.getRoomByName(roomEditDto.getNumber()).get();

		assertTrue(NUMBER.equals(result.getNumber()));

		verify(roomRepository).findByNumber(roomEditDto.getNumber());
	}

	@Test
	void testGetAllRoom() {
		Room room = createObjects.createRoom();

		when(roomRepository.findAll()).thenReturn(Arrays.asList(room));

		List<RoomReadDto> result = roomService.getAllRooms();

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertTrue(1 == result.size());
		assertTrue(NUMBER.equals(result.get(0).getNumber()));

		verify(roomRepository).findAll();
	}

	@Test
	void testGetAllPageRooms() {
		Room room = createObjects.createRoom();

		Pageable pageable = PageRequest.of(0, 1);
		Page<Room> mockPage = new PageImpl<>(Arrays.asList(room), pageable, 1);

		when(roomRepository.findAll(pageable)).thenReturn(mockPage);

		Page<RoomReadDto> page = roomService.getAllPageRooms(pageable);

		assertThat(page).isNotNull();
		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(page.getContent().get(0).getNumber()).isEqualTo(NUMBER);

		verify(roomRepository).findAll(pageable);
	}

	@Test
	void testUpdateRoom() {
		Room room = createObjects.createRoom();
		RoomAddEditDto roomEditDto = createObjects.createRoomEditDto();
		roomEditDto.setNumber(NUMBER + NUMBER);

		when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
		when(roomRepository.saveAndFlush(room)).thenReturn(room);

		RoomReadDto result = roomService.updateRoom(room.getId(), roomEditDto).get();

		assertEquals((NUMBER + NUMBER), (result.getNumber()));
		assertTrue((NUMBER + NUMBER).equals(result.getNumber()));

		verify(roomRepository).findById(room.getId());
		verify(roomRepository).saveAndFlush(room);
	}

	@Test
	void testDeleteRoomById() {
		Room room = createObjects.createRoom();

		when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));

		assertTrue(roomService.deleteRoomById(room.getId()));

		verify(roomRepository).findById(room.getId());
		verify(roomRepository).delete(room);
		verify(roomRepository).flush();
	}
}

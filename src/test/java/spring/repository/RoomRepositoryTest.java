package spring.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import spring.model.Room;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = { "/sql/clear_tables.sql", "/sql/V1_schema.sql", "/sql/V2_sample_data.sql" })
public class RoomRepositoryTest {

	@Autowired
	private RoomRepository roomRepository;

	private static final String ROOM_NUMBER_ONE_ORIGINAL = "Room 101";
	private static final String ROOM_NUMBER_TWO_ORIGINAL = "Room 102";
	private static final String ROOM_NUMBER_THREE = "Room Three";
	private static final UUID ROOM_ID_NOT_EXISTING = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private static final UUID ROOM_ID_ONE = UUID.fromString("00000000-0000-0000-0000-000000000051");

	@Test
	void testAddRoom() {
		Room room = roomRepository.findByNumber(ROOM_NUMBER_ONE_ORIGINAL).get();
		room.setNumber(ROOM_NUMBER_THREE);
		assertTrue(roomRepository.save(room).getNumber().equals(ROOM_NUMBER_THREE));
	}

	@Test
	void testGetRoomById_exists() {
		assertTrue(roomRepository.findById(ROOM_ID_ONE).get().getNumber().equals(ROOM_NUMBER_ONE_ORIGINAL));
	}

	@Test
	void testGetRoomById_NotExists() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> roomRepository.findById(ROOM_ID_NOT_EXISTING).map(Room::getNumber).orElseThrow(() -> {
					throw new RuntimeException("Такого id нет. " + ROOM_ID_NOT_EXISTING);
				}));
		assertTrue(exception.getMessage().contains("Такого id нет. " + ROOM_ID_NOT_EXISTING));
	}

	@Test
	void testGetRoomByNumber() {
		assertTrue(roomRepository.findByNumber(ROOM_NUMBER_ONE_ORIGINAL).get().getNumber()
				.equals(ROOM_NUMBER_ONE_ORIGINAL));
	}

	@Test
	void testGetAllRoom() {
		List<Room> universities = roomRepository.findAll();
		assertNotNull(universities);
		assertFalse(universities.isEmpty());
		assertTrue(2 == universities.size());
	}

	@Test
	void testGetAllPageRoom() {
		Pageable pageable = PageRequest.of(0, 2);
		Page<Room> page = roomRepository.findAll(pageable);

		assertThat(page).isNotNull();
		assertThat(page.getContent()).hasSize(2);
		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent().get(0).getNumber()).contains(ROOM_NUMBER_ONE_ORIGINAL);
		assertThat(page.getContent().get(1).getNumber()).contains(ROOM_NUMBER_TWO_ORIGINAL);
	}

	@Test
	@Transactional
	void testDeleteRoomById() {
		Room room = roomRepository.findByNumber(ROOM_NUMBER_ONE_ORIGINAL).get();
		room.setNumber(ROOM_NUMBER_THREE);
		room = roomRepository.save(room);
		roomRepository.deleteById(room.getId());
		assertFalse(roomRepository.existsById(room.getId()));
	}

}

package spring.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import spring.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {

	Optional<Room> findByNumber(String number);

	@Override
	Page<Room> findAll(Pageable pageable);
}

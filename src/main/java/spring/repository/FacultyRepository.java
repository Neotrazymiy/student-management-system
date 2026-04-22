package spring.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import spring.model.Faculty;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, UUID> {

	Optional<Faculty> findByName(String name);
}

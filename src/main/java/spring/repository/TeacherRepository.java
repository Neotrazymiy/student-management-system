package spring.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import spring.model.Teacher;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {

	Optional<Teacher> findByUserFirstNameAndUserLastName(String firstName, String lastName);

	@Override
	Page<Teacher> findAll(Pageable pageable);

	Optional<Teacher> findByUserId(UUID userId);

	Optional<Teacher> findByUserUserName(String userName);
}

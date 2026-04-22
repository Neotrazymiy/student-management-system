package spring.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import spring.model.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

	Optional<Course> findByCourseName(String courseName);

	@Override
	Page<Course> findAll(Pageable pageable);
}

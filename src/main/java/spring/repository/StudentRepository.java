package spring.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import spring.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {

	Optional<Student> findByUserFirstNameAndUserLastName(String firstName, String lastName);

	@Override
	Page<Student> findAll(Pageable pageable);

	Optional<Student> findByUserId(UUID userId);

	Page<Student> findAllByGroup_Id(Pageable pageable, UUID groupId);

	Optional<Student> findByUserUserName(String userName);

	@Query("select s from Student s where s.id not in (select st.id from Lesson ls JOIN ls.students st where ls.id in :lessonIds)")
	List<Student> findStudentsNotInLessons(List<UUID> lessonIds);

	List<Student> findByIdIn(List<UUID> studentIds);

	Page<Student> findByIdIn(List<UUID> studentIds, Pageable pageable);
}

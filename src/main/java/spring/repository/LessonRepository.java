package spring.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import spring.model.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID>, JpaSpecificationExecutor<Lesson> {

	Optional<Lesson> findByTeachersUserUserNameAndDateAndStartTime(String userName, LocalDate date,
			LocalTime startTime);

	@Override
	Page<Lesson> findAll(Pageable pageable);

	@Query("SELECT DISTINCT l FROM Lesson l JOIN l.groups g WHERE g.id=:groupId")
	List<Lesson> findAllByGroups_Id(UUID groupId);

	List<Lesson> findByDateAndStartTime(LocalDate date, LocalTime time);

	List<Lesson> findByIdIn(List<UUID> uuids);
}

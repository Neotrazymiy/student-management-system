package spring.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import spring.model.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {

	Optional<Group> findByName(String name);

	@Override
	Page<Group> findAll(Pageable pageable);

	List<Group> findByIdIn(List<UUID> groupIds);

	Page<Group> findByIdIn(List<UUID> groupIds, Pageable pageable);

	@Query("select g from Group g where g.id not in (select gr.id from Lesson ls JOIN ls.groups gr where ls.id in :lessonIds)")
	List<Group> findGroupsNotInLessons(List<UUID> lessonIds);
}

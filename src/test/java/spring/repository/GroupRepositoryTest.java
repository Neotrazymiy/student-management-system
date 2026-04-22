package spring.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
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

import spring.model.Group;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = { "/sql/clear_tables.sql", "/sql/V1_schema.sql", "/sql/V2_sample_data.sql" })
class GroupRepositoryTest {

	@Autowired
	private GroupRepository groupRepository;

	private static final String GROUP_NAME_ONE_ORIGINAL = "Group A";
	private static final String GROUP_NAME_TWO_ORIGINAL = "Group B";
	private static final String GROUP_NAME_THREE = "Group Three";
	private static final UUID GROUP_ID_NOT_EXISTING = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private static final UUID GROUP_ID_ONE = UUID.fromString("00000000-0000-0000-0000-000000000041");

	@Test
	void testAddGroup() {
		Group group = groupRepository.findByName(GROUP_NAME_ONE_ORIGINAL).get();
		group.setName(GROUP_NAME_THREE);
		assertTrue(groupRepository.save(group).getName().equals(GROUP_NAME_THREE));
	}

	@Test
	void testGetGroupById_exists() {
		assertTrue(groupRepository.findById(GROUP_ID_ONE).get().getName().equals(GROUP_NAME_ONE_ORIGINAL));
	}

	@Test
	void testGetGroupById_NotExists() {
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> groupRepository.findById(GROUP_ID_NOT_EXISTING).map(Group::getName).orElseThrow(() -> {
					throw new RuntimeException("Такого id нет. " + GROUP_ID_NOT_EXISTING);
				}));
		assertTrue(exception.getMessage().contains("Такого id нет. " + GROUP_ID_NOT_EXISTING));
	}

	@Test
	void testGetGroupByName() {
		assertTrue(groupRepository.findByName(GROUP_NAME_ONE_ORIGINAL).get().getName().equals(GROUP_NAME_ONE_ORIGINAL));
	}

	@Test
	void testGetAllGroup() {
		List<Group> universities = groupRepository.findAll();
		assertNotNull(universities);
		assertFalse(universities.isEmpty());
		assertTrue(2 == universities.size());
	}

	@Test
	void testGetAllPageGroup() {
		Pageable pageable = PageRequest.of(0, 2);
		Page<Group> page = groupRepository.findAll(pageable);

		assertThat(page).isNotNull();
		assertThat(page.getContent()).hasSize(2);
		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent().get(0).getName()).contains(GROUP_NAME_ONE_ORIGINAL);
		assertThat(page.getContent().get(1).getName()).contains(GROUP_NAME_TWO_ORIGINAL);
	}

	@Test
	@Transactional
	void testDeleteGroupById() {
		Group group = groupRepository.findByName(GROUP_NAME_ONE_ORIGINAL).get();
		group.setName(GROUP_NAME_THREE);
		group = groupRepository.save(group);
		groupRepository.deleteById(group.getId());
		assertFalse(groupRepository.existsById(group.getId()));
	}

	@Test
	void getGroupByIds() {
		List<UUID> uuids = new ArrayList<>();
		uuids.add(GROUP_ID_ONE);
		List<Group> groups = groupRepository.findAllById(uuids);
		assertTrue(groups.size() == 1);
		assertTrue(groups.get(0).getId().equals(groupRepository.findById(GROUP_ID_ONE).get().getId()));
	}

	@Test
	void getPageGroupByIds() {
		List<UUID> uuids = new ArrayList<>();
		uuids.add(GROUP_ID_ONE);
		Page<Group> page = groupRepository.findByIdIn(uuids, PageRequest.of(0, 2));

		assertThat(page).isNotNull();
		assertThat(page.getContent()).hasSize(1);
		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(page.getContent().get(0).getName()).contains(GROUP_NAME_ONE_ORIGINAL);
	}

	@Test
	void getGroupsNotInLessons() {
		UUID lessonId = UUID.fromString("00000000-0000-0000-0000-000000000071");
		List<UUID> uuids = new ArrayList<>();
		uuids.add(lessonId);
		List<Group> groups = groupRepository.findGroupsNotInLessons(uuids);

		assertFalse(groups.get(0).getLessons().stream().anyMatch(l -> l.getId().equals(lessonId)));
	}
}

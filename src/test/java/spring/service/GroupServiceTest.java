/**
 *
 */
package spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import spring.dto.GroupAddEditDto;
import spring.dto.GroupReadDto;
import spring.mapper.GroupAddEditMapper;
import spring.mapper.GroupReadMapper;
import spring.model.Group;
import spring.repository.DepartmentRepository;
import spring.repository.GroupRepository;

@SpringBootTest
class GroupServiceTest {

	@Autowired
	private GroupService groupService;

	@Autowired
	private GroupAddEditMapper groupAddEditMapper;

	@Autowired
	private GroupReadMapper groupReadMapper;

	@MockBean
	private GroupRepository groupRepository;

	@MockBean
	private DepartmentRepository departmentRepository;

	private static final String NAME = "namenamename";
	private CreateObjects createObjects = new CreateObjects();

	@Test
	void testAddGroup() {
		Group group = createObjects.createGroup();
		GroupAddEditDto groupEditDto = createObjects.createGroupEditDto();

		when(departmentRepository.findById(groupEditDto.getDepartmentId()))
				.thenReturn(Optional.of(group.getDepartment()));
		when(groupRepository.save(any(Group.class))).thenReturn(group);

		GroupReadDto result = groupService.addGroup(groupEditDto);

		assertThat(result).isNotNull();
		assertTrue(NAME.equals(result.getName()));
		assertThat(result.getName()).isEqualTo(NAME);

		verify(groupRepository).save(any(Group.class));
		verify(departmentRepository).findById(groupEditDto.getDepartmentId());
	}

	@Test
	void testGetGroupById_exists() {
		Group group = createObjects.createGroup();

		when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

		GroupReadDto result = groupService.getGroupById(group.getId()).get();

		assertTrue(NAME.equals(result.getName()));

		verify(groupRepository).findById(group.getId());
	}

	@Test
	void testGetGroupById_NotExists() {
		UUID random = UUID.randomUUID();

		when(groupRepository.findById(random)).thenReturn(Optional.empty());
		RuntimeException exception = assertThrows(RuntimeException.class, () -> groupService.getGroupById(random));
		assertTrue(exception.getMessage().equals("Такого id нет. " + random));
	}

	@Test
	void testGetGroupByName() {
		Group group = createObjects.createGroup();
		GroupAddEditDto groupEditDto = createObjects.createGroupEditDto();

		when(groupRepository.findByName(group.getName())).thenReturn(Optional.of(group));

		GroupReadDto result = groupService.getGroupByName(groupEditDto.getName()).get();

		assertTrue(NAME.equals(result.getName()));

		verify(groupRepository).findByName(groupEditDto.getName());
	}

	@Test
	void testGetGroupByIds() {
		Group group = createObjects.createGroup();
		List<Group> groups = new ArrayList<>();
		groups.add(group);
		List<UUID> uuids = new ArrayList<>();
		uuids.add(UUID.randomUUID());

		when(groupRepository.findByIdIn(uuids)).thenReturn(groups);

		List<GroupReadDto> groupReadDtos = groupService.getGroupsByIds(uuids);

		assertEquals(group.getId(), groupReadDtos.get(0).getId());

		verify(groupRepository).findByIdIn(uuids);
	}

	@Test
	void testGetPageGroupByIds() {
		Pageable pageable = PageRequest.of(0, 10);
		Group group = createObjects.createGroup();
		List<Group> groups = new ArrayList<>();
		groups.add(group);
		List<UUID> uuids = new ArrayList<>();
		uuids.add(UUID.randomUUID());
		Page<Group> page = new PageImpl<>(groups);

		when(groupRepository.findByIdIn(uuids, pageable)).thenReturn(page);

		Page<GroupReadDto> groupReadDtos = groupService.getPageGroupsByIds(uuids, pageable);

		assertEquals(group.getId(), groupReadDtos.getContent().get(0).getId());

		verify(groupRepository).findByIdIn(uuids, pageable);
	}

	@Test
	void testGetPageGroupNullByIds() {
		Pageable pageable = PageRequest.of(0, 10);
		List<UUID> uuids = new ArrayList<>();

		Page<GroupReadDto> groupReadDtos = groupService.getPageGroupsByIds(uuids, pageable);

		assertTrue(groupReadDtos.isEmpty());

		verify(groupRepository, never()).findByIdIn(uuids, pageable);
	}

	@Test
	void testGetAllGroup() {
		Group group = createObjects.createGroup();

		when(groupRepository.findAll()).thenReturn(Arrays.asList(group));

		List<GroupReadDto> result = groupService.getAllGroups();

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertTrue(1 == result.size());
		assertTrue(NAME.equals(result.get(0).getName()));

		verify(groupRepository).findAll();
	}

	@Test
	void testGetAllPageGroups() {
		Group group = createObjects.createGroup();

		Pageable pageable = PageRequest.of(0, 1);
		Page<Group> mockPage = new PageImpl<>(Arrays.asList(group), pageable, 1);

		when(groupRepository.findAll(pageable)).thenReturn(mockPage);

		Page<GroupReadDto> page = groupService.getAllPageGroups(pageable);

		assertThat(page).isNotNull();
		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(page.getContent().get(0).getName()).isEqualTo(NAME);

		verify(groupRepository).findAll(pageable);
	}

	@Test
	void testUpdateGroup() {
		Group group = createObjects.createGroup();
		GroupAddEditDto groupEditDto = createObjects.createGroupEditDto();
		groupEditDto.setDepartmentId(UUID.randomUUID());
		groupEditDto.setName(NAME + NAME);

		when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
		when(departmentRepository.findById(groupEditDto.getDepartmentId()))
				.thenReturn(Optional.of(group.getDepartment()));
		when(groupRepository.save(group)).thenReturn(group);

		GroupReadDto result = groupService.updateGroup(group.getId(), groupEditDto).get();

		assertEquals((NAME + NAME), (result.getName()));
		assertTrue((NAME + NAME).equals(result.getName()));

		verify(groupRepository).findById(group.getId());
		verify(departmentRepository).findById(groupEditDto.getDepartmentId());
		verify(groupRepository).save(group);
	}

	@Test
	void testDeleteGroupById() {
		Group group = createObjects.createGroup();

		when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
		groupService.deleteGroupById(group.getId());

		verify(groupRepository).findById(group.getId());
		verify(groupRepository).delete(group);
		verify(groupRepository).flush();
	}

	@Test
	void testGetGroupsWithoutLesson() {
		List<UUID> uuids = new ArrayList<>();
		uuids.add(UUID.randomUUID());
		Group group = createObjects.createGroup();
		List<Group> groups = new ArrayList<>();
		groups.add(group);

		when(groupRepository.findGroupsNotInLessons(uuids)).thenReturn(groups);

		List<GroupReadDto> dtos = groupService.getGroupsWithoutLesson(uuids);

		assertEquals(group.getId(), dtos.get(0).getId());

		verify(groupRepository).findGroupsNotInLessons(uuids);
	}

	@Test
	void testGetGroupsWithoutLessonNull() {
		List<UUID> uuids = new ArrayList<>();
		Group group = createObjects.createGroup();
		List<Group> groups = new ArrayList<>();
		groups.add(group);

		when(groupRepository.findAll()).thenReturn(groups);

		List<GroupReadDto> dtos = groupService.getGroupsWithoutLesson(uuids);

		assertEquals(group.getId(), dtos.get(0).getId());

		verify(groupRepository).findAll();
	}

}

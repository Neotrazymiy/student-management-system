package spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import spring.auxiliaryObjects.HelpsMethod;
import spring.dto.RoleAddEditDto;
import spring.dto.RoleReadDto;
import spring.mapper.RoleAddEditMapper;
import spring.mapper.RoleReadMapper;
import spring.model.Role;
import spring.repository.RoleRepository;
import spring.repository.UserRepository;

@SpringBootTest
class RoleServiceTest {

	@Autowired
	private RoleService roleService;

	@Autowired
	private RoleAddEditMapper roleAddEditMapper;

	@Autowired
	private RoleReadMapper roleReadMapper;

	@MockBean
	private RoleRepository roleRepository;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private HelpsMethod helpsMethod;

	private static final String NAME = "ROLE_QUEST";
	private CreateObjects createObjects = new CreateObjects();

	@Test
	void testAddRole() {
		Role role = createObjects.createRole();
		RoleAddEditDto roleEditDto = createObjects.createRoleEditDto();

		when(roleRepository.save(any(Role.class))).thenReturn(role);

		RoleReadDto result = roleService.addRole(roleEditDto);

		assertThat(result).isNotNull();
		assertTrue(NAME.equals(result.getName()));
		assertThat(result.getName()).isEqualTo(NAME);

		verify(roleRepository).save(any(Role.class));
	}

	@Test
	void testGetRoleById_exists() {
		Role role = createObjects.createRole();

		when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));

		RoleReadDto result = roleService.getRoleById(role.getId()).get();

		assertTrue(NAME.equals(result.getName()));

		verify(roleRepository).findById(role.getId());
	}

	@Test
	void testGetRoleById_NotExists() {
		UUID random = UUID.randomUUID();

		when(roleRepository.findById(random)).thenReturn(Optional.empty());
		RuntimeException exception = assertThrows(RuntimeException.class, () -> roleService.getRoleById(random));
		assertTrue(exception.getMessage().equals("Такого id нет. " + random));
	}

	@Test
	void testGetRoleByName() {
		Role role = createObjects.createRole();
		RoleAddEditDto roleEditDto = createObjects.createRoleEditDto();

		when(roleRepository.findByName(roleEditDto.getName())).thenReturn(Optional.of(role));

		RoleReadDto result = roleService.getRoleByName(roleEditDto.getName()).get();

		assertTrue(NAME.equals(result.getName()));

		verify(roleRepository).findByName(roleEditDto.getName());
	}

	@Test
	void testGetAllRole() {
		Role role = createObjects.createRole();

		when(roleRepository.findAll()).thenReturn(Arrays.asList(role));

		List<RoleReadDto> result = roleService.getAllRoles();

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertTrue(1 == result.size());
		assertTrue(NAME.equals(result.get(0).getName()));

		verify(roleRepository).findAll();
	}

	@Test
	void testGetAllPageRoles() {
		Role role = createObjects.createRole();

		Pageable pageable = PageRequest.of(0, 1);
		Page<Role> mockPage = new PageImpl<>(Arrays.asList(role), pageable, 1);

		when(roleRepository.findAll(pageable)).thenReturn(mockPage);

		Page<RoleReadDto> page = roleService.getAllPageRoles(pageable);

		assertThat(page).isNotNull();
		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(page.getContent().get(0).getName()).isEqualTo(NAME);

		verify(roleRepository).findAll(pageable);
	}

	@Test
	void testUpdateRole() {
		Role role = createObjects.createRole();
		RoleAddEditDto roleEditDto = createObjects.createRoleEditDto();
		roleEditDto.setName(NAME + NAME);

		when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));
		when(roleRepository.saveAndFlush(role)).thenReturn(role);

		RoleReadDto result = roleService.updateRole(role.getId(), roleEditDto).get();

		assertEquals((NAME), (result.getName()));
		assertTrue((NAME).equals(result.getName()));

		verify(roleRepository).findById(role.getId());
	}

	@Test
	void testDeleteRoleById() {
		Role role = createObjects.createRole();

		when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));

		roleService.deleteRoleById(role.getId());

		verify(roleRepository).findById(role.getId());
		verify(roleRepository).delete(role);
	}
}

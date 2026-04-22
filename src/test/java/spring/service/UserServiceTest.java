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
import spring.dto.UserAddEditDto;
import spring.dto.UserReadDto;
import spring.mapper.UserAddEditMapper;
import spring.mapper.UserReadMapper;
import spring.model.RoleElement;
import spring.model.User;
import spring.repository.RoleRepository;
import spring.repository.UserRepository;

@SpringBootTest
class UserServiceTest {

	@Autowired
	private UserService userService;

	@Autowired
	private UserAddEditMapper userAddEditMapper;

	@Autowired
	private UserReadMapper userReadMapper;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private RoleRepository roleRepository;

	private CreateObjects createObjects = new CreateObjects();
	private static final String USER_NAME = "namenamename";

	@Test
	void testAddUser() {
		User user = createObjects.createUser();
		UserAddEditDto userEditDto = createObjects.createUserEditDto();
		userEditDto.getRoles().clear();

		when(roleRepository.findByName(RoleElement.ROLE_QUEST.name())).thenReturn(Optional.of(user.getRoles().get(0)));
		when(userRepository.save(any(User.class))).thenReturn(user);

		UserReadDto result = userService.addUser(userEditDto);

		assertThat(result).isNotNull();
		assertTrue(RoleElement.ROLE_QUEST.name().equals(result.getRoles().get(0).getName()));
		assertThat(result.getUserName()).isEqualTo(USER_NAME);

		verify(roleRepository).findByName(RoleElement.ROLE_QUEST.name());
		verify(userRepository).save(any(User.class));
	}

	@Test
	void testGetUserById_exists() {
		User user = createObjects.createUser();

		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

		UserReadDto result = userService.getUserById(user.getId()).get();

		assertTrue(USER_NAME.equals(result.getUserName()));

		verify(userRepository).findById(user.getId());
	}

	@Test
	void testGetUserById_NotExists() {
		UUID random = UUID.randomUUID();

		when(userRepository.findById(random)).thenReturn(Optional.empty());
		RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserById(random));
		assertTrue(exception.getMessage().equals("Такого id нет. " + random));
	}

	@Test
	void testGetUserByUserName() {
		User user = createObjects.createUser();
		UserAddEditDto userEditDto = createObjects.createUserEditDto();

		when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));

		UserReadDto result = userService.getUserByName(userEditDto.getUserName()).get();

		assertTrue(USER_NAME.equals(result.getUserName()));

		verify(userRepository).findByUserName(userEditDto.getUserName());
	}

	@Test
	void testGetAllUser() {
		User user = createObjects.createUser();

		when(userRepository.findAll()).thenReturn(Arrays.asList(user));

		List<UserReadDto> result = userService.getAllUsers();

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertTrue(1 == result.size());
		assertTrue(USER_NAME.equals(result.get(0).getUserName()));

		verify(userRepository).findAll();
	}

	@Test
	void testGetAllPageUsers() {
		User user = createObjects.createUser();

		Pageable pageable = PageRequest.of(0, 1);
		Page<User> mockPage = new PageImpl<>(Arrays.asList(user), pageable, 1);

		when(userRepository.findAll(pageable)).thenReturn(mockPage);

		Page<UserReadDto> page = userService.getAllPageUsers(pageable);

		assertThat(page).isNotNull();
		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(page.getContent().get(0).getUserName()).isEqualTo(USER_NAME);

		verify(userRepository).findAll(pageable);
	}

	@Test
	void testUpdateUserWhenNotRole() {
		User user = createObjects.createUser();
		UserAddEditDto userEditDto = createObjects.createUserEditDto();
		userEditDto.getRoles().clear();

		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(roleRepository.findByName(RoleElement.ROLE_QUEST.name())).thenReturn(Optional.of(user.getRoles().get(0)));
		when(userRepository.saveAndFlush(user)).thenReturn(user);

		UserReadDto result = userService.updateUser(user.getId(), userEditDto).get();

		assertEquals(RoleElement.ROLE_QUEST.name(), result.getRoles().get(0).getName());
		assertTrue(user.getRoles().get(0).getName().equals(result.getRoles().get(0).getName()));

		verify(userRepository).findById(user.getId());
		verify(roleRepository).findByName(RoleElement.ROLE_QUEST.name());
		verify(userRepository).saveAndFlush(user);
	}

	@Test
	void testUpdateUserWhenHaveRole() {
		User user = createObjects.createUser();
		UserAddEditDto userEditDto = createObjects.createUserEditDto();

		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(roleRepository.findByName(RoleElement.ROLE_QUEST.name())).thenReturn(Optional.of(user.getRoles().get(0)));
		when(userRepository.saveAndFlush(user)).thenReturn(user);

		UserReadDto result = userService.updateUser(user.getId(), userEditDto).get();

		assertEquals(RoleElement.ROLE_QUEST.name(), result.getRoles().get(0).getName());
		assertTrue(userEditDto.getRoles().get(0).getName().equals(result.getRoles().get(0).getName()));

		verify(userRepository).findById(user.getId());
		verify(roleRepository).findByName(RoleElement.ROLE_QUEST.name());
		verify(userRepository).saveAndFlush(user);
	}

	@Test
	void testDeleteUserById() {
		User user = createObjects.createUser();

		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

		assertTrue(userService.deleteUserById(user.getId()));

		verify(userRepository).findById(user.getId());
		verify(userRepository).delete(user);
	}

}

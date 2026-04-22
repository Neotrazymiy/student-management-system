package spring.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import spring.auxiliaryObjects.CreateObjects;
import spring.config.SecurityConfig;
import spring.dto.RoleReadDto;
import spring.dto.UserAddEditDto;
import spring.dto.UserReadDto;
import spring.model.RoleElement;
import spring.service.AdminService;
import spring.service.CastomUserDetailsService;
import spring.service.RoleService;
import spring.service.UserService;

@WebMvcTest(controllers = AdminUserControlller.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class AdminUserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RoleService roleService;

	@MockBean
	private UserService userService;

	@MockBean
	private AdminService adminService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;

	private static final String NAME = "namenamename";
	private static final UUID USER_ID = UUID.randomUUID();
	private CreateObjects createObjects = new CreateObjects();

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void getUserPage() throws Exception {
		RoleReadDto roleDtoTeacher = createObjects.createRoleDto(RoleElement.ROLE_TEACHER);
		RoleReadDto roleDtoStudent = createObjects.createRoleDto(RoleElement.ROLE_STUDENT);
		RoleReadDto roleDtoQuest = createObjects.createRoleDto(RoleElement.ROLE_QUEST);

		List<RoleReadDto> roles = new ArrayList<>();
		roles.add(roleDtoTeacher);
		roles.add(roleDtoStudent);

		when(roleService.getRoleByName(RoleElement.ROLE_TEACHER.name())).thenReturn(Optional.of(roleDtoTeacher));
		when(roleService.getRoleByName(RoleElement.ROLE_STUDENT.name())).thenReturn(Optional.of(roleDtoStudent));

		UserReadDto userReadDto1 = createObjects.createUserDto(roleDtoStudent);
		UserReadDto userReadDto2 = createObjects.createUserDto(roleDtoQuest);

		List<UserReadDto> dtos = new ArrayList<>();
		dtos.add(userReadDto1);
		dtos.add(userReadDto2);

		Page<UserReadDto> page = new PageImpl<>(dtos, PageRequest.of(0, 10), 1);
		when(userService.getUsersExcludingRoles(roles, PageRequest.of(0, 10))).thenReturn(page);

		mockMvc.perform(get("/admin/admins")).andExpect(status().isOk()).andExpect(view().name("admin/users/users"))
				.andExpect(model().attributeExists("pageUser")).andExpect(model().attribute("currentPage", 0))
				.andExpect(model().attribute("pageSize", 10)).andExpect(model().attribute("totalPage", 1))
				.andExpect(model().attribute("basePath", "/admin/admins"));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void editUser() throws Exception {
		UserReadDto userReadDto = createObjects.createUserDto(new RoleReadDto());
		when(userService.getUserById(USER_ID)).thenReturn(Optional.of(userReadDto));

		mockMvc.perform(get("/admin/users/{id}", USER_ID)).andExpect(status().isOk())
				.andExpect(view().name("admin/users/edit-user")).andExpect(model().attributeExists("user"))
				.andExpect(model().attribute("user", userReadDto));

	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void editUserNotFound() throws Exception {
		when(userService.getUserById(USER_ID)).thenReturn(Optional.empty());
		mockMvc.perform(get("/admin/users/{id}", USER_ID).param("page", "1").param("size", "10"))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
				.andExpect(result -> assertEquals(HttpStatus.NOT_FOUND,
						((ResponseStatusException) result.getResolvedException()).getStatus()));
		verify(userService).getUserById(USER_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateUser() throws Exception {
		UserReadDto userReadDto = createObjects.createUserDto(new RoleReadDto());
		when(userService.updateUser(eq(USER_ID), any(UserAddEditDto.class))).thenReturn(Optional.of(userReadDto));

		mockMvc.perform(post("/admin/users/{id}/update", USER_ID).with(csrf()).param("page", "1").param("size", "10")
				.param("userName", NAME).param("firstName", "йцу").param("lastName", "йцу"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/admins?page=1&size=10"));

		ArgumentCaptor<UserAddEditDto> argumentCaptor = ArgumentCaptor.forClass(UserAddEditDto.class);

		verify(userService).updateUser(eq(USER_ID), argumentCaptor.capture());
		UserAddEditDto userAddEditDto = argumentCaptor.getValue();

		assertThat(userAddEditDto.getUserName()).isEqualTo(NAME);
		assertThat(userAddEditDto.getFirstName()).isEqualTo("йцу");
		assertThat(userAddEditDto.getLastName()).isEqualTo("йцу");
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateUserNotFaund() throws Exception {
		when(userService.updateUser(eq(USER_ID), any(UserAddEditDto.class))).thenReturn(Optional.empty());
		mockMvc.perform(post("/admin/users/{id}/update", USER_ID).with(csrf()).param("page", "1").param("size", "10"))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
				.andExpect(result -> assertEquals(HttpStatus.NOT_FOUND,
						((ResponseStatusException) result.getResolvedException()).getStatus()));
		verify(userService).updateUser(eq(USER_ID), any(UserAddEditDto.class));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void userNew() throws Exception {
		UserAddEditDto dto = new UserAddEditDto();
		mockMvc.perform(get("/admin/users/new").param("page", "1").param("size", "10")).andExpect(status().isOk())
				.andExpect(view().name("admin/users/edit-user")).andExpect(model().attribute("user", dto));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void createUser() throws Exception {
		UserAddEditDto userAddEditDto = new UserAddEditDto();
		userAddEditDto.setUserName(NAME);
		userAddEditDto.setFirstName("йцу");
		userAddEditDto.setLastName("йцу");

		mockMvc.perform(post("/admin/users").param("page", "1").with(csrf()).param("size", "10").param("userName", NAME)
				.param("firstName", "йцу").param("lastName", "йцу")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/admins?page=1&size=10"));

		ArgumentCaptor<UserAddEditDto> argumentCaptor = ArgumentCaptor.forClass(UserAddEditDto.class);

		verify(userService).addUser(argumentCaptor.capture());

		assertThat(userAddEditDto.getUserName()).isEqualTo(NAME);
		assertThat(userAddEditDto.getFirstName()).isEqualTo("йцу");
		assertThat(userAddEditDto.getLastName()).isEqualTo("йцу");
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void newTeacher() throws Exception {
		mockMvc.perform(post("/admin/teachers/{id}/new", USER_ID).with(csrf()).param("page", "1").param("size", "10")
				.param("page", "0").param("size", "10")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/admins?page=1&size=10"));

		verify(adminService).makeTeacherWithUser(USER_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void deleteUser() throws Exception {
		when(userService.deleteUserById(USER_ID)).thenReturn(true);
		mockMvc.perform(post("/admin/users/{id}/delete", USER_ID).with(csrf()).param("page", "1").param("size", "10"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/admins?page=1&size=10"));
		verify(userService).deleteUserById(USER_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void deleteUserNotFound() throws Exception {
		when(userService.deleteUserById(USER_ID)).thenReturn(false);
		mockMvc.perform(post("/admin/users/{id}/delete", USER_ID).with(csrf()).param("page", "1").param("size", "10"))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
				.andExpect(result -> assertEquals(HttpStatus.NOT_FOUND,
						((ResponseStatusException) result.getResolvedException()).getStatus()));
		verify(userService).deleteUserById(USER_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void addAdminUser() throws Exception {
		mockMvc.perform(post("/admin/admins/{id}/new", USER_ID).with(csrf()).param("page", "1").param("size", "10"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/admins?page=1&size=10"));
		verify(adminService).addRoleAdminUser(USER_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void deleteAdmin() throws Exception {
		mockMvc.perform(post("/admin/admins/{id}/delete", USER_ID).with(csrf()).param("page", "1").param("size", "10"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/admins?page=1&size=10"));
		verify(adminService).deleteRoleByUserId(USER_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void addMethodistUser() throws Exception {
		mockMvc.perform(post("/admin/methodist/{id}/new", USER_ID).with(csrf()).param("page", "1").param("size", "10"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/admins?page=1&size=10"));
		verify(adminService).addRoleMethodistUser(USER_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "STUDENT" })
	void userFirbidden() throws Exception {
		mockMvc.perform(post("/admin/users")).andExpect(status().isForbidden());
	}

}

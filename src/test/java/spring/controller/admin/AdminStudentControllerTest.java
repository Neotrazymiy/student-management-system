package spring.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
import spring.dto.GroupReadDto;
import spring.dto.RoleIdsDto;
import spring.dto.RoleReadDto;
import spring.dto.StudentAddEditDto;
import spring.dto.StudentReadDto;
import spring.dto.UserAddEditDto;
import spring.dto.UserReadDto;
import spring.model.RoleElement;
import spring.service.AdminService;
import spring.service.CastomUserDetailsService;
import spring.service.GroupService;
import spring.service.IpBlockService;
import spring.service.RoleService;
import spring.service.StudentService;
import spring.service.UserService;

@WebMvcTest(controllers = AdminStudentController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class AdminStudentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private GroupService groupService;

	@MockBean
	private StudentService studentService;

	@MockBean
	private AdminService adminService;

	@MockBean
	private RoleService roleService;

	@MockBean
	private UserService userService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;
	
	@MockBean
	private IpBlockService ipBlockService;

	private static final UUID STUDENT_ID = UUID.randomUUID();
	private static final String NAME = "namenamename";
	private CreateObjects createObjects = new CreateObjects();

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void getPageStudent() throws Exception {
		StudentReadDto studentReadDto = createObjects
				.createStudentDto(createObjects.createUserDto(createObjects.createRoleDto(RoleElement.ROLE_STUDENT)));
		List<StudentReadDto> dtos = new ArrayList<>();
		dtos.add(studentReadDto);

		GroupReadDto groupReadDto = createObjects.createGroupDto();
		UUID groupId = groupReadDto.getId();
		List<GroupReadDto> dtos2 = new ArrayList<>();
		dtos2.add(groupReadDto);

		Page<StudentReadDto> page = new PageImpl<>(dtos);
		Page<GroupReadDto> page2 = new PageImpl<>(dtos2);

		when(studentService.getAllPageStudents(any(PageRequest.class), eq(groupId))).thenReturn(page);
		when(groupService.getAllPageGroups(any(PageRequest.class))).thenReturn(page2);

		mockMvc.perform(get("/admin/students").param("groupPage", "0").param("groupSize", "5").param("groupId",
				groupId.toString())).andExpect(status().isOk()).andExpect(view().name("admin/students/students"))
				.andExpect(model().attributeExists("pageStudent")).andExpect(model().attribute("currentPage", 0))
				.andExpect(model().attribute("pageSize", 10)).andExpect(model().attribute("totalPage", 1))
				.andExpect(model().attribute("basePath", "/admin/students"));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void editStudent() throws Exception {
		StudentReadDto studentReadDto = createObjects.createStudentDto(new UserReadDto());
		when(studentService.getStudentById(STUDENT_ID)).thenReturn(Optional.of(studentReadDto));

		mockMvc.perform(get("/admin/students/{id}", STUDENT_ID)).andExpect(status().isOk())
				.andExpect(view().name("admin/students/edit-student"))
				.andExpect(model().attribute("student", studentReadDto));

		verify(studentService).getStudentById(STUDENT_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void editStudent_NOT_FOUND() throws Exception {
		when(studentService.getStudentById(STUDENT_ID)).thenReturn(Optional.empty());

		mockMvc.perform(get("/admin/students/{id}", STUDENT_ID))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
				.andExpect(result -> assertEquals(HttpStatus.NOT_FOUND,
						((ResponseStatusException) result.getResolvedException()).getStatus()));
		verify(studentService).getStudentById(STUDENT_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateStudent() throws Exception {
		UserAddEditDto userAddEditDto = new UserAddEditDto();
		userAddEditDto.setUserName(NAME);

		StudentAddEditDto studentAddEditDto = new StudentAddEditDto();
		studentAddEditDto.setGroupId(STUDENT_ID);
		studentAddEditDto.setUser(userAddEditDto);

		mockMvc.perform(post("/admin/students/{id}/update", STUDENT_ID).with(csrf()).param("page", "1")
				.param("size", "10").param("groupId", STUDENT_ID.toString())
				.param("user.userName", studentAddEditDto.getUser().getUserName()))
				.andExpect(status().is3xxRedirection()).andExpectAll(redirectedUrl("/admin/students?page=1&size=10"));

		ArgumentCaptor<StudentAddEditDto> captor = ArgumentCaptor.forClass(StudentAddEditDto.class);
		verify(studentService).updateStudent(eq(STUDENT_ID), captor.capture());

		assertThat(studentAddEditDto.getGroupId()).isEqualTo(captor.getValue().getGroupId());
		assertThat(studentAddEditDto.getUser()).isEqualTo(captor.getValue().getUser());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void deleteStudent() throws Exception {
		mockMvc.perform(
				post("/admin/students/{id}/delete", STUDENT_ID).with(csrf()).param("page", "1").param("size", "10"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/students?page=1&size=10"));

		verify(adminService).deleteStudentAndRoleWithUser(STUDENT_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void editGroup() throws Exception {
		StudentReadDto studentDto = createObjects.createStudentDto(createObjects.createUserDto(new RoleReadDto()));
		List<GroupReadDto> groups = new ArrayList<>();

		groups.add(createObjects.createGroupDto());
		groups.add(createObjects.createGroupDto());

		when(studentService.getStudentById(STUDENT_ID)).thenReturn(Optional.of(studentDto));
		when(groupService.getAllGroups()).thenReturn(groups);

		mockMvc.perform(get("/admin/students/{id}/group-edit", STUDENT_ID).param("returnUrl", "/url"))
				.andExpect(status().isOk()).andExpect(view().name("admin/students/edit-group"))
				.andExpect(model().attribute("student", studentDto)).andExpect(model().attribute("groups", groups))
				.andExpect(model().attribute("returnUrl", "/url"));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateStudentGroup() throws Exception {
		UUID groupId = UUID.randomUUID();

		mockMvc.perform(post("/admin/students/{id}/group-edit", STUDENT_ID).with(csrf()).param("returnUrl", "/url")
				.param("groupId", groupId.toString())).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/url"));

		verify(adminService).makeUserStudent(STUDENT_ID, groupId);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void editRole() throws Exception {
		UserReadDto userReadDto = createObjects.createUserDto(createObjects.createRoleDto(RoleElement.ROLE_STUDENT));
		StudentReadDto studentReadDto = createObjects.createStudentDto(userReadDto);
		List<RoleReadDto> roles = userReadDto.getRoles();

		when(userService.getUserById(STUDENT_ID)).thenReturn(Optional.of(userReadDto));
		when(studentService.getStudentByUserId(STUDENT_ID)).thenReturn(studentReadDto);
		when(roleService.getAllRoles()).thenReturn(roles);

		mockMvc.perform(get("/admin/students/{id}/roles", STUDENT_ID).param("returnUrl", "/url"))
				.andExpect(status().isOk()).andExpect(view().name("admin/students/edit-roleStudent"))
				.andExpect(model().attribute("user", userReadDto))
				.andExpect(model().attribute("student", studentReadDto)).andExpect(model().attribute("role", roles));

		verify(userService).getUserById(STUDENT_ID);
		verify(studentService).getStudentByUserId(STUDENT_ID);
		verify(roleService).getAllRoles();
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateStudentRoles_redirectsCorrectly() throws Exception {
		List<UUID> list = new ArrayList<>();
		list.add(STUDENT_ID);
		RoleIdsDto roleIdsDto = new RoleIdsDto(list);
		UserReadDto user = createObjects.createUserDto(new RoleReadDto());

		when(adminService.updateRoles(eq(STUDENT_ID), anyList())).thenReturn(user);
		when(adminService.checkChooseRole(user, RoleElement.ROLE_STUDENT)).thenReturn(true);

		mockMvc.perform(post("/admin/students/{id}/roles", STUDENT_ID).with(csrf()).param("returnUrl", "/dashboard")
				.param("roleIds", roleIdsDto.getRoleIds().get(0).toString())).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/dashboard"));

		verify(adminService).updateRoles(STUDENT_ID, roleIdsDto.getRoleIds());
		verify(adminService).checkChooseRole(user, RoleElement.ROLE_STUDENT);
	}

}

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
import spring.dto.RoleIdsDto;
import spring.dto.RoleReadDto;
import spring.dto.TeacherAddEditDto;
import spring.dto.TeacherReadDto;
import spring.dto.UserAddEditDto;
import spring.dto.UserReadDto;
import spring.model.RoleElement;
import spring.service.AdminService;
import spring.service.CastomUserDetailsService;
import spring.service.IpBlockService;
import spring.service.RoleService;
import spring.service.TeacherService;
import spring.service.UserService;

@WebMvcTest(controllers = AdminTeacherController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class AdminTeacherControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TeacherService teacherService;

	@MockBean
	private RoleService roleService;

	@MockBean
	private AdminService adminService;

	@MockBean
	private UserService userService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;
	
	@MockBean
	private IpBlockService ipBlockService;

	private static final String NAME = "namenamename";
	private static final UUID TEACHER_ID = UUID.randomUUID();
	private CreateObjects createObjects = new CreateObjects();

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void getPageTeacher() throws Exception {
		TeacherReadDto teacherReadDto = createObjects
				.createTeacherDto(createObjects.createUserDto(createObjects.createRoleDto(RoleElement.ROLE_TEACHER)));
		List<TeacherReadDto> dtos = new ArrayList<>();
		dtos.add(teacherReadDto);
		Page<TeacherReadDto> page = new PageImpl<>(dtos);

		when(teacherService.getAllPageTeachers(any(PageRequest.class))).thenReturn(page);

		mockMvc.perform(get("/admin/teachers")).andExpect(status().isOk())
				.andExpect(view().name("admin/teachers/teachers")).andExpect(model().attributeExists("pageTeacher"))
				.andExpect(model().attribute("currentPage", 0)).andExpect(model().attribute("pageSize", 10))
				.andExpect(model().attribute("totalPage", 1))
				.andExpect(model().attribute("basePath", "/admin/teachers"));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void editTeacher() throws Exception {
		TeacherReadDto teacherReadDto = new TeacherReadDto();
		when(teacherService.getTeacherById(TEACHER_ID)).thenReturn(Optional.of(teacherReadDto));
		mockMvc.perform(get("/admin/teachers/{id}", TEACHER_ID)).andExpect(status().isOk())
				.andExpect(view().name("admin/teachers/edit-teacher"))
				.andExpect(model().attribute("teacher", teacherReadDto));
		verify(teacherService).getTeacherById(TEACHER_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void editTeacher_NOT_FOUND() throws Exception {
		when(teacherService.getTeacherById(TEACHER_ID)).thenReturn(Optional.empty());
		mockMvc.perform(get("/admin/teachers/{id}", TEACHER_ID))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
				.andExpect(result -> assertEquals(HttpStatus.NOT_FOUND,
						((ResponseStatusException) result.getResolvedException()).getStatus()));
		verify(teacherService).getTeacherById(TEACHER_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateTeacher() throws Exception {
		UserAddEditDto userAddEditDto = new UserAddEditDto();
		userAddEditDto.setUserName(NAME);
		List<UUID> uuids = new ArrayList<>();
		uuids.add(TEACHER_ID);
		TeacherAddEditDto teacherAddEditDto = new TeacherAddEditDto();
		teacherAddEditDto.setDepartmentId(TEACHER_ID);
		teacherAddEditDto.setUser(userAddEditDto);
		teacherAddEditDto.setCourseIds(uuids);
		teacherAddEditDto.setLessonIds(uuids);

		mockMvc.perform(post("/admin/teachers/{id}/update", TEACHER_ID).with(csrf()).param("page", "1")
				.param("size", "10").param("user.userName", teacherAddEditDto.getUser().getUserName())
				.param("departmentId", teacherAddEditDto.getDepartmentId().toString())
				.param("courseIds",
						teacherAddEditDto.getCourseIds().stream().map(UUID::toString).toArray(String[]::new))
				.param("lessonIds",
						teacherAddEditDto.getLessonIds().stream().map(UUID::toString).toArray(String[]::new)))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/teachers?page=1&size=10"));

		ArgumentCaptor<TeacherAddEditDto> captor = ArgumentCaptor.forClass(TeacherAddEditDto.class);
		verify(teacherService).updateTeacher(eq(TEACHER_ID), captor.capture());

		assertThat(teacherAddEditDto.getCourseIds()).isEqualTo(captor.getValue().getCourseIds());
		assertThat(teacherAddEditDto.getDepartmentId()).isEqualTo(captor.getValue().getDepartmentId());
		assertThat(teacherAddEditDto.getLessonIds()).isEqualTo(captor.getValue().getLessonIds());
		assertThat(teacherAddEditDto.getUser()).isEqualTo(captor.getValue().getUser());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void deleteTeacher() throws Exception {
		mockMvc.perform(
				post("/admin/teachers/{id}/delete", TEACHER_ID).with(csrf()).param("page", "1").param("size", "10"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/teachers?page=1&size=10"));
		verify(adminService).deleteTeacherAndRoleWithUser(TEACHER_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void editRoleTeacher() throws Exception {
		UserReadDto userReadDto = createObjects.createUserDto(createObjects.createRoleDto(RoleElement.ROLE_TEACHER));
		TeacherReadDto teacher = createObjects.createTeacherDto(userReadDto);
		List<RoleReadDto> roles = new ArrayList<>();
		roles.add(userReadDto.getRoles().get(0));

		when(userService.getUserById(TEACHER_ID)).thenReturn(Optional.of(userReadDto));
		when(teacherService.getTeacherByUserId(TEACHER_ID)).thenReturn(teacher);
		when(roleService.getAllRoles()).thenReturn(roles);

		mockMvc.perform(get("/admin/teachers/{id}/roles", TEACHER_ID).param("returnUrl", "/dashboard"))
				.andExpect(status().isOk()).andExpect(view().name("admin/teachers/edit-roleTeacher"))
				.andExpect(model().attribute("user", userReadDto)).andExpect(model().attribute("teacher", teacher))
				.andExpect(model().attribute("role", roles)).andExpect(model().attribute("returnUrl", "/dashboard"));

		verify(userService).getUserById(TEACHER_ID);
		verify(teacherService).getTeacherByUserId(TEACHER_ID);
		verify(roleService).getAllRoles();
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateTeacherRoles_redirectsCorrectly() throws Exception {
		List<UUID> list = new ArrayList<>();
		list.add(TEACHER_ID);
		RoleIdsDto roleIdsDto = new RoleIdsDto(list);
		UserReadDto userReadDto = createObjects.createUserDto(createObjects.createRoleDto(RoleElement.ROLE_TEACHER));

		when(adminService.updateRoles(eq(TEACHER_ID), anyList())).thenReturn(userReadDto);

		when(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_TEACHER)).thenReturn(true);

		mockMvc.perform(post("/admin/teachers/{id}/roles", TEACHER_ID).with(csrf()).param("returnUrl", "/dashboard")
				.param("roleIds", roleIdsDto.getRoleIds().get(0).toString())).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/dashboard"));

		verify(adminService).updateRoles(TEACHER_ID, roleIdsDto.getRoleIds());
		verify(adminService).checkChooseRole(userReadDto, RoleElement.ROLE_TEACHER);
	}

}

package spring.controller.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import spring.dto.RoleIdsDto;
import spring.dto.RoleReadDto;
import spring.dto.StudentReadDto;
import spring.dto.UserReadDto;
import spring.model.RoleElement;
import spring.repository.StudentRepository;
import spring.repository.TeacherRepository;
import spring.service.AdminService;
import spring.service.RoleService;
import spring.service.StudentService;
import spring.service.TeacherService;
import spring.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = { "/sql/clear_tables.sql", "/sql/V1_schema.sql", "/sql/V2_sample_data.sql" })
class AdminStudentIntegControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private StudentService studentService;

	@Autowired
	private AdminService adminService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserService userService;

	@Autowired
	private TeacherService teacherService;

	@Autowired
	private TeacherRepository teacherRepository;

	private static final UUID STUDENT_ID = UUID.fromString("00000000-0000-0000-0000-000000000121");
	private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000093");
	private static final String USER_NAME = "student1";
	private static final UUID STUDENT_ROLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000061");
	private static final UUID METHODIST_ROLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000065");
	private static final UUID TEACHER_ROLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000062");
	private static final UUID QUEST_ROLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000064");
	private static final UUID ADMIN_ROLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000063");

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void editRole() throws Exception {
		UserReadDto userReadDto = userService.getUserById(USER_ID).get();
		StudentReadDto studentReadDto = studentService.getStudentByUserId(USER_ID);
		List<RoleReadDto> roleReadDtos = roleService.getAllRoles();

		mockMvc.perform(get("/admin/students/{id}/roles", USER_ID).param("returnUrl", "/")).andExpect(status().isOk())
				.andExpect(view().name("admin/students/edit-roleStudent"))
				.andExpect(model().attribute("user", userReadDto))
				.andExpect(model().attribute("student", studentReadDto))
				.andExpect(model().attribute("role", roleReadDtos)).andExpect(model().attributeExists("returnUrl"));

		assertTrue(userReadDto.getUserName().equals(USER_NAME));
		assertTrue(studentReadDto.getUser().getUserName().equals(USER_NAME));
		assertNotNull(roleReadDtos);
		assertTrue(roleReadDtos.size() == 5);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateStudentRoles_STUDENT_ADMIN() throws Exception {
		List<UUID> roleIds = new ArrayList<>();
		roleIds.add(STUDENT_ROLE_ID);
		roleIds.add(ADMIN_ROLE_ID);
		RoleIdsDto roleIdsDto = new RoleIdsDto(roleIds);

		UserReadDto userReadDto = adminService.updateRoles(USER_ID, roleIdsDto.getRoleIds());

		mockMvc.perform(post("/admin/students/{id}/roles", USER_ID).with(csrf()).param("returnUrl", "/dashboard")
				.param("roleIds", STUDENT_ROLE_ID.toString()).param("roleIds", ADMIN_ROLE_ID.toString()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/dashboard"));

		assertTrue(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_STUDENT));
		assertTrue(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_ADMIN));
		assertFalse(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_METHODIST));
		assertTrue(studentRepository.existsById(STUDENT_ID));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateStudentRoles_STUDENT_METHODIST() throws Exception {
		List<UUID> roleIds = new ArrayList<>();
		roleIds.add(STUDENT_ROLE_ID);
		roleIds.add(METHODIST_ROLE_ID);
		RoleIdsDto roleIdsDto = new RoleIdsDto(roleIds);

		UserReadDto userReadDto = adminService.updateRoles(USER_ID, roleIdsDto.getRoleIds());

		mockMvc.perform(post("/admin/students/{id}/roles", USER_ID).with(csrf()).param("returnUrl", "/dashboard")
				.param("roleIds", STUDENT_ROLE_ID.toString()).param("roleIds", METHODIST_ROLE_ID.toString()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/dashboard"));

		assertTrue(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_STUDENT));
		assertTrue(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_METHODIST));
		assertFalse(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_ADMIN));
		assertTrue(studentRepository.existsById(STUDENT_ID));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateStudentRoles_STUDENT() throws Exception {
		List<UUID> roleIds = new ArrayList<>();
		roleIds.add(STUDENT_ROLE_ID);
		RoleIdsDto roleIdsDto = new RoleIdsDto(roleIds);

		UserReadDto userReadDto = adminService.updateRoles(USER_ID, roleIdsDto.getRoleIds());

		mockMvc.perform(post("/admin/students/{id}/roles", USER_ID).with(csrf()).param("returnUrl", "/dashboard")
				.param("roleIds", STUDENT_ROLE_ID.toString())).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/dashboard"));

		assertTrue(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_STUDENT));
		assertFalse(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_ADMIN));
		assertFalse(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_METHODIST));
		assertTrue(studentRepository.existsById(STUDENT_ID));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateStudentRoles_ADMIN() throws Exception {
		List<UUID> roleIds = new ArrayList<>();
		roleIds.add(ADMIN_ROLE_ID);
		RoleIdsDto roleIdsDto = new RoleIdsDto(roleIds);

		UserReadDto userReadDto = adminService.updateRoles(USER_ID, roleIdsDto.getRoleIds());

		mockMvc.perform(
				post("/admin/students/{id}/roles", USER_ID).with(csrf()).param("roleIds", ADMIN_ROLE_ID.toString()))
				.andExpect(status().isOk());

		assertFalse(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_STUDENT));
		assertTrue(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_ADMIN));
		assertFalse(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_METHODIST));
		assertFalse(studentRepository.existsById(STUDENT_ID));
		assertEquals(studentService.getAllStudents().size(), 0);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateStudentRoles_QUEST() throws Exception {
		List<UUID> roleIds = new ArrayList<>();
		roleIds.add(QUEST_ROLE_ID);
		RoleIdsDto roleIdsDto = new RoleIdsDto(roleIds);

		UserReadDto userReadDto = adminService.updateRoles(USER_ID, roleIdsDto.getRoleIds());

		mockMvc.perform(
				post("/admin/students/{id}/roles", USER_ID).with(csrf()).param("roleIds", QUEST_ROLE_ID.toString()))
				.andExpect(status().isOk());

		assertFalse(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_STUDENT));
		assertTrue(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_QUEST));
		assertFalse(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_METHODIST));
		assertFalse(studentRepository.existsById(STUDENT_ID));
		assertEquals(studentService.getAllStudents().size(), 0);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateStudentRoles_METHODIST() throws Exception {
		List<UUID> roleIds = new ArrayList<>();
		roleIds.add(METHODIST_ROLE_ID);
		RoleIdsDto roleIdsDto = new RoleIdsDto(roleIds);

		UserReadDto userReadDto = adminService.updateRoles(USER_ID, roleIdsDto.getRoleIds());

		mockMvc.perform(
				post("/admin/students/{id}/roles", USER_ID).with(csrf()).param("roleIds", METHODIST_ROLE_ID.toString()))
				.andExpect(status().isOk());

		assertFalse(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_STUDENT));
		assertFalse(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_ADMIN));
		assertTrue(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_METHODIST));
		assertFalse(studentRepository.existsById(STUDENT_ID));
		assertEquals(studentService.getAllStudents().size(), 0);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateStudentRoles_TEACHER() throws Exception {
		List<UUID> roleIds = new ArrayList<>();
		roleIds.add(TEACHER_ROLE_ID);
		RoleIdsDto roleIdsDto = new RoleIdsDto(roleIds);
		assertTrue(teacherService.getAllTeachers().size() == 1);
		UserReadDto userReadDto = adminService.updateRoles(USER_ID, roleIdsDto.getRoleIds());

		mockMvc.perform(
				post("/admin/students/{id}/roles", USER_ID).with(csrf()).param("roleIds", TEACHER_ROLE_ID.toString()))
				.andExpect(status().is3xxRedirection());

		assertTrue(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_TEACHER));
		assertFalse(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_ADMIN));
		assertFalse(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_METHODIST));
		assertFalse(studentRepository.existsById(STUDENT_ID));
		assertEquals(studentService.getAllStudents().size(), 0);

		UUID userIdOldStudent = userService.getAllUsers().stream().filter(user -> user.getId().equals(USER_ID))
				.collect(Collectors.toList()).get(0).getId();
		UUID userIdNewTeacher = teacherService.getTeacherByUserId(userIdOldStudent).getId();

		assertTrue(teacherRepository.existsById(userIdNewTeacher));
		assertTrue(teacherService.getAllTeachers().size() == 2);
	}

}

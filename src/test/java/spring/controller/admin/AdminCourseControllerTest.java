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
import spring.dto.CourseAddEditDto;
import spring.dto.CourseReadDto;
import spring.dto.DepartmentReadDto;
import spring.dto.GroupReadDto;
import spring.service.AdminService;
import spring.service.CastomUserDetailsService;
import spring.service.CourseService;
import spring.service.DepartmentService;
import spring.service.GroupService;
import spring.service.TeacherService;

@WebMvcTest(controllers = AdminCourseController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class AdminCourseControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CourseService courseService;

	@MockBean
	private GroupService groupService;

	@MockBean
	private DepartmentService departmentService;

	@MockBean
	private AdminService adminService;

	@MockBean
	private TeacherService teacherService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;

	private static final String NAME = "name";
	private static final UUID COURSE_ID = UUID.randomUUID();
	private CreateObjects createObjects = new CreateObjects();

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void getPageCourseTest() throws Exception {
		List<DepartmentReadDto> departmentReadDtos = new ArrayList<>();
		departmentReadDtos.add(createObjects.createDepartmentDto());

		List<GroupReadDto> groupReadDtos = new ArrayList<>();
		groupReadDtos.add(createObjects.createGroupDto());
		Page<GroupReadDto> pageGroup = new PageImpl<>(groupReadDtos);

		List<CourseReadDto> dtos = new ArrayList<>();
		dtos.add(createObjects.createCourseDto());
		Page<CourseReadDto> pageCourse = new PageImpl<>(dtos);

		when(courseService.getAllPageCourses(any(PageRequest.class))).thenReturn(pageCourse);
		when(groupService.getAllPageGroups(any(PageRequest.class))).thenReturn(pageGroup);
		when(departmentService.getAllDepartments()).thenReturn(departmentReadDtos);

		mockMvc.perform(get("/admin/courses").param("groupPage", "0").param("groupSize", "5"))
				.andExpect(status().isOk()).andExpect(view().name("admin/courses/courses"))
				.andExpect(model().attributeExists("pageCourse")).andExpect(model().attribute("currentPage", 0))
				.andExpect(model().attribute("pageSize", 10)).andExpect(model().attribute("totalPage", 1))
				.andExpect(model().attribute("basePath", "/admin/courses"))
				.andExpect(model().attribute("pageGroup", pageGroup))
				.andExpect(model().attribute("totalPages", pageGroup.getTotalPages()))
				.andExpect(model().attributeExists("newCourse")).andExpect(model().attributeExists("departments"));

		verify(courseService).getAllPageCourses(any(PageRequest.class));
		verify(groupService).getAllPageGroups(any(PageRequest.class));
		verify(departmentService).getAllDepartments();
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateCourseTest() throws Exception {
		UUID departmnetId = UUID.randomUUID();
		UUID groupId = UUID.randomUUID();

		mockMvc.perform(post("/admin/courses/{id}/update", COURSE_ID).with(csrf()).param("courseName", NAME)
				.param("departmentId", departmnetId.toString()).param("groupId", groupId.toString()).param("page", "1")
				.param("size", "10")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/courses?page=1&size=10"));

		ArgumentCaptor<CourseAddEditDto> argumentCaptor = ArgumentCaptor.forClass(CourseAddEditDto.class);
		verify(courseService).updateCourse(eq(COURSE_ID), argumentCaptor.capture());

		assertEquals(NAME, argumentCaptor.getValue().getCourseName());
		assertEquals(departmnetId, argumentCaptor.getValue().getDepartmentId());
		assertEquals(groupId, argumentCaptor.getValue().getGroupId());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void deleteCourseTest() throws Exception {
		when(courseService.deleteCourseById(COURSE_ID)).thenReturn(true);
		mockMvc.perform(
				post("/admin/courses/{id}/delete", COURSE_ID).with(csrf()).param("page", "1").param("size", "10"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/courses?page=1&size=10"));

		verify(courseService).deleteCourseById(COURSE_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void deleteCourse_NOT_FOUND_Test() throws Exception {
		when(courseService.deleteCourseById(COURSE_ID)).thenReturn(false);

		mockMvc.perform(
				post("/admin/courses/{id}/delete", COURSE_ID).with(csrf()).param("page", "1").param("size", "10"))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
				.andExpect(result -> assertEquals(HttpStatus.NOT_FOUND,
						((ResponseStatusException) result.getResolvedException()).getStatus()));

		verify(courseService).deleteCourseById(COURSE_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void removeGroupFromCourseTest() throws Exception {
		mockMvc.perform(post("/admin/courses/{courseId}/groups/{groupName}/delete", COURSE_ID, NAME).with(csrf())
				.param("page", "1").param("size", "10")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/courses?page=1&size=10"));

		verify(adminService).removeGroup(COURSE_ID, NAME);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void createCourseTest() throws Exception {
		CourseAddEditDto courseAddEditDto = new CourseAddEditDto();
		courseAddEditDto.setCourseName(NAME);
		courseAddEditDto.setDepartmentId(UUID.randomUUID());
		courseAddEditDto.setGroupId(UUID.randomUUID());

		mockMvc.perform(post("/admin/courses/new").with(csrf()).param("page", "1").param("size", "10")
				.param("courseName", courseAddEditDto.getCourseName())
				.param("groupId", courseAddEditDto.getGroupId().toString())
				.param("departmentId", courseAddEditDto.getDepartmentId().toString()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/courses?page=1&size=10"));

		ArgumentCaptor<CourseAddEditDto> captor = ArgumentCaptor.forClass(CourseAddEditDto.class);
		verify(courseService).addCourse(captor.capture());

		assertThat(courseAddEditDto.getCourseName()).isEqualTo(captor.getValue().getCourseName());
		assertThat(courseAddEditDto.getDepartmentId()).isEqualTo(captor.getValue().getDepartmentId());
		assertThat(courseAddEditDto.getGroupId()).isEqualTo(captor.getValue().getGroupId());
	}

}

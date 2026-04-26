package spring.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import spring.config.SecurityConfig;
import spring.dto.CourseReadDto;
import spring.dto.EnrollmentReadDto;
import spring.dto.StudentReadDto;
import spring.dto.UserReadDto;
import spring.model.EnrollmentStatus;
import spring.service.CastomUserDetailsService;
import spring.service.EnrollmentService;
import spring.service.IpBlockService;

@WebMvcTest(controllers = EnrollmentController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class EnrollmentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private EnrollmentService enrollmentService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;
	
	@MockBean
	private IpBlockService ipBlockService;

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void test() throws Exception {
		EnrollmentReadDto enrollment = new EnrollmentReadDto();
		enrollment.setId(UUID.randomUUID());
		enrollment.setGrade("22");
		enrollment.setStatus(EnrollmentStatus.ACTIVE);

		UserReadDto user = new UserReadDto();
		user.setId(UUID.randomUUID());
		user.setEmail("qwe");
		user.setEnabled(true);
		user.setFirstName("qwe");
		user.setLastName("qwe");
		user.setUserName("qwe");
		user.setPasswordHash("qwe");

		StudentReadDto student = new StudentReadDto();
		student.setId(UUID.randomUUID());
		student.setUser(user);

		enrollment.setStudent(student);

		CourseReadDto course = new CourseReadDto();
		course.setId(UUID.randomUUID());
		course.setCourseName("qwe");

		enrollment.setCourse(course);

		Page<EnrollmentReadDto> page = new PageImpl<>(Arrays.asList(enrollment));
		when(enrollmentService.getAllPageEnrollments(any(PageRequest.class))).thenReturn(page);

		mockMvc.perform(get("/enrollments")).andExpect(status().isOk()).andExpect(view().name("enrollments"))
				.andExpect(model().attributeExists("pageEnrollment")).andExpect(model().attribute("currentPage", 0))
				.andExpect(model().attribute("totalPage", 1));
	}

}

package spring.controller.student;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import spring.auxiliaryObjects.CreateObjects;
import spring.config.SecurityConfig;
import spring.dto.CourseReadDto;
import spring.service.CastomUserDetailsService;
import spring.service.CourseService;

@WebMvcTest(controllers = StudentCourseController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class StudentCourseControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CourseService courseService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;

	private CreateObjects createObjects = new CreateObjects();

	@Test
	@WithMockUser(username = "student", roles = { "STUDENT" })
	void getStudentTest() throws Exception {
		CourseReadDto courseReadDto = createObjects.createCourseDto();
		List<CourseReadDto> courseReadDtos = new ArrayList<>();
		courseReadDtos.add(courseReadDto);

		when(courseService.getAllCourses()).thenReturn(courseReadDtos);

		mockMvc.perform(get("/student/courses")).andExpect(status().isOk())
				.andExpect(view().name("student/courses/courses"))
				.andExpect(model().attribute("courses", courseReadDtos));
		verify(courseService).getAllCourses();
	}
}

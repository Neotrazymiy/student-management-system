package spring.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

@WebMvcTest(controllers = CourseController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class CourseControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	protected CourseService courseService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;

	private CreateObjects createObjects = new CreateObjects();

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void test() throws Exception {
		CourseReadDto course = createObjects.createCourseDto();
		List<CourseReadDto> dtos = new ArrayList<>();
		dtos.add(course);

		when(courseService.getAllCourses()).thenReturn(dtos);

		mockMvc.perform(get("/courses")).andExpect(status().isOk()).andExpect(view().name("courses"));

		verify(courseService).getAllCourses();
	}

}

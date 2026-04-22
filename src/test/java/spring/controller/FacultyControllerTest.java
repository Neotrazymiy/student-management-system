package spring.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import spring.config.SecurityConfig;
import spring.dto.FacultyReadDto;
import spring.dto.UniversityReadDto;
import spring.service.CastomUserDetailsService;
import spring.service.FacultyService;

@WebMvcTest(controllers = FacultyController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class FacultyControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FacultyService facultyService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void test() throws Exception {
		FacultyReadDto faculty = new FacultyReadDto();
		faculty.setId(UUID.randomUUID());
		faculty.setName("qwe");

		UniversityReadDto university = new UniversityReadDto();
		university.setId(UUID.randomUUID());
		faculty.setUniversity(university);

		List<FacultyReadDto> list = new ArrayList<>();
		list.add(faculty);

		when(facultyService.getAllFaculty()).thenReturn(list);
		mockMvc.perform(get("/facultys")).andExpect(status().isOk()).andExpect(view().name("facultys"))
				.andExpect(model().attributeExists("facultys")).andExpect(model().attribute("facultys", list));
	}

}

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
import spring.dto.UniversityReadDto;
import spring.service.CastomUserDetailsService;
import spring.service.UniversityService;

@WebMvcTest(controllers = UniversityController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class UniversityControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UniversityService universityService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void test() throws Exception {
		UniversityReadDto university = new UniversityReadDto();
		university.setId(UUID.randomUUID());
		university.setName("qwe");
		List<UniversityReadDto> list = new ArrayList<>();
		list.add(university);

		when(universityService.getAllUniversitys()).thenReturn(list);
		mockMvc.perform(get("/universitys")).andExpect(status().isOk()).andExpect(view().name("universitys"))
				.andExpect(model().attributeExists("universitys")).andExpect(model().attribute("universitys", list));
	}

}

package spring.controller.quest;

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
import spring.dto.UniversityReadDto;
import spring.service.CastomUserDetailsService;
import spring.service.UniversityService;

@WebMvcTest(controllers = QuestUniversityController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class QuestUniversityControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UniversityService universityService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;

	private CreateObjects createObjects = new CreateObjects();

	@Test
	@WithMockUser(username = "quest", roles = { "QUEST" })
	void getUniversityTest() throws Exception {
		UniversityReadDto universityReadDto = createObjects.createUniversityDto();
		List<UniversityReadDto> universityReadDtos = new ArrayList<>();
		universityReadDtos.add(universityReadDto);
		when(universityService.getAllUniversitys()).thenReturn(universityReadDtos);

		mockMvc.perform(get("/quest/universitys")).andExpect(status().isOk())
				.andExpect(view().name("quest/universitys"))
				.andExpect(model().attribute("universitys", universityReadDtos));
		verify(universityService).getAllUniversitys();
	}
}

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
import spring.dto.FacultyReadDto;
import spring.service.CastomUserDetailsService;
import spring.service.FacultyService;
import spring.service.IpBlockService;

@WebMvcTest(controllers = QuestFacultyController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class QuestFacultyControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FacultyService facultyService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;
	
	@MockBean
	private IpBlockService ipBlockService;

	private CreateObjects createObjects = new CreateObjects();

	@Test
	@WithMockUser(username = "quest", roles = { "QUEST" })
	void getFacultyTest() throws Exception {
		FacultyReadDto facultyReadDto = createObjects.createFacultyDto();
		List<FacultyReadDto> facultyReadDtos = new ArrayList<>();
		facultyReadDtos.add(facultyReadDto);
		when(facultyService.getAllFaculty()).thenReturn(facultyReadDtos);

		mockMvc.perform(get("/quest/facultys")).andExpect(status().isOk()).andExpect(view().name("quest/facultys"))
				.andExpect(model().attribute("facultys", facultyReadDtos));
		verify(facultyService).getAllFaculty();
	}

}

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
import spring.dto.DepartmentReadDto;
import spring.service.CastomUserDetailsService;
import spring.service.DepartmentService;
import spring.service.IpBlockService;

@WebMvcTest(controllers = QuestDepartmentController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class QuestDepartmentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DepartmentService departmentService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;
	
	@MockBean
	private IpBlockService ipBlockService;

	private CreateObjects createObjects = new CreateObjects();

	@Test
	@WithMockUser(username = "quest", roles = { "QUEST" })
	void test() throws Exception {
		DepartmentReadDto departmentReadDto = createObjects.createDepartmentDto();
		List<DepartmentReadDto> departmentReadDtos = new ArrayList<>();
		departmentReadDtos.add(departmentReadDto);
		when(departmentService.getAllDepartments()).thenReturn(departmentReadDtos);

		mockMvc.perform(get("/quest/departments")).andExpect(status().isOk())
				.andExpect(view().name("quest/departments"))
				.andExpect(model().attribute("departments", departmentReadDtos));
		verify(departmentService).getAllDepartments();
	}

}

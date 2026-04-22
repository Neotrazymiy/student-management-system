package spring.controller.teacher;

import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import spring.auxiliaryObjects.CreateObjects;
import spring.config.SecurityConfig;
import spring.dto.GroupReadDto;
import spring.service.CastomUserDetailsService;
import spring.service.DepartmentService;
import spring.service.GroupService;

@WebMvcTest(controllers = TeacherGroupController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class TeacherGroupControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private GroupService groupService;

	@MockBean
	private DepartmentService departmentService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;

	private CreateObjects createObjects = new CreateObjects();

	@Test
	@WithMockUser(username = "teacher", roles = { "TEACHER" })
	void getPageGroupTest() throws Exception {
		List<GroupReadDto> groupReadDtos = new ArrayList<>();
		groupReadDtos.add(createObjects.createGroupDto());
		Page<GroupReadDto> page = new PageImpl<>(groupReadDtos);

		when(groupService.getAllPageGroups(any(PageRequest.class))).thenReturn(page);

		mockMvc.perform(get("/teacher/groups")).andExpect(status().isOk())
				.andExpect(view().name("teacher/groups/groups")).andExpect(model().attributeExists("pageGroup"))
				.andExpect(model().attribute("currentPage", 0)).andExpect(model().attribute("pageSize", 10))
				.andExpect(model().attribute("totalPage", 1))
				.andExpect(model().attribute("basePath", "/teacher/groups"));
	}

}

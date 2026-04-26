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
import spring.dto.DepartmentReadDto;
import spring.dto.GroupReadDto;
import spring.service.CastomUserDetailsService;
import spring.service.DepartmentService;
import spring.service.GroupService;
import spring.service.IpBlockService;

@WebMvcTest(controllers = GroupController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class GroupControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private GroupService groupService;

	@MockBean
	protected DepartmentService departmentService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;
	
	@MockBean
	private IpBlockService ipBlockService;

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void groups() throws Exception {
		GroupReadDto group = new GroupReadDto();
		group.setId(UUID.randomUUID());

		DepartmentReadDto department = new DepartmentReadDto();
		department.setId(UUID.randomUUID());
		group.setDepartment(department);

		Page<GroupReadDto> page = new PageImpl<>(Arrays.asList(group));
		when(groupService.getAllPageGroups(any(PageRequest.class))).thenReturn(page);

		mockMvc.perform(get("/groups")).andExpect(status().isOk()).andExpect(view().name("group/groups"))
				.andExpect(model().attributeExists("pageGroup")).andExpect(model().attribute("currentPage", 0))
				.andExpect(model().attribute("totalPage", 1));
	}

}

package spring.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
import spring.dto.DepartmentReadDto;
import spring.dto.GroupAddEditDto;
import spring.dto.GroupReadDto;
import spring.exception.DeleteException;
import spring.service.CastomUserDetailsService;
import spring.service.DepartmentService;
import spring.service.GroupService;

@WebMvcTest(controllers = AdminGroupController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class AdminGroupControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private GroupService groupService;

	@MockBean
	private DepartmentService departmentService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;

	private static final String NAME = "namenamename";
	private static final UUID group_ID = UUID.randomUUID();
	private CreateObjects createObjects = new CreateObjects();

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void getPageGroupTest() throws Exception {
		List<DepartmentReadDto> departmentReadDtos = new ArrayList<>();
		departmentReadDtos.add(createObjects.createDepartmentDto());

		List<GroupReadDto> groupReadDtos = new ArrayList<>();
		groupReadDtos.add(createObjects.createGroupDto());
		Page<GroupReadDto> page = new PageImpl<>(groupReadDtos);

		when(groupService.getAllPageGroups(any(PageRequest.class))).thenReturn(page);
		when(departmentService.getAllDepartments()).thenReturn(departmentReadDtos);

		mockMvc.perform(get("/admin/groups")).andExpect(status().isOk()).andExpect(view().name("admin/groups/groups"))
				.andExpect(model().attributeExists("pageGroup")).andExpect(model().attribute("currentPage", 0))
				.andExpect(model().attribute("pageSize", 10)).andExpect(model().attribute("totalPage", 1))
				.andExpect(model().attribute("basePath", "/admin/groups"))
				.andExpect(model().attributeExists("newGroup"))
				.andExpect(model().attribute("departments", departmentReadDtos));

	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateGroupTest() throws Exception {
		UUID departmnetId = UUID.randomUUID();

		mockMvc.perform(post("/admin/groups/{id}/update", group_ID).with(csrf()).param("name", NAME)
				.param("departmentId", departmnetId.toString()).param("page", "1").param("size", "10"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/groups?page=1&size=10"));

		ArgumentCaptor<GroupAddEditDto> argumentCaptor = ArgumentCaptor.forClass(GroupAddEditDto.class);
		verify(groupService).updateGroup(eq(group_ID), argumentCaptor.capture());

		assertEquals(NAME, argumentCaptor.getValue().getName());
		assertEquals(departmnetId, argumentCaptor.getValue().getDepartmentId());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void deleteGroupTest() throws Exception {
		mockMvc.perform(post("/admin/groups/{id}/delete", group_ID).with(csrf()).param("page", "1").param("size", "10"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/groups?page=1&size=10"));
		verify(groupService).deleteGroupById(group_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void deleteGroup_MyException_Test() throws Exception {
		doThrow(new DeleteException(NAME)).when(groupService).deleteGroupById(group_ID);

		mockMvc.perform(post("/admin/groups/{id}/delete", group_ID).with(csrf())).andExpect(status().isOk())
				.andExpect(view().name("admin/exception")).andExpect(model().attribute("exception", NAME));

		verify(groupService).deleteGroupById(group_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void createGroupTest() throws Exception {
		GroupAddEditDto groupAddEditDto = new GroupAddEditDto();
		groupAddEditDto.setName(NAME);
		groupAddEditDto.setDepartmentId(UUID.randomUUID());

		mockMvc.perform(post("/admin/groups/new").with(csrf()).param("page", "1").param("size", "10")
				.param("name", groupAddEditDto.getName())
				.param("departmentId", groupAddEditDto.getDepartmentId().toString()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/groups?page=1&size=10"));

		ArgumentCaptor<GroupAddEditDto> captor = ArgumentCaptor.forClass(GroupAddEditDto.class);
		verify(groupService).addGroup(captor.capture());

		assertThat(groupAddEditDto.getName()).isEqualTo(captor.getValue().getName());
		assertThat(groupAddEditDto.getDepartmentId()).isEqualTo(captor.getValue().getDepartmentId());
	}

}

package spring.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import spring.auxiliaryObjects.CreateObjects;
import spring.config.SecurityConfig;
import spring.dto.RoleAddEditDto;
import spring.dto.RoleReadDto;
import spring.exception.DeleteException;
import spring.model.RoleElement;
import spring.service.CastomUserDetailsService;
import spring.service.RoleService;

@WebMvcTest(controllers = AdminRoleController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class AdminRoleControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RoleService roleService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;

	private static final String NAME = "ROLE_QWEQWE";
	private static final UUID role_ID = UUID.randomUUID();
	private CreateObjects createObjects = new CreateObjects();

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void getRoleTest() throws Exception {
		List<RoleReadDto> roleReadDtos = new ArrayList<>();
		roleReadDtos.add(createObjects.createRoleDto(RoleElement.ROLE_ADMIN));

		when(roleService.getAllRoles()).thenReturn(roleReadDtos);

		mockMvc.perform(get("/admin/roles")).andExpect(status().isOk()).andExpect(view().name("admin/roles/roles"))
				.andExpect(model().attribute("roles", roleReadDtos)).andExpect(model().attributeExists("newRole"));

		verify(roleService).getAllRoles();
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateRoleTest() throws Exception {
		UUID permissionId = UUID.randomUUID();

		mockMvc.perform(post("/admin/roles/{id}/update", role_ID).with(csrf()).param("name", NAME).param("permissionId",
				permissionId.toString())).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/roles"));

		ArgumentCaptor<RoleAddEditDto> captor = ArgumentCaptor.forClass(RoleAddEditDto.class);
		verify(roleService).updateRole(eq(role_ID), captor.capture());

		assertThat(NAME).isEqualTo(captor.getValue().getName());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void deleteRoleTest() throws Exception {
		mockMvc.perform(post("/admin/roles/{id}/delete", role_ID).with(csrf())).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/roles"));
		verify(roleService).deleteRoleById(role_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void deleteRole_MyException_Test() throws Exception {
		doThrow(new DeleteException(NAME)).when(roleService).deleteRoleById(role_ID);

		mockMvc.perform(post("/admin/roles/{id}/delete", role_ID).with(csrf())).andExpect(status().isOk())
				.andExpect(view().name("admin/exception")).andExpect(model().attribute("exception", NAME));
		verify(roleService).deleteRoleById(role_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void createRole() throws Exception {
		RoleAddEditDto roleAddEditDto = new RoleAddEditDto();
		roleAddEditDto.setName(NAME);

		mockMvc.perform(post("/admin/roles/new").with(csrf()).param("name", roleAddEditDto.getName()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/roles"));

		ArgumentCaptor<RoleAddEditDto> captor = ArgumentCaptor.forClass(RoleAddEditDto.class);
		verify(roleService).addRole(captor.capture());

		assertThat(roleAddEditDto.getName()).isEqualTo(captor.getValue().getName());
	}

}

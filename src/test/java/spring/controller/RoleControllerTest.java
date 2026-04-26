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
import spring.dto.RoleReadDto;
import spring.service.CastomUserDetailsService;
import spring.service.IpBlockService;
import spring.service.RoleService;

@WebMvcTest(controllers = RoleController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class RoleControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RoleService roleService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;
	
	@MockBean
	private IpBlockService ipBlockService;

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void test() throws Exception {
		RoleReadDto role = new RoleReadDto();
		role.setId(UUID.randomUUID());

		Page<RoleReadDto> page = new PageImpl<>(Arrays.asList(role));
		when(roleService.getAllPageRoles(any(PageRequest.class))).thenReturn(page);

		mockMvc.perform(get("/roles")).andExpect(status().isOk()).andExpect(view().name("roles"))
				.andExpect(model().attributeExists("pageRole")).andExpect(model().attribute("currentPage", 0))
				.andExpect(model().attribute("totalPage", 1));
	}

}

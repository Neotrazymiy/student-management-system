package spring.controller.quest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import spring.config.SecurityConfig;
import spring.dto.UserAddEditDto;
import spring.service.CastomUserDetailsService;
import spring.service.UserService;

@WebMvcTest(controllers = QuestRegistrationController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class QuestRegistrationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;

	@Test
	@WithMockUser(username = "quest", roles = { "QUEST" })
	void registration() throws Exception {
		UserAddEditDto userAddEditDto = new UserAddEditDto();

		mockMvc.perform(get("/quest/registration")).andExpect(status().isOk()).andExpect(view().name("registration"))
				.andExpect(model().attribute("user", userAddEditDto));
	}

	@Test
	@WithMockUser(username = "quest", roles = { "QUEST" })
	void createError() throws Exception {
		mockMvc.perform(post("/quest/registration/edit").with(csrf()).param("userName", "qw").param("firstName", "qwe")
				.param("lastName", "йцу").param("email", "qwe@q").param("passwordHash", "qwe"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/quest/registration"))
				.andExpect(flash().attributeExists("errors")).andExpect(flash().attributeExists("user"));
	}

	@Test
	@WithMockUser(username = "quest", roles = { "QUEST" })
	void createOk() throws Exception {
		mockMvc.perform(post("/quest/registration/edit").with(csrf()).param("userName", "qwe").param("firstName", "йцу")
				.param("lastName", "йцу").param("email", "q@we.com").param("passwordHash", "name"))
				.andExpect(status().isOk()).andExpect(view().name("login"));

		ArgumentCaptor<UserAddEditDto> captor = ArgumentCaptor.forClass(UserAddEditDto.class);
		verify(userService).addUser(captor.capture());
		verify(userService, times(1)).addUser(any(UserAddEditDto.class));

		assertTrue(captor.getValue().getUserName().equals("qwe"));
		assertTrue(captor.getValue().getFirstName().equals("йцу"));
		assertTrue(captor.getValue().getLastName().equals("йцу"));
		assertTrue(captor.getValue().getEmail().equals("q@we.com"));
		assertTrue(captor.getValue().getPasswordHash().equals("name"));

	}

}

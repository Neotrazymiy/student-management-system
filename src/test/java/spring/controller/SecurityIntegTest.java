package spring.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser(username = "student", roles = { "STUDENT" })
	void userFirbiddenStudent() throws Exception {
		mockMvc.perform(get("/methodist/courses")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "quest", roles = { "QUEST" })
	void userFirbiddenQuest() throws Exception {
		mockMvc.perform(get("/methodist/courses")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void userFirbiddenAdmin() throws Exception {
		mockMvc.perform(get("/methodist/courses")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "teacher", roles = { "TEACHER" })
	void userFirbiddenTeacher() throws Exception {
		mockMvc.perform(get("/methodist/courses")).andExpect(status().isForbidden());
	}

}

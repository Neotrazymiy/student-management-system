package spring.controller.student;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import javax.transaction.Transactional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = { "/sql/clear_tables.sql", "/sql/V1_schema.sql", "/sql/V2_sample_data.sql" })
class StudentLessonIntegControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser(username = "student", roles = { "STUDENT" })
	void lessonTrueTest() throws Exception {
		mockMvc.perform(get("/student/lessons").with(csrf()).param("page", "0").param("size", "10")
				.param("groupPage", "0").param("groupSize", "5")).andExpect(status().isOk())
				.andExpect(view().name("student/lessons/lessonsFilter"))
				.andExpect(model().attributeExists("pageLesson")).andExpect(model().attribute("currentPage", 0))
				.andExpect(model().attribute("pageSize", 10)).andExpect(model().attributeExists("filter"))
				.andExpect(model().attributeExists("totalPage"))
				.andExpect(model().attribute("basePath", "/student/lessons"))
				.andExpect(model().attributeExists("pageGroup")).andExpect(model().attributeExists("totalPages"))
				.andExpect(model().attributeExists("courses")).andExpect(model().attributeExists("teachers"))
				.andExpect(model().attributeExists("rooms"));
	}

	@Test
	@WithMockUser(username = "student", roles = { "STUDENT" })
	void lessonValidErrorTest() throws Exception {
		mockMvc.perform(get("/student/lessons").with(csrf()).param("from", "22-05-2027").param("to", "22-05-2020")
				.param("page", "0").param("size", "10").param("groupPage", "0").param("groupSize", "5"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/student/lessons"))
				.andExpect(flash().attributeExists("errors")).andExpect(flash().attributeExists("filter"));
	}

	@Test
	@WithMockUser(username = "student1", roles = { "STUDENT" })
	void studentScheduleTrueTest() throws Exception {
		mockMvc.perform(get("/student/studentSchedule").with(csrf()).param("page", "0").param("size", "10"))
				.andExpect(status().isOk()).andExpect(view().name("student/lessons/studentSchedule"))
				.andExpect(model().attributeExists("pageLesson")).andExpect(model().attribute("currentPage", 0))
				.andExpect(model().attribute("pageSize", 10)).andExpect(model().attributeExists("filter"))
				.andExpect(model().attributeExists("totalPage"))
				.andExpect(model().attribute("basePath", "/student/studentSchedule"))
				.andExpect(model().attributeExists("courses")).andExpect(model().attributeExists("teachers"))
				.andExpect(model().attributeExists("rooms")).andExpect(model().attributeExists("departments"));
	}

	@Test
	@WithMockUser(username = "student", roles = { "STUDENT" })
	void studentScheduleValidErrorTest() throws Exception {
		mockMvc.perform(
				get("/student/studentSchedule").with(csrf()).param("from", "22-05-2027").param("to", "22-05-2020")
						.param("page", "0").param("size", "10").param("groupPage", "0").param("groupSize", "5"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/student/studentSchedule"))
				.andExpect(flash().attributeExists("errors")).andExpect(flash().attributeExists("filter"));
	}

	@Test
	@WithMockUser(username = "student1", roles = { "STUDENT" })
	void studentScheduleExportTest() throws Exception {
		mockMvc.perform(get("/student/studentSchedule/export").with(csrf())).andExpect(status().isOk())
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=schedule.ics"))
				.andExpect(content().contentType("text/calendar"))
				.andExpect(content().string(Matchers.containsString("BEGIN:VCALENDAR")));
	}

}

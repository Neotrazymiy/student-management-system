package spring.controller.teacher;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.UUID;

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

import spring.dto.LessonAddEditDto;
import spring.dto.LessonReadDto;
import spring.model.LessonStatus;
import spring.service.LessonService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = { "/sql/clear_tables.sql", "/sql/V1_schema.sql", "/sql/V2_sample_data.sql" })
class TeacherLessonIntegControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private LessonService lessonService;

	@Test
	@WithMockUser(username = "teacher1", roles = { "TEACHER" })
	void lessonTrueTest() throws Exception {

		mockMvc.perform(get("/teacher/lessonsSchedule").with(csrf()).param("page", "0").param("size", "10"))
				.andExpect(status().isOk()).andExpect(view().name("teacher/lessons/teacherSchedule"))
				.andExpect(model().attributeExists("pageLesson")).andExpect(model().attribute("currentPage", 0))
				.andExpect(model().attribute("pageSize", 10)).andExpect(model().attributeExists("filter"))
				.andExpect(model().attributeExists("totalPage"))
				.andExpect(model().attribute("basePath", "/teacher/lessonsSchedule"))
				.andExpect(model().attributeExists("departments"));
	}

	@Test
	@WithMockUser(username = "teacher", roles = { "TEACHER" })
	void lessonValidErrorTest() throws Exception {
		mockMvc.perform(get("/teacher/lessonsSchedule").with(csrf()).param("from", "22-05-2027")
				.param("to", "22-05-2020").param("page", "0").param("size", "10"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/teacher/lessonsSchedule"))
				.andExpect(flash().attributeExists("errors")).andExpect(flash().attributeExists("filter"));
	}

	@Test
	@WithMockUser(username = "teacher", roles = { "TEACHER" })
	void teacherLessonUpdate() throws Exception {
		UUID lessonId = lessonService.getAllLessons().get(0).getId();
		LessonReadDto lessonReadDto = lessonService.getLessonById(lessonId).get();
		String oldStatus = lessonReadDto.getStatus().name();
		LessonAddEditDto lessonAddEditDto = new LessonAddEditDto();
		lessonAddEditDto.setStatus(LessonStatus.MOVED);
		lessonAddEditDto.setCourseId(lessonReadDto.getCourse().getId());
		lessonAddEditDto.setRoomId(lessonReadDto.getRoom().getId());
		lessonAddEditDto.setFrom(lessonReadDto.getDate());
		lessonAddEditDto.setStartTime(lessonReadDto.getStartTime());
		lessonAddEditDto.setEndTime(lessonReadDto.getEndTime());
		lessonReadDto = lessonService.updateLesson(lessonId, lessonAddEditDto).get();
		String newStatus = lessonReadDto.getStatus().name();

		mockMvc.perform(post("/teacher/lessonsSchedule/{id}/update", lessonId).with(csrf())
				.param("status", LessonStatus.MOVED.name())
				.param("courseId", lessonReadDto.getCourse().getId().toString())
				.param("roomId", lessonReadDto.getRoom().getId().toString())
				.param("from", lessonReadDto.getDate().toString())
				.param("startTime", lessonReadDto.getStartTime().toString())
				.param("endTime", lessonReadDto.getEndTime().toString())).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/teacher/lessonsSchedule?page=0&size=10&from=2026-12-30"));

		assertFalse(oldStatus.equals(newStatus));
	}

	@Test
	@WithMockUser(username = "teacher1", roles = { "TEACHER" })
	void teacherScheduleExportTest() throws Exception {
		mockMvc.perform(get("/teacher/lessonsSchedule/export").with(csrf())).andExpect(status().isOk())
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=schedule.ics"))
				.andExpect(content().contentType("text/calendar"))
				.andExpect(content().string(Matchers.containsString("BEGIN:VCALENDAR")));
	}

}

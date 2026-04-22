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
import spring.dto.CourseReadDto;
import spring.dto.LessonReadDto;
import spring.dto.RoomReadDto;
import spring.service.CastomUserDetailsService;
import spring.service.LessonService;

@WebMvcTest(controllers = LessonController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class LessonControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private LessonService lessonService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void test() throws Exception {
		LessonReadDto lesson = new LessonReadDto();
		lesson.setId(UUID.randomUUID());

		CourseReadDto course = new CourseReadDto();
		course.setCourseName("qwe");
		lesson.setCourse(course);

		RoomReadDto room = new RoomReadDto();
		room.setNumber("1");
		lesson.setRoom(room);

		Page<LessonReadDto> page = new PageImpl<>(Arrays.asList(lesson));
		when(lessonService.getAllPageLessons(any(PageRequest.class))).thenReturn(page);

		mockMvc.perform(get("/lessons")).andExpect(status().isOk()).andExpect(view().name("lessons"))
				.andExpect(model().attributeExists("pageLesson")).andExpect(model().attribute("currentPage", 0))
				.andExpect(model().attribute("totalPage", 1));
	}

}

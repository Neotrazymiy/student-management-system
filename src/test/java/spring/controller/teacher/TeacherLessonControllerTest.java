package spring.controller.teacher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import spring.auxiliaryObjects.CreateObjects;
import spring.config.SecurityConfig;
import spring.dto.DepartmentReadDto;
import spring.dto.LessonAddEditDto;
import spring.dto.LessonReadDto;
import spring.model.DateFilter;
import spring.service.CalendarService;
import spring.service.CastomUserDetailsService;
import spring.service.DepartmentService;
import spring.service.IpBlockService;
import spring.service.LessonService;

@WebMvcTest(controllers = TeacherLessonController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class TeacherLessonControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private LessonService lessonService;

	@MockBean
	private DepartmentService departmentService;

	@MockBean
	private CalendarService calendarService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;
	
	@MockBean
	private IpBlockService ipBlockService;

	private CreateObjects createObjects = new CreateObjects();

	@Test
	@WithMockUser(username = "teacher", roles = { "TEACHER" })
	void teacherSchedule() throws Exception {
		LessonReadDto lessonReadDto = createObjects.createLessonDto();
		List<LessonReadDto> lessonReadDtos = new ArrayList<>();
		lessonReadDtos.add(lessonReadDto);
		Page<LessonReadDto> pageLesson = new PageImpl<>(lessonReadDtos);

		LessonAddEditDto lessonAddEditDto = createObjects.createLessonEditDto();

		DepartmentReadDto departmentReadDto = createObjects.createDepartmentDto();
		List<DepartmentReadDto> departmentReadDtos = new ArrayList<>();
		departmentReadDtos.add(departmentReadDto);

		when(lessonService.getPageTeacherSchedule(any(Pageable.class), eq("teacher"), any(LessonAddEditDto.class)))
				.thenReturn(pageLesson);
		when(departmentService.getAllDepartments()).thenReturn(departmentReadDtos);

		mockMvc.perform(get("/teacher/lessonsSchedule").param("from", lessonAddEditDto.getFrom().toString())
				.param("to", lessonAddEditDto.getTo().toString())
				.param("dateFilter", lessonAddEditDto.getDateFilter().toString())
				.param("departmentId", lessonAddEditDto.getDepartmentId().toString()))

				.andExpect(status().isOk()).andExpect(view().name("teacher/lessons/teacherSchedule"))
				.andExpect(model().attribute("pageLesson", pageLesson)).andExpect(model().attributeExists("filter"))
				.andExpect(model().attribute("currentPage", 0)).andExpect(model().attribute("pageSize", 10))
				.andExpect(model().attribute("totalPage", pageLesson.getTotalPages()))
				.andExpect(model().attribute("basePath", "/teacher/lessonsSchedule"))
				.andExpect(model().attribute("departments", departmentReadDtos));
	}

	@Test
	@WithMockUser(username = "teacher", roles = { "TEACHER" })
	void teacherUpdate() throws Exception {
		UUID lessonId = UUID.randomUUID();
		LessonAddEditDto lessonAddEditDto = createObjects.createLessonEditDto();
		lessonAddEditDto.setFrom(LocalDate.of(2027, 10, 12));
		lessonAddEditDto.setTo(LocalDate.of(2027, 11, 20));

		mockMvc.perform(post("/teacher/lessonsSchedule/{id}/update", lessonId).with(csrf()).param("page", "1")
				.param("size", "10").param("from", "2027-10-12").param("to", "2027-11-20").param("dateFilter", "DAY")
				.param("departmentId", lessonAddEditDto.getDepartmentId().toString()))

				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrlPattern("/teacher/lessonsSchedule**"))
				.andExpect(
						redirectedUrl("/teacher/lessonsSchedule?page=1&size=10" + "&from=2027-10-12" + "&to=2027-11-20"
								+ "&dateFilter=DAY" + "&departmentId=" + lessonAddEditDto.getDepartmentId()));

		ArgumentCaptor<LessonAddEditDto> captor = ArgumentCaptor.forClass(LessonAddEditDto.class);

		verify(lessonService).updateLesson(eq(lessonId), captor.capture());

		LessonAddEditDto captured = captor.getValue();

		assertEquals(LocalDate.of(2027, 10, 12), captured.getFrom());
		assertEquals(LocalDate.of(2027, 11, 20), captured.getTo());
		assertEquals(DateFilter.DAY, captured.getDateFilter());
		assertEquals(lessonAddEditDto.getDepartmentId(), captured.getDepartmentId());
	}

	@Test
	@WithMockUser(username = "teacher1", roles = { "TEACHER" })
	void teacherScheduleExport() throws Exception {
		LessonReadDto lessonReadDto = createObjects.createLessonDto();
		List<LessonReadDto> lessonReadDtos = new ArrayList<>();
		lessonReadDtos.add(lessonReadDto);

		LessonAddEditDto lessonAddEditDto = createObjects.createLessonEditDto();
		byte[] icsBytes = "CALENDAR".getBytes();

		when(lessonService.getTeacherScheduleForExport(eq("teacher1"), any(LessonAddEditDto.class)))
				.thenReturn(lessonReadDtos);
		when(calendarService.createCalendar(lessonReadDtos)).thenReturn(icsBytes);

		mockMvc.perform(get("/teacher/lessonsSchedule/export").param("from", lessonAddEditDto.getFrom().toString())
				.param("to", lessonAddEditDto.getTo().toString())
				.param("dateFilter", lessonAddEditDto.getDateFilter().toString())
				.param("departmentId", lessonAddEditDto.getDepartmentId().toString())).andExpect(status().isOk())
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=schedule.ics"))
				.andExpect(content().contentType("text/calendar")).andExpect(content().bytes(icsBytes));
	}

}

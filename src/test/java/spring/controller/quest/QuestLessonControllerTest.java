package spring.controller.quest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

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

import spring.auxiliaryObjects.CreateObjects;
import spring.config.SecurityConfig;
import spring.dto.CourseReadDto;
import spring.dto.GroupReadDto;
import spring.dto.LessonAddEditDto;
import spring.dto.LessonReadDto;
import spring.dto.RoomReadDto;
import spring.dto.TeacherReadDto;
import spring.model.RoleElement;
import spring.service.CalendarService;
import spring.service.CastomUserDetailsService;
import spring.service.CourseService;
import spring.service.DepartmentService;
import spring.service.GroupService;
import spring.service.IpBlockService;
import spring.service.LessonService;
import spring.service.MethodistService;
import spring.service.RoomService;
import spring.service.StudentService;
import spring.service.TeacherService;

@WebMvcTest(controllers = QuestLessonController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class QuestLessonControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private LessonService lessonService;

	@MockBean
	private GroupService groupService;

	@MockBean
	private TeacherService teacherService;

	@MockBean
	private CourseService courseService;

	@MockBean
	private RoomService roomService;

	@MockBean
	private DepartmentService departmentService;

	@MockBean
	private StudentService studentService;

	@MockBean
	private MethodistService methodistService;

	@MockBean
	private CalendarService calendarService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;
	
	@MockBean
	private IpBlockService ipBlockService;

	private CreateObjects createObjects = new CreateObjects();

	@Test
	@WithMockUser(username = "quest", roles = { "QUEST" })
	void lesson() throws Exception {
		LessonReadDto lessonReadDto = createObjects.createLessonDto();
		List<LessonReadDto> lessonReadDtos = new ArrayList<>();
		lessonReadDtos.add(lessonReadDto);
		Page<LessonReadDto> pageLesson = new PageImpl<>(lessonReadDtos);

		LessonAddEditDto lessonAddEditDto = createObjects.createLessonEditDto();

		GroupReadDto groupReadDto = createObjects.createGroupDto();
		List<GroupReadDto> groupReadDtos = new ArrayList<>();
		groupReadDtos.add(groupReadDto);
		Page<GroupReadDto> pageGroup = new PageImpl<>(groupReadDtos);

		CourseReadDto courseReadDto = createObjects.createCourseDto();
		List<CourseReadDto> courseReadDtos = new ArrayList<>();
		courseReadDtos.add(courseReadDto);

		TeacherReadDto teacherReadDto = createObjects
				.createTeacherDto(createObjects.createUserDto(createObjects.createRoleDto(RoleElement.ROLE_TEACHER)));
		List<TeacherReadDto> teacherReadDtos = new ArrayList<>();
		teacherReadDtos.add(teacherReadDto);

		RoomReadDto roomReadDto = createObjects.createRoomDto();
		List<RoomReadDto> roomReadDtos = new ArrayList<>();
		roomReadDtos.add(roomReadDto);

		when(lessonService.getPageLessonsfilter(any(PageRequest.class), any(LessonAddEditDto.class)))
				.thenReturn(pageLesson);
		when(groupService.getAllPageGroups(any(PageRequest.class))).thenReturn(pageGroup);
		when(courseService.getAllCourses()).thenReturn(courseReadDtos);
		when(teacherService.getAllTeachers()).thenReturn(teacherReadDtos);
		when(roomService.getAllRooms()).thenReturn(roomReadDtos);

		mockMvc.perform(get("/quest/lessons").param("groupPage", "0").param("groupSize", "5")
				.param("from", lessonAddEditDto.getFrom().toString()).param("to", lessonAddEditDto.getTo().toString())
				.param("dateFilter", lessonAddEditDto.getDateFilter().toString())
				.param("courseId", lessonAddEditDto.getCourseId().toString())
				.param("teacherId", lessonAddEditDto.getTeacherId().toString())
				.param("roomId", lessonAddEditDto.getRoomId().toString())
				.param("groupId", lessonAddEditDto.getGroupId().toString()))

				.andExpect(status().isOk()).andExpect(view().name("quest/lessons/lessonsFilter"))
				.andExpect(model().attribute("pageLesson", pageLesson)).andExpect(model().attributeExists("filter"))
				.andExpect(model().attribute("currentPage", 0)).andExpect(model().attribute("pageSize", 10))
				.andExpect(model().attribute("totalPage", pageLesson.getTotalPages()))
				.andExpect(model().attribute("basePath", "/quest/lessons"))
				.andExpect(model().attribute("pageGroup", pageGroup))
				.andExpect(model().attribute("totalPages", pageGroup.getTotalPages()))
				.andExpect(model().attribute("courses", courseReadDtos))
				.andExpect(model().attribute("teachers", teacherReadDtos))
				.andExpect(model().attribute("rooms", roomReadDtos));
	}

}

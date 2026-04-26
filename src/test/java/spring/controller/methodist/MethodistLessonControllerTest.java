package spring.controller.methodist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import spring.auxiliaryObjects.CreateObjects;
import spring.config.SecurityConfig;
import spring.dto.CourseReadDto;
import spring.dto.GroupReadDto;
import spring.dto.LessonAddEditDto;
import spring.dto.LessonReadDto;
import spring.dto.RoomReadDto;
import spring.dto.StudentReadDto;
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

@WebMvcTest(controllers = MethodistLessonController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class MethodistLessonControllerTest {

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

	private static final UUID LESSON_ID = UUID.randomUUID();
	private CreateObjects createObjects = new CreateObjects();

	@Test
	@WithMockUser(username = "methodist", roles = { "METHODIST" })
	void getPageLessonTest() throws Exception {
		List<CourseReadDto> courseReadDtos = new ArrayList<>();
		courseReadDtos.add(createObjects.createCourseDto());

		List<StudentReadDto> studentReadDtos = new ArrayList<>();
		studentReadDtos.add(createObjects
				.createStudentDto(createObjects.createUserDto(createObjects.createRoleDto(RoleElement.ROLE_STUDENT))));

		List<GroupReadDto> groupReadDtos = new ArrayList<>();
		groupReadDtos.add(createObjects.createGroupDto());

		List<RoomReadDto> roomReadDtos = new ArrayList<>();
		roomReadDtos.add(createObjects.createRoomDto());

		List<LessonReadDto> dtos = new ArrayList<>();
		dtos.add(createObjects.createLessonDto());
		Page<LessonReadDto> pageLesson = new PageImpl<>(dtos);

		Map<UUID, LessonReadDto> map = new HashMap<>();
		map.put(dtos.get(0).getId(), dtos.get(0));

		when(lessonService.getAllPageLessons(any(PageRequest.class))).thenReturn(pageLesson);
		when(courseService.getAllCourses()).thenReturn(courseReadDtos);
		when(roomService.getAllRooms()).thenReturn(roomReadDtos);
		when(lessonService.getAllMapLessons(any(PageRequest.class))).thenReturn(map);
		when(studentService.getStudentByIds(any())).thenReturn(studentReadDtos);
		when(groupService.getGroupsByIds(any())).thenReturn(groupReadDtos);

		mockMvc.perform(get("/methodist/lessons")).andExpect(status().isOk())
				.andExpect(view().name("methodist/lessons/lessons")).andExpect(model().attributeExists("pageLesson"))
				.andExpect(model().attribute("currentPage", 0)).andExpect(model().attribute("pageSize", 10))
				.andExpect(model().attribute("totalPage", 1))
				.andExpect(model().attribute("basePath", "/methodist/lessons"))
				.andExpect(model().attribute("courses", courseReadDtos))
				.andExpect(model().attribute("rooms", roomReadDtos))
				.andExpect(model().attribute("lessonsStudents", map))
				.andExpect(model().attribute("newLesson", new LessonAddEditDto()))
				.andExpect(model().attributeExists("updateDto")).andExpect(model().attributeExists("dto"))
				.andExpect(model().attributeExists("studentsDto"))
				.andExpect(model().attributeExists("studentsUpdateDto")).andExpect(model().attributeExists("groupsDto"))
				.andExpect(model().attributeExists("groupsUpdateDto"))
				.andExpect(model().attributeExists("onlyChoiceStudentsNew"))
				.andExpect(model().attributeExists("onlyChoiceGroupsNew"))
				.andExpect(model().attributeExists("choiceStudentsOrGroups"))
				.andExpect(model().attributeExists("onlyChoiceGroupsUpdate"))
				.andExpect(model().attributeExists("onlyChoiceStudentsUpdate"));

		verify(lessonService).getAllPageLessons(any(PageRequest.class));
		verify(courseService).getAllCourses();
		verify(roomService).getAllRooms();
		verify(lessonService).getAllMapLessons(any(PageRequest.class));
		verify(studentService, times(2)).getStudentByIds(any());
		verify(groupService, times(2)).getGroupsByIds(any());
	}

	@Test
	@WithMockUser(username = "methodist", roles = { "METHODIST" })
	void updateLessonTest() throws Exception {
		LessonAddEditDto dto = createObjects.createLessonEditDto();

		mockMvc.perform(post("/methodist/lessons/{id}/update", LESSON_ID).with(csrf())
				.param("from", dto.getFrom().toString()).param("startTime", dto.getStartTime().toString())
				.param("endTime", dto.getEndTime().toString()).param("to", dto.getTo().toString())
				.param("studentIds", dto.getStudentIds().stream().map(UUID::toString).toArray(String[]::new))
				.param("dataFilter", dto.getDateFilter().toString()).param("status", dto.getStatus().toString())
				.param("departmentId", dto.getDepartmentId().toString()).param("courseId", dto.getCourseId().toString())
				.param("roomId", dto.getRoomId().toString()).param("teacherId", dto.getTeacherId().toString())
				.param("groupId", dto.getGroupId().toString()).param("lessonId", dto.getLessonId().toString()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/methodist/lessons?page=0&size=10"));

		ArgumentCaptor<LessonAddEditDto> captor = ArgumentCaptor.forClass(LessonAddEditDto.class);
		verify(lessonService).updateLesson(eq(LESSON_ID), captor.capture());

		assertEquals(dto.getCourseId(), captor.getValue().getCourseId());
	}

	@Test
	@WithMockUser(username = "methodist", roles = { "METHODIST" })
	void deleteLessonTest() throws Exception {
		when(lessonService.deleteLessonById(LESSON_ID)).thenReturn(true);
		mockMvc.perform(post("/methodist/lessons/{id}/delete", LESSON_ID).with(csrf()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/methodist/lessons?page=0&size=10"));
		verify(lessonService).deleteLessonById(LESSON_ID);
	}

	@Test
	@WithMockUser(username = "methodist", roles = { "METHODIST" })
	void choiceStudentLessonTest() throws Exception {
		List<UUID> uuids = new ArrayList<>();
		uuids.add(UUID.randomUUID());
		String source = "updateDto";
		LessonAddEditDto dto = createObjects.createLessonEditDto();
		List<StudentReadDto> studentReadDtos = new ArrayList<>();
		studentReadDtos.add(createObjects
				.createStudentDto(createObjects.createUserDto(createObjects.createRoleDto(RoleElement.ROLE_STUDENT))));
		Page<StudentReadDto> page = new PageImpl<>(studentReadDtos);

		when(methodistService.availableStudentsUpdate(any(LessonAddEditDto.class))).thenReturn(uuids);
		when(studentService.getPageStudentByIds(eq(uuids), any(Pageable.class))).thenReturn(page);
		when(studentService.getStudentByIds(dto.getStudentIds())).thenReturn(studentReadDtos);

		mockMvc.perform(post("/methodist/lessons/choiceStudents").with(csrf()).param("from", dto.getFrom().toString())
				.param("startTime", dto.getStartTime().toString()).param("endTime", dto.getEndTime().toString())
				.param("to", dto.getTo().toString())
				.param("studentIds", dto.getStudentIds().stream().map(UUID::toString).toArray(String[]::new))
				.param("groupIds", dto.getGroupIds().stream().map(UUID::toString).toArray(String[]::new))
				.param("dateFilter", dto.getDateFilter().toString()).param("status", dto.getStatus().toString())
				.param("departmentId", dto.getDepartmentId().toString()).param("courseId", dto.getCourseId().toString())
				.param("roomId", dto.getRoomId().toString()).param("teacherId", dto.getTeacherId().toString())
				.param("groupId", dto.getGroupId().toString()).param("lessonId", dto.getLessonId().toString())
				.param("source", source)).andExpect(status().isOk())
				.andExpect(view().name("methodist/lessons/addStudents"))
				.andExpect(model().attribute("pageStudent", page)).andExpect(model().attribute("currentPage", 0))
				.andExpect(model().attribute("pageSize", 10)).andExpect(model().attributeExists("totalPage"))
				.andExpect(model().attribute("basePath", "/methodist/lessons/choiceStudents"))
				.andExpect(model().attributeExists("currentStudents")).andExpect(model().attribute("dto", dto));

		verify(methodistService).availableStudentsUpdate(dto);
		verify(studentService).getPageStudentByIds(eq(uuids), any(Pageable.class));
		verify(studentService).getStudentByIds(dto.getStudentIds());

	}

	@Test
	@WithMockUser(username = "methodist", roles = { "METHODIST" })
	void returnStudentLesson() throws Exception {
		LessonAddEditDto dto = createObjects.createLessonEditDto();
		String source = "updateDto";
		mockMvc.perform(post("/methodist/lessons/returnStudents").with(csrf()).param("from", dto.getFrom().toString())
				.param("startTime", dto.getStartTime().toString()).param("endTime", dto.getEndTime().toString())
				.param("to", dto.getTo().toString())
				.param("studentIds", dto.getStudentIds().stream().map(UUID::toString).toArray(String[]::new))
				.param("groupIds", dto.getGroupIds().stream().map(UUID::toString).toArray(String[]::new))
				.param("dateFilter", dto.getDateFilter().toString()).param("status", dto.getStatus().toString())
				.param("departmentId", dto.getDepartmentId().toString()).param("courseId", dto.getCourseId().toString())
				.param("roomId", dto.getRoomId().toString()).param("teacherId", dto.getTeacherId().toString())
				.param("groupId", dto.getGroupId().toString()).param("lessonId", dto.getLessonId().toString())
				.param("source", source)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/methodist/lessons"))
				.andExpect(flash().attribute("dto", new LessonAddEditDto()))
				.andExpect(flash().attribute("updateDto", dto))
				.andExpect(result -> assertTrue(result.getFlashMap().containsKey("newLesson")));
	}

	@Test
	@WithMockUser(username = "methodist", roles = { "METHODIST" })
	void choiceGroupLessonTest() throws Exception {
		List<UUID> uuids = new ArrayList<>();
		uuids.add(UUID.randomUUID());
		String source = "updateDto";
		LessonAddEditDto dto = createObjects.createLessonEditDto();
		List<GroupReadDto> groupReadDtos = new ArrayList<>();
		groupReadDtos.add(createObjects.createGroupDto());
		Page<GroupReadDto> page = new PageImpl<>(groupReadDtos);

		when(methodistService.availableGroupsUpdate(any(LessonAddEditDto.class))).thenReturn(uuids);
		when(groupService.getPageGroupsByIds(eq(uuids), any(Pageable.class))).thenReturn(page);
		when(groupService.getGroupsByIds(dto.getStudentIds())).thenReturn(groupReadDtos);

		mockMvc.perform(post("/methodist/lessons/choiceGroups").with(csrf()).param("from", dto.getFrom().toString())
				.param("startTime", dto.getStartTime().toString()).param("endTime", dto.getEndTime().toString())
				.param("to", dto.getTo().toString())
				.param("studentIds", dto.getStudentIds().stream().map(UUID::toString).toArray(String[]::new))
				.param("groupIds", dto.getGroupIds().stream().map(UUID::toString).toArray(String[]::new))
				.param("dateFilter", dto.getDateFilter().toString()).param("status", dto.getStatus().toString())
				.param("departmentId", dto.getDepartmentId().toString()).param("courseId", dto.getCourseId().toString())
				.param("roomId", dto.getRoomId().toString()).param("teacherId", dto.getTeacherId().toString())
				.param("groupId", dto.getGroupId().toString()).param("lessonId", dto.getLessonId().toString())
				.param("source", source)).andExpect(status().isOk())
				.andExpect(view().name("methodist/lessons/addGroups")).andExpect(model().attribute("pageGroup", page))
				.andExpect(model().attribute("currentPage", 0)).andExpect(model().attribute("pageSize", 10))
				.andExpect(model().attributeExists("totalPage"))
				.andExpect(model().attribute("basePath", "/methodist/lessons/choiceGroups"))
				.andExpect(model().attributeExists("currentGroups")).andExpect(model().attribute("dto", dto));

		verify(methodistService).availableGroupsUpdate(dto);
		verify(groupService).getPageGroupsByIds(eq(uuids), any(Pageable.class));
		verify(groupService).getGroupsByIds(dto.getStudentIds());

	}

	@Test
	@WithMockUser(username = "methodist", roles = { "METHODIST" })
	void returnGroupLesson() throws Exception {
		LessonAddEditDto dto = createObjects.createLessonEditDto();
		String source = "updateDto";
		mockMvc.perform(post("/methodist/lessons/returnGroups").with(csrf()).param("from", dto.getFrom().toString())
				.param("startTime", dto.getStartTime().toString()).param("endTime", dto.getEndTime().toString())
				.param("to", dto.getTo().toString())
				.param("studentIds", dto.getStudentIds().stream().map(UUID::toString).toArray(String[]::new))
				.param("groupIds", dto.getGroupIds().stream().map(UUID::toString).toArray(String[]::new))
				.param("dateFilter", dto.getDateFilter().toString()).param("status", dto.getStatus().toString())
				.param("departmentId", dto.getDepartmentId().toString()).param("courseId", dto.getCourseId().toString())
				.param("roomId", dto.getRoomId().toString()).param("teacherId", dto.getTeacherId().toString())
				.param("groupId", dto.getGroupId().toString()).param("lessonId", dto.getLessonId().toString())
				.param("source", source)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/methodist/lessons"))
				.andExpect(flash().attribute("dto", new LessonAddEditDto()))
				.andExpect(flash().attribute("updateDto", dto))
				.andExpect(result -> assertTrue(result.getFlashMap().containsKey("newLesson")));
	}

	@Test
	@WithMockUser(username = "methodist", roles = { "METHODIST" })
	void createLessonForStudentsTest() throws Exception {
		LessonAddEditDto dto = createObjects.createLessonEditDto();

		mockMvc.perform(post("/methodist/lessons/new").with(csrf()).param("from", dto.getFrom().toString())
				.param("startTime", dto.getStartTime().toString()).param("endTime", dto.getEndTime().toString())
				.param("to", dto.getTo().toString())
				.param("studentIds", dto.getStudentIds().stream().map(UUID::toString).toArray(String[]::new))
				.param("dataFilter", dto.getDateFilter().toString()).param("status", dto.getStatus().toString())
				.param("departmentId", dto.getDepartmentId().toString()).param("courseId", dto.getCourseId().toString())
				.param("roomId", dto.getRoomId().toString()).param("teacherId", dto.getTeacherId().toString())
				.param("groupId", dto.getGroupId().toString()).param("lessonId", dto.getLessonId().toString()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/methodist/lessons?page=0&size=10"));

		ArgumentCaptor<LessonAddEditDto> captor = ArgumentCaptor.forClass(LessonAddEditDto.class);
		verify(lessonService).addLessonForStudents(captor.capture());

		assertEquals(dto.getCourseId(), captor.getValue().getCourseId());
	}

	@Test
	@WithMockUser(username = "methodist", roles = { "METHODIST" })
	void createLessonForGroupsTest() throws Exception {
		LessonAddEditDto dto = createObjects.createLessonEditDto();

		mockMvc.perform(post("/methodist/lessons/new").with(csrf()).param("from", dto.getFrom().toString())
				.param("startTime", dto.getStartTime().toString()).param("endTime", dto.getEndTime().toString())
				.param("to", dto.getTo().toString())
				.param("groupIds", dto.getGroupIds().stream().map(UUID::toString).toArray(String[]::new))
				.param("dataFilter", dto.getDateFilter().toString()).param("status", dto.getStatus().toString())
				.param("departmentId", dto.getDepartmentId().toString()).param("courseId", dto.getCourseId().toString())
				.param("roomId", dto.getRoomId().toString()).param("teacherId", dto.getTeacherId().toString())
				.param("groupId", dto.getGroupId().toString()).param("lessonId", dto.getLessonId().toString()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/methodist/lessons?page=0&size=10"));

		ArgumentCaptor<LessonAddEditDto> captor = ArgumentCaptor.forClass(LessonAddEditDto.class);
		verify(lessonService).addLessonForGroups(captor.capture());

		assertEquals(dto.getCourseId(), captor.getValue().getCourseId());
	}

}

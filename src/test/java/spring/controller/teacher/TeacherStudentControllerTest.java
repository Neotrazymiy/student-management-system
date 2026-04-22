package spring.controller.teacher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;
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

import spring.auxiliaryObjects.CreateObjects;
import spring.config.SecurityConfig;
import spring.dto.GroupReadDto;
import spring.dto.StudentReadDto;
import spring.model.RoleElement;
import spring.service.CastomUserDetailsService;
import spring.service.GroupService;
import spring.service.StudentService;

@WebMvcTest(controllers = TeacherStudentController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class TeacherStudentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StudentService studentService;

	@MockBean
	private GroupService groupService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;

	private CreateObjects createObjects = new CreateObjects();

	@Test
	@WithMockUser(username = "teacher", roles = { "TEACHER" })
	void getPageStudent() throws Exception {
		StudentReadDto studentReadDto = createObjects
				.createStudentDto(createObjects.createUserDto(createObjects.createRoleDto(RoleElement.ROLE_STUDENT)));
		List<StudentReadDto> dtos = new ArrayList<>();
		dtos.add(studentReadDto);

		GroupReadDto groupReadDto = createObjects.createGroupDto();
		UUID groupId = groupReadDto.getId();
		List<GroupReadDto> dtos2 = new ArrayList<>();
		dtos2.add(groupReadDto);

		Page<StudentReadDto> page = new PageImpl<>(dtos);
		Page<GroupReadDto> page2 = new PageImpl<>(dtos2);

		when(studentService.getAllPageStudents(any(PageRequest.class), eq(groupId))).thenReturn(page);
		when(groupService.getAllPageGroups(any(PageRequest.class))).thenReturn(page2);

		mockMvc.perform(get("/teacher/students").param("groupPage", "0").param("groupSize", "5").param("groupId",
				groupId.toString())).andExpect(status().isOk()).andExpect(view().name("teacher/students/students"))
				.andExpect(model().attributeExists("pageStudent")).andExpect(model().attribute("currentPage", 0))
				.andExpect(model().attribute("pageSize", 10)).andExpect(model().attribute("totalPage", 1))
				.andExpect(model().attribute("basePath", "/teacher/students"));
	}

}

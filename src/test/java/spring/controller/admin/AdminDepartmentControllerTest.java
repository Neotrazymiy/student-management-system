package spring.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import spring.auxiliaryObjects.CreateObjects;
import spring.config.SecurityConfig;
import spring.dto.DepartmentAddEditDto;
import spring.dto.DepartmentReadDto;
import spring.dto.FacultyReadDto;
import spring.exception.DeleteException;
import spring.service.CastomUserDetailsService;
import spring.service.DepartmentService;
import spring.service.FacultyService;
import spring.service.IpBlockService;

@WebMvcTest(controllers = AdminDepartmentController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class AdminDepartmentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FacultyService facultyService;

	@MockBean
	private DepartmentService departmentService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;
	
	@MockBean
	private IpBlockService ipBlockService;

	private static final String NAME = "namenamename";
	private static final UUID DEPARTMENT_ID = UUID.randomUUID();
	private CreateObjects createObjects = new CreateObjects();

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void getDepartmentTest() throws Exception {
		DepartmentReadDto departmentReadDto = createObjects.createDepartmentDto();
		List<DepartmentReadDto> departmentReadDtos = new ArrayList<>();
		departmentReadDtos.add(departmentReadDto);

		FacultyReadDto facultyReadDto = createObjects.createFacultyDto();
		List<FacultyReadDto> facultyReadDtos = new ArrayList<>();
		facultyReadDtos.add(facultyReadDto);

		when(departmentService.getAllDepartments()).thenReturn(departmentReadDtos);
		when(facultyService.getAllFaculty()).thenReturn(facultyReadDtos);

		mockMvc.perform(get("/admin/departments")).andExpect(status().isOk())
				.andExpect(view().name("admin/departments/departments"))
				.andExpect(model().attribute("departments", departmentReadDtos))
				.andExpect(model().attribute("facultys", facultyReadDtos))
				.andExpect(model().attributeExists("newDepartment"));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateDepartmentTest() throws Exception {
		UUID facultyId = UUID.randomUUID();

		mockMvc.perform(post("/admin/departments/{id}/update", DEPARTMENT_ID).with(csrf()).param("name", NAME)
				.param("facultyId", facultyId.toString())).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/departments"));

		ArgumentCaptor<DepartmentAddEditDto> captor = ArgumentCaptor.forClass(DepartmentAddEditDto.class);
		verify(departmentService).updateDepartment(eq(DEPARTMENT_ID), captor.capture());

		assertThat(NAME).isEqualTo(captor.getValue().getName());
		assertThat(facultyId).isEqualTo(captor.getValue().getFacultyId());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void deleteDepartmentTest() throws Exception {
		mockMvc.perform(post("/admin/departments/{id}/delete", DEPARTMENT_ID).with(csrf()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/departments"));
		verify(departmentService).deleteDepartmentById(DEPARTMENT_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void deleteDepartment_MyException_Test() throws Exception {
		doThrow(new DeleteException(NAME)).when(departmentService).deleteDepartmentById(DEPARTMENT_ID);

		mockMvc.perform(post("/admin/departments/{id}/delete", DEPARTMENT_ID).with(csrf())).andExpect(status().isOk())
				.andExpect(view().name("admin/exception")).andExpect(model().attribute("exception", NAME));
		verify(departmentService).deleteDepartmentById(DEPARTMENT_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void createDepartmentTest() throws Exception {
		DepartmentAddEditDto departmentAddEditDto = new DepartmentAddEditDto();
		departmentAddEditDto.setName(NAME);
		departmentAddEditDto.setFacultyId(UUID.randomUUID());

		mockMvc.perform(post("/admin/departments/new").with(csrf()).param("name", departmentAddEditDto.getName())
				.param("facultyId", departmentAddEditDto.getFacultyId().toString()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/departments"));

		ArgumentCaptor<DepartmentAddEditDto> captor = ArgumentCaptor.forClass(DepartmentAddEditDto.class);
		verify(departmentService).addDepartment(captor.capture());

		assertThat(departmentAddEditDto.getName()).isEqualTo(captor.getValue().getName());
		assertThat(departmentAddEditDto.getFacultyId()).isEqualTo(captor.getValue().getFacultyId());
	}

}

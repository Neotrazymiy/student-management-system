package spring.controller;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import spring.config.SecurityConfig;
import spring.dto.DepartmentReadDto;
import spring.dto.FacultyReadDto;
import spring.service.CastomUserDetailsService;
import spring.service.DepartmentService;

@WebMvcTest(controllers = DepartmentController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class DepartmentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DepartmentService departmentService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void test() throws Exception {
		DepartmentReadDto department = new DepartmentReadDto();
		department.setId(UUID.randomUUID());
		department.setName("qwe");

		FacultyReadDto faculty = new FacultyReadDto();
		faculty.setId(UUID.randomUUID());
		department.setFaculty(faculty);

		List<DepartmentReadDto> list = new ArrayList<>();
		list.add(department);

		when(departmentService.getAllDepartments()).thenReturn(list);
		mockMvc.perform(get("/departments")).andExpect(status().isOk()).andExpect(view().name("departments"))
				.andExpect(model().attributeExists("departments")).andExpect(model().attribute("departments", list));
	}

}

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
import spring.dto.FacultyAddEditDto;
import spring.dto.FacultyReadDto;
import spring.dto.UniversityReadDto;
import spring.exception.DeleteException;
import spring.service.CastomUserDetailsService;
import spring.service.FacultyService;
import spring.service.UniversityService;

@WebMvcTest(controllers = AdminFacultyController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class AdminFacultyControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UniversityService universityService;

	@MockBean
	private FacultyService facultyService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;

	private static final String NAME = "namenamename";
	private static final UUID FACULTY_ID = UUID.randomUUID();
	private CreateObjects createObjects = new CreateObjects();

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void getFacultyTest() throws Exception {
		List<UniversityReadDto> universityReadDtos = new ArrayList<>();
		universityReadDtos.add(createObjects.createUniversityDto());

		List<FacultyReadDto> facultyReadDtos = new ArrayList<>();
		facultyReadDtos.add(createObjects.createFacultyDto());

		when(universityService.getAllUniversitys()).thenReturn(universityReadDtos);
		when(facultyService.getAllFaculty()).thenReturn(facultyReadDtos);

		mockMvc.perform(get("/admin/facultys")).andExpect(status().isOk())
				.andExpect(view().name("admin/facultys/facultys"))
				.andExpect(model().attribute("universitys", universityReadDtos))
				.andExpect(model().attribute("facultys", facultyReadDtos))
				.andExpect(model().attributeExists("newFaculty"));

		verify(universityService).getAllUniversitys();
		verify(facultyService).getAllFaculty();
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateFacultyTest() throws Exception {
		UUID universityId = UUID.randomUUID();

		mockMvc.perform(post("/admin/facultys/{id}/update", FACULTY_ID).with(csrf()).param("name", NAME)
				.param("universityId", universityId.toString())).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/facultys"));

		ArgumentCaptor<FacultyAddEditDto> captor = ArgumentCaptor.forClass(FacultyAddEditDto.class);
		verify(facultyService).updateFaculty(eq(FACULTY_ID), captor.capture());

		assertThat(NAME).isEqualTo(captor.getValue().getName());
		assertThat(universityId).isEqualTo(captor.getValue().getUniversityId());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void deleteFacultyTest() throws Exception {
		mockMvc.perform(post("/admin/facultys/{id}/delete", FACULTY_ID).with(csrf()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/facultys"));
		verify(facultyService).deleteFacultyById(FACULTY_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void deleteFaculty_MyException_Test() throws Exception {
		doThrow(new DeleteException(NAME)).when(facultyService).deleteFacultyById(FACULTY_ID);

		mockMvc.perform(post("/admin/facultys/{id}/delete", FACULTY_ID).with(csrf())).andExpect(status().isOk())
				.andExpect(view().name("admin/exception")).andExpect(model().attribute("exception", NAME));
		verify(facultyService).deleteFacultyById(FACULTY_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void createFaculty() throws Exception {
		FacultyAddEditDto facultyAddEditDto = new FacultyAddEditDto();
		facultyAddEditDto.setName(NAME);
		facultyAddEditDto.setUniversityId(UUID.randomUUID());

		mockMvc.perform(post("/admin/facultys/new").with(csrf()).param("name", facultyAddEditDto.getName())
				.param("universityId", facultyAddEditDto.getUniversityId().toString()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/facultys"));

		ArgumentCaptor<FacultyAddEditDto> captor = ArgumentCaptor.forClass(FacultyAddEditDto.class);
		verify(facultyService).addFaculty(captor.capture());

		assertThat(facultyAddEditDto.getName()).isEqualTo(captor.getValue().getName());
		assertThat(facultyAddEditDto.getUniversityId()).isEqualTo(captor.getValue().getUniversityId());
	}

}

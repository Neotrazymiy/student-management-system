package spring.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import spring.auxiliaryObjects.CreateObjects;
import spring.config.SecurityConfig;
import spring.dto.UniversityAddEditDto;
import spring.dto.UniversityReadDto;
import spring.exception.DeleteException;
import spring.model.University;
import spring.service.CastomUserDetailsService;
import spring.service.IpBlockService;
import spring.service.UniversityService;
import spring.service.importt.CsvService;
import spring.service.importt.XlsxService;

@WebMvcTest(controllers = AdminUniversityController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class AdminUniversityControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UniversityService universityService;

	@MockBean
	private XlsxService xlsxService;

	@MockBean
	private CsvService csvService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;

	@MockBean
	private IpBlockService ipBlockService;
	
	private static final String NAME = "namenamename";
	private static final UUID UNIVERSITY_ID = UUID.randomUUID();
	private CreateObjects createObjects = new CreateObjects();

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void getUniversityTest() throws Exception {
		List<UniversityReadDto> universityReadDtos = new ArrayList<>();
		universityReadDtos.add(createObjects.createUniversityDto());

		when(universityService.getAllUniversitys()).thenReturn(universityReadDtos);

		mockMvc.perform(get("/admin/universitys")).andExpect(status().isOk())
				.andExpect(view().name("admin/universitys/universitys"))
				.andExpect(model().attribute("universitys", universityReadDtos))
				.andExpect(model().attributeExists("newUniversity"));

		verify(universityService).getAllUniversitys();
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateUniversityTest() throws Exception {
		mockMvc.perform(post("/admin/universitys/{id}/update", UNIVERSITY_ID).with(csrf()).param("name", NAME))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/universitys"));

		ArgumentCaptor<UniversityAddEditDto> captor = ArgumentCaptor.forClass(UniversityAddEditDto.class);
		verify(universityService).updateUniversity(eq(UNIVERSITY_ID), captor.capture());

		assertThat(NAME).isEqualTo(captor.getValue().getName());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void deleteUniversityTest() throws Exception {
		mockMvc.perform(post("/admin/universitys/{id}/delete", UNIVERSITY_ID).with(csrf()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/universitys"));
		verify(universityService).deleteUniversityById(UNIVERSITY_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void deleteUniversity_MyException_Test() throws Exception {
		doThrow(new DeleteException(NAME)).when(universityService).deleteUniversityById(UNIVERSITY_ID);

		mockMvc.perform(post("/admin/universitys/{id}/delete", UNIVERSITY_ID).with(csrf())).andExpect(status().isOk())
				.andExpect(view().name("admin/exception")).andExpect(model().attribute("exception", NAME));
		verify(universityService).deleteUniversityById(UNIVERSITY_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void createUniversityTest() throws Exception {
		UniversityAddEditDto universityAddEditDto = new UniversityAddEditDto();
		universityAddEditDto.setName(NAME);

		mockMvc.perform(post("/admin/universitys/new").with(csrf()).param("name", universityAddEditDto.getName()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/universitys"));

		ArgumentCaptor<UniversityAddEditDto> captor = ArgumentCaptor.forClass(UniversityAddEditDto.class);
		verify(universityService).addUniversity(captor.capture());

		assertThat(universityAddEditDto.getName()).isEqualTo(captor.getValue().getName());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void importCsvTest() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "universitys.csv", "text/csv", "test".getBytes());

		mockMvc.perform(multipart("/admin/universitys/import").file(file).with(csrf()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/universitys"))
				.andExpect(flash().attributeExists("message"));

		verify(csvService).importCsv(file, University.class);
		verifyNoInteractions(xlsxService);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void importXlsxTest() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "universitys.xlsx",
				"test/sdffsd.ksmrbvksr-smkvse.skemfs/sefse", new byte[] { 1, 2, 3 });

		mockMvc.perform(multipart("/admin/universitys/import").file(file).with(csrf()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/universitys"))
				.andExpect(flash().attributeExists("message"));

		verify(xlsxService).importXlsx(file, University.class);
		verifyNoInteractions(csvService);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void importNotSupportiveFormatTest() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "universitys.text", "text/csv", "test".getBytes());

		mockMvc.perform(multipart("/admin/universitys/import").file(file).with(csrf()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/universitys"))
				.andExpect(flash().attribute("message", "Неподдерживаемый формат файла"));

		verifyNoInteractions(csvService, xlsxService);
	}

}

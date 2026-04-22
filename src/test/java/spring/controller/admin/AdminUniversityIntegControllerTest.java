package spring.controller.admin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayOutputStream;

import javax.transaction.Transactional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import spring.model.University;
import spring.repository.UniversityRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = { "/sql/clear_tables.sql", "/sql/V1_schema.sql", "/sql/V2_sample_data.sql" })
class AdminUniversityIntegControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UniversityRepository universityRepository;

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void importCsvTest() throws Exception {
		String csv = "code,name\nKNU,Київський національний університет";
		MockMultipartFile file = new MockMultipartFile("file", "universitys.csv", "text/csv", csv.getBytes());

		mockMvc.perform(multipart("/admin/universitys/import").file(file).with(csrf()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/universitys"))
				.andExpect(flash().attributeExists("message"));

		University university = universityRepository.findByCode("KNU").get();

		assertTrue("Київський національний університет".equals(university.getName()));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void importXlsxTest() throws Exception {
		byte[] xlsx = createUniversityXlsx();

		MockMultipartFile file = new MockMultipartFile("file", "universitys.xlsx",
				"test/sdffsd.ksmrbvksr-smkvse.skemfs/sefse", xlsx);

		mockMvc.perform(multipart("/admin/universitys/import").file(file).with(csrf()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/universitys"))
				.andExpect(flash().attributeExists("message"));

		University university = universityRepository.findByCode("KNU").get();

		assertTrue("Київський національний університет".equals(university.getName()));
	}

	private byte[] createUniversityXlsx() throws Exception {
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Universities");

			// header
			Row header = sheet.createRow(0);
			header.createCell(0).setCellValue("code");
			header.createCell(1).setCellValue("name");

			// data row
			Row row = sheet.createRow(1);
			row.createCell(0).setCellValue("KNU");
			row.createCell(1).setCellValue("Київський національний університет");

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			workbook.write(out);
			return out.toByteArray();
		}
	}
}

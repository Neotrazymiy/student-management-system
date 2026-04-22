package spring.service.importt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import spring.importt.XlsxImportRecord;
import spring.model.University;

@SpringBootTest
class XlsxServiceTest {

	@Autowired
	private XlsxService xlsxService;

	@MockBean
	private RecordService recordService;

	@Test
	void importXlsx_shouldCallRecordService() throws Exception {

		byte[] xlsxBytes = createTestXlsx();

		MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", xlsxBytes);

		xlsxService.importXlsx(file, University.class);

		verify(recordService, times(1)).importRecord(any(XlsxImportRecord.class), eq(University.class));
	}

	private byte[] createTestXlsx() throws Exception {
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet();

			// header
			Row header = sheet.createRow(0);
			header.createCell(0).setCellValue("code");
			header.createCell(1).setCellValue("name");

			// data row
			Row row = sheet.createRow(1);
			row.createCell(0).setCellValue("123");
			row.createCell(1).setCellValue("Test University");

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			workbook.write(out);
			return out.toByteArray();
		}
	}
}

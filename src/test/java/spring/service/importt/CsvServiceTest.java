package spring.service.importt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import spring.importt.ImportRecord;
import spring.model.University;

@SpringBootTest
class CsvServiceTest {

	@Autowired
	private CsvService csvService;

	@MockBean
	private RecordService recordService;

	@Test
	void importCsvTest() {
		MockMultipartFile file = new MockMultipartFile("file", "file.csv", "text/csv",
				"code,name\n123,Test University".getBytes());
		Class<?> clazz = University.class;

		csvService.importCsv(file, clazz);
		verify(recordService).importRecord(any(ImportRecord.class), eq(University.class));
	}

}

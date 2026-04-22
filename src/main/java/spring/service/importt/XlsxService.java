package spring.service.importt;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import spring.importt.XlsxImportRecord;

@Service
@AllArgsConstructor
public class XlsxService {

	private final RecordService recordService;

	public void importXlsx(MultipartFile file, Class<?> clazz) {
		try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

			Sheet sheet = workbook.getSheetAt(0);
			Row header = sheet.getRow(0);

			Map<String, Integer> headerIndex = new HashMap<>();
			for (Cell cell : header) {
				headerIndex.put(cell.getStringCellValue(), cell.getColumnIndex());
			}
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				recordService.importRecord(new XlsxImportRecord(sheet.getRow(i), headerIndex), clazz);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

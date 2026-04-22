package spring.service.importt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import spring.importt.CsvImportRecord;
import spring.importt.ImportRecord;

@Service
@AllArgsConstructor
public class CsvService {

	private final RecordService recordService;

	public void importCsv(MultipartFile file, Class<?> clazz) {
		try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

			for (CSVRecord record : records) {
				ImportRecord importRecord = new CsvImportRecord(record);
				recordService.importRecord(importRecord, clazz);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

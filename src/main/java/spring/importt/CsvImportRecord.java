package spring.importt;

import org.apache.commons.csv.CSVRecord;

public class CsvImportRecord implements ImportRecord {

	private final CSVRecord record;

	public CsvImportRecord(CSVRecord record) {
		this.record = record;
	}

	@Override
	public String get(String column) {
		return record.get(column);
	}
}

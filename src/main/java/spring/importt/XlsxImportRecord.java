package spring.importt;

import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class XlsxImportRecord implements ImportRecord {

	private final Row row;
	private final Map<String, Integer> headerIndex;

	public XlsxImportRecord(Row row, Map<String, Integer> headerIndex) {
		this.row = row;
		this.headerIndex = headerIndex;
	}

	@Override
	public String get(String column) {
		Cell cell = row.getCell(headerIndex.get(column));
		return cell == null ? null : cell.toString();
	}
}

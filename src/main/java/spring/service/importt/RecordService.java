package spring.service.importt;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import spring.importt.ImportRecord;
import spring.importt.ReferenceImporter;
import spring.model.BaseReferenceEntity;

@Service
@AllArgsConstructor
public class RecordService {

	private final ReferenceEntityService referenceEntityService;
	private final ReferenceImportService referenceImportService;

	protected <T extends BaseReferenceEntity> void importRecord(ImportRecord record, Class<?> clazz) {
		String code = record.get("code");

		ReferenceImporter<T> importer = referenceEntityService.get(clazz);

		referenceImportService.importEntity(code, importer.mapFields(record), importer.getRepository(),
				importer::createEntity);
	}

}

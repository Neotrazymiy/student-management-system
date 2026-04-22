package spring.service.importt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import spring.importt.ReferenceImporter;
import spring.model.BaseReferenceEntity;

@Service
public class ReferenceEntityService {

	private final Map<Class<?>, ReferenceImporter<?>> importers = new HashMap<>();

	public ReferenceEntityService(List<ReferenceImporter<?>> importersList) {
		for (ReferenceImporter<?> importer : importersList) {
			importers.put(importer.getEntityClass(), importer);
		}
	}

	@SuppressWarnings("unchecked")
	protected <T extends BaseReferenceEntity> ReferenceImporter<T> get(Class<?> clazz) {
		ReferenceImporter<?> importer = importers.get(clazz);
		if (importer == null) {
			throw new RuntimeException("Репозиторий для класса " + clazz.getSimpleName() + " отсутствует.");
		}
		return (ReferenceImporter<T>) importer;
	}

}

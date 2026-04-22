package spring.service.importt;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import spring.model.BaseReferenceEntity;
import spring.repository.CodeRepository;

@Service
public class ReferenceImportService {

	@SuppressWarnings("unchecked")
	@Transactional
	public <T extends BaseReferenceEntity> T importEntity(String code, Map<String, Object> fields,
			JpaRepository<T, UUID> repository, Supplier<T> supplier) {
		T entity;

		if (repository instanceof CodeRepository) {
			entity = ((CodeRepository<T>) repository).findByCode(code).orElseGet(supplier);
		} else {
			throw new RuntimeException("Repository does not support code lookup");
		}
		entity.setCode(code);

		fields.forEach((k, v) -> {
			try {
				Field field = entity.getClass().getDeclaredField(k);
				field.setAccessible(true);
				field.set(entity, v);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		return repository.save(entity);
	}

}

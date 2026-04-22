package spring.importt;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import spring.model.BaseReferenceEntity;

public interface ReferenceImporter<T extends BaseReferenceEntity> {

	Class<T> getEntityClass();

	JpaRepository<T, UUID> getRepository();

	T createEntity();

	Map<String, Object> mapFields(ImportRecord importRecord);

}

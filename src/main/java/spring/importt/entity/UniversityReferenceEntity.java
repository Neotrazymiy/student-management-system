package spring.importt.entity;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import spring.importt.ImportRecord;
import spring.importt.MapsFields;
import spring.importt.ReferenceImporter;
import spring.model.University;
import spring.repository.UniversityRepository;

@Service
@AllArgsConstructor
public class UniversityReferenceEntity implements ReferenceImporter<University> {

	private UniversityRepository universityRepository;
	private MapsFields mapsFields;

	@Override
	public Class<University> getEntityClass() {
		return University.class;
	}

	@Override
	public JpaRepository<University, UUID> getRepository() {
		return universityRepository;
	}

	@Override
	public University createEntity() {
		return new University();
	}

	@Override
	public Map<String, Object> mapFields(ImportRecord importRecord) {
		return mapsFields.getUniversityField(importRecord);
	}

}

package spring.repository;

import java.util.Optional;

import spring.model.BaseReferenceEntity;

public interface CodeRepository<T extends BaseReferenceEntity> {

	Optional<T> findByCode(String code);
}

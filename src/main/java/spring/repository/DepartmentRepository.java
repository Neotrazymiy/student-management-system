package spring.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import spring.model.Department;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {

	Optional<Department> findByName(String name);
}

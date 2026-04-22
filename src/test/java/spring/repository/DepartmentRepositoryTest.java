package spring.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import spring.model.Department;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = { "/sql/clear_tables.sql", "/sql/V1_schema.sql", "/sql/V2_sample_data.sql" })
class DepartmentRepositoryTest {

	@Autowired
	private DepartmentRepository departmentRepository;

	private static final String DEPARTMENT_NAME_ONE = "Department One";
	private static final String DEPARTMENT_NAME_THREE = "Department Three";
	private static final UUID DEPARTMENT_ID_NOT_EXISTING = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private static final UUID DEPARTMENT_ID_ONE = UUID.fromString("00000000-0000-0000-0000-000000000021");

	@Test
	void testAddDepartment() {
		Department department = departmentRepository.findByName(DEPARTMENT_NAME_ONE).get();
		department.setName(DEPARTMENT_NAME_THREE);
		assertTrue(departmentRepository.save(department).getName().equals(DEPARTMENT_NAME_THREE));
	}

	@Test
	void testGetDepartmentById_exists() {
		assertTrue(departmentRepository.findById(DEPARTMENT_ID_ONE).get().getName().equals(DEPARTMENT_NAME_ONE));
	}

	@Test
	void testGetDepartmentById_NotExists() {
		RuntimeException exception = assertThrows(RuntimeException.class, () -> departmentRepository
				.findById(DEPARTMENT_ID_NOT_EXISTING).map(Department::getName).orElseThrow(() -> {
					throw new RuntimeException("Такого id нет. " + DEPARTMENT_ID_NOT_EXISTING);
				}));
		assertTrue(exception.getMessage().contains("Такого id нет. " + DEPARTMENT_ID_NOT_EXISTING));
	}

	@Test
	void testGetDepartmentByName() {
		assertTrue(departmentRepository.findByName(DEPARTMENT_NAME_ONE).get().getName().equals(DEPARTMENT_NAME_ONE));
	}

	@Test
	void testGetAllDepartment() {
		List<Department> universities = departmentRepository.findAll();
		assertNotNull(universities);
		assertFalse(universities.isEmpty());
		assertTrue(2 == universities.size());
	}

	@Test
	@Transactional
	void testDeleteDepartmentById() {
		Department department = departmentRepository.findByName(DEPARTMENT_NAME_ONE).get();
		department.setName(DEPARTMENT_NAME_THREE);
		department = departmentRepository.save(department);
		departmentRepository.deleteById(department.getId());
		assertFalse(departmentRepository.existsById(department.getId()));
	}

}

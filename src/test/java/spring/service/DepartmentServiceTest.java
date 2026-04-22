package spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import spring.auxiliaryObjects.CreateObjects;
import spring.dto.DepartmentAddEditDto;
import spring.dto.DepartmentReadDto;
import spring.mapper.DepartmentAddEditMapper;
import spring.mapper.DepartmentReadMapper;
import spring.model.Department;
import spring.repository.DepartmentRepository;
import spring.repository.FacultyRepository;

@SpringBootTest
class DepartmentServiceTest {

	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private DepartmentAddEditMapper departmentAddEditMapper;

	@Autowired
	private DepartmentReadMapper departmentReadMapper;

	@MockBean
	private DepartmentRepository departmentRepository;

	@MockBean
	private FacultyRepository facultyRepository;

	private static final String NAME = "namenamename";
	private CreateObjects createObjects = new CreateObjects();

	@Test
	void testAddDepartment() {
		Department department = createObjects.createDepartment();
		DepartmentAddEditDto departmentEditDto = createObjects.createDepartmentEditDto();

		when(facultyRepository.findById(departmentEditDto.getFacultyId()))
				.thenReturn(Optional.of(department.getFaculty()));
		when(departmentRepository.save(any(Department.class))).thenReturn(department);

		DepartmentReadDto result = departmentService.addDepartment(departmentEditDto);

		assertThat(result).isNotNull();
		assertTrue(NAME.equals(result.getName()));
		assertThat(result.getName()).isEqualTo(NAME);

		verify(facultyRepository).findById(departmentEditDto.getFacultyId());
		verify(departmentRepository).save(any(Department.class));
	}

	@Test
	void testGetDepartmentById_exists() {
		Department department = createObjects.createDepartment();

		when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));

		DepartmentReadDto result = departmentService.getDepartmentById(department.getId()).get();

		assertTrue(NAME.equals(result.getName()));

		verify(departmentRepository).findById(department.getId());
	}

	@Test
	void testGetDepartmentById_NotExists() {
		UUID random = UUID.randomUUID();

		when(departmentRepository.findById(random)).thenReturn(Optional.empty());
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> departmentService.getDepartmentById(random));
		assertTrue(exception.getMessage().equals("Такого id нет. " + random));
	}

	@Test
	void testGetDepartmentByName() {
		Department department = createObjects.createDepartment();
		DepartmentAddEditDto departmentEditDto = createObjects.createDepartmentEditDto();

		when(departmentRepository.findByName(department.getName())).thenReturn(Optional.of(department));

		DepartmentReadDto result = departmentService.getDepartmentByName(departmentEditDto.getName()).get();

		assertTrue(NAME.equals(result.getName()));

		verify(departmentRepository).findByName(departmentEditDto.getName());
	}

	@Test
	void testGetAllDepartment() {
		Department department = createObjects.createDepartment();

		when(departmentRepository.findAll()).thenReturn(Arrays.asList(department));

		List<DepartmentReadDto> result = departmentService.getAllDepartments();

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertTrue(1 == result.size());
		assertTrue(NAME.equals(result.get(0).getName()));

		verify(departmentRepository).findAll();
	}

	@Test
	void testUpdateDepartment() {
		Department department = createObjects.createDepartment();
		DepartmentAddEditDto departmentEditDto = createObjects.createDepartmentEditDto();
		departmentEditDto.setName(NAME + NAME);
		departmentEditDto.setFacultyId(UUID.randomUUID());

		when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
		when(facultyRepository.findById(departmentEditDto.getFacultyId()))
				.thenReturn(Optional.of(department.getFaculty()));
		when(departmentRepository.saveAndFlush(department)).thenReturn(department);

		DepartmentReadDto result = departmentService.updateDepartment(department.getId(), departmentEditDto).get();

		assertEquals((NAME + NAME), (result.getName()));
		assertTrue((NAME + NAME).equals(result.getName()));

		verify(departmentRepository).findById(department.getId());
		verify(facultyRepository).findById(departmentEditDto.getFacultyId());
		verify(departmentRepository).saveAndFlush(department);
	}

	@Test
	void testDeleteDepartmentById() {
		Department department = createObjects.createDepartment();

		when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
		departmentService.deleteDepartmentById(department.getId());

		verify(departmentRepository).findById(department.getId());
		verify(departmentRepository).delete(department);
		verify(departmentRepository).flush();
	}

}

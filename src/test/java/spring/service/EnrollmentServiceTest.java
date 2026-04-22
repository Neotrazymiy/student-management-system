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
import spring.dto.EnrollmentAddEditDto;
import spring.dto.EnrollmentReadDto;
import spring.mapper.EnrollmentAddEditMapper;
import spring.mapper.EnrollmentReadMapper;
import spring.model.Enrollment;
import spring.repository.EnrollmentRepository;

@SpringBootTest
class EnrollmentServiceTest {

	@Autowired
	private EnrollmentService enrollmentService;

	@Autowired
	private EnrollmentAddEditMapper enrollmentAddEditMapper;

	@Autowired
	private EnrollmentReadMapper enrollmentReadMapper;

	@MockBean
	private EnrollmentRepository enrollmentRepository;

	private static final String GRADE = "namenamename";
	private CreateObjects createObjects = new CreateObjects();

	@Test
	void testAddEnrollment() {
		Enrollment enrollment = createObjects.createEnrollment();
		EnrollmentAddEditDto enrollmentEditDto = createObjects.createEnrollmentEditDto();

		when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

		EnrollmentReadDto result = enrollmentService.addEnrollment(enrollmentEditDto);

		assertThat(result).isNotNull();
		assertTrue(GRADE.equals(result.getGrade()));
		assertThat(result.getGrade()).isEqualTo(GRADE);

		verify(enrollmentRepository).save(any(Enrollment.class));
	}

	@Test
	void testGetEnrollmentById_exists() {
		Enrollment enrollment = createObjects.createEnrollment();

		when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));

		EnrollmentReadDto result = enrollmentService.getEnrollmentById(enrollment.getId()).get();

		assertTrue(GRADE.equals(result.getGrade()));

		verify(enrollmentRepository).findById(enrollment.getId());
	}

	@Test
	void testGetEnrollmentById_NotExists() {
		UUID random = UUID.randomUUID();
		when(enrollmentRepository.findById(random)).thenReturn(Optional.empty());
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> enrollmentService.getEnrollmentById(random));
		assertTrue(exception.getMessage().equals("Такого id нет. " + random));
	}

	@Test
	void testGetEnrollmentByGrade() {
		Enrollment enrollment = createObjects.createEnrollment();
		EnrollmentAddEditDto enrollmentEditDto = createObjects.createEnrollmentEditDto();

		when(enrollmentRepository.findByGrade(enrollment.getGrade())).thenReturn(Optional.of(enrollment));

		EnrollmentReadDto result = enrollmentService.getEnrollmentByGrade(enrollmentEditDto.getGrade()).get();

		assertTrue(GRADE.equals(result.getGrade()));

		verify(enrollmentRepository).findByGrade(enrollmentEditDto.getGrade());
	}

	@Test
	void testGetAllEnrollment() {
		Enrollment enrollment = createObjects.createEnrollment();

		when(enrollmentRepository.findAll()).thenReturn(Arrays.asList(enrollment));

		List<EnrollmentReadDto> result = enrollmentService.getAllEnrollments();

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertTrue(1 == result.size());
		assertTrue(GRADE.equals(result.get(0).getGrade()));

		verify(enrollmentRepository).findAll();
	}

	@Test
	void testUpdateEnrollment() {
		Enrollment enrollment = createObjects.createEnrollment();
		EnrollmentAddEditDto enrollmentEditDto = createObjects.createEnrollmentEditDto();
		enrollmentEditDto.setGrade(GRADE + GRADE);

		when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
		when(enrollmentRepository.saveAndFlush(enrollment)).thenReturn(enrollment);

		EnrollmentReadDto result = enrollmentService.updateEnrollment(enrollment.getId(), enrollmentEditDto).get();

		assertEquals((GRADE + GRADE), (result.getGrade()));
		assertTrue((GRADE + GRADE).equals(result.getGrade()));

		verify(enrollmentRepository).findById(enrollment.getId());
		verify(enrollmentRepository).saveAndFlush(enrollment);
	}

	@Test
	void testDeleteEnrollmentById() {
		Enrollment enrollment = createObjects.createEnrollment();

		when(enrollmentRepository.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));

		assertTrue(enrollmentService.deleteEnrollmentById(enrollment.getId()));

		verify(enrollmentRepository).findById(enrollment.getId());
		verify(enrollmentRepository).delete(enrollment);
	}

}

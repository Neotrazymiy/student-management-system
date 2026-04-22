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
import spring.dto.FacultyAddEditDto;
import spring.dto.FacultyReadDto;
import spring.mapper.FacultyAddEditMapper;
import spring.mapper.FacultyReadMapper;
import spring.model.Faculty;
import spring.repository.FacultyRepository;
import spring.repository.UniversityRepository;

@SpringBootTest
class FacultyServiceTest {

	@Autowired
	private FacultyService facultyService;

	@Autowired
	private FacultyAddEditMapper facultyAddEditMapper;

	@Autowired
	private FacultyReadMapper facultyReadMapper;

	@MockBean
	private FacultyRepository facultyRepository;

	@MockBean
	private UniversityRepository universityRepository;

	private static final String NAME = "namenamename";
	private CreateObjects createObjects = new CreateObjects();

	@Test
	void testAddFaculty() {
		Faculty faculty = createObjects.createFaculty();
		FacultyAddEditDto facultyEditDto = createObjects.createFacultyEditDto();

		when(universityRepository.findById(facultyEditDto.getUniversityId()))
				.thenReturn(Optional.of(faculty.getUniversity()));
		when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

		FacultyReadDto result = facultyService.addFaculty(facultyEditDto);

		assertThat(result).isNotNull();
		assertTrue(NAME.equals(result.getName()));
		assertThat(result.getName()).isEqualTo(NAME);

		verify(facultyRepository).save(any(Faculty.class));
		verify(universityRepository).findById(facultyEditDto.getUniversityId());
	}

	@Test
	void testGetFacultyById_exists() {
		Faculty faculty = createObjects.createFaculty();

		when(facultyRepository.findById(faculty.getId())).thenReturn(Optional.of(faculty));

		FacultyReadDto result = facultyService.getFacultyById(faculty.getId()).get();

		assertTrue(NAME.equals(result.getName()));

		verify(facultyRepository).findById(faculty.getId());
	}

	@Test
	void testGetFacultyById_NotExists() {
		UUID random = UUID.randomUUID();

		when(facultyRepository.findById(random)).thenReturn(Optional.empty());
		RuntimeException exception = assertThrows(RuntimeException.class, () -> facultyService.getFacultyById(random));
		assertTrue(exception.getMessage().equals("Такого id нет. " + random));
	}

	@Test
	void testGetFacultyByName() {
		Faculty faculty = createObjects.createFaculty();
		FacultyAddEditDto facultyEditDto = createObjects.createFacultyEditDto();

		when(facultyRepository.findByName(faculty.getName())).thenReturn(Optional.of(faculty));

		FacultyReadDto result = facultyService.getFacultyByName(facultyEditDto.getName()).get();

		assertTrue(NAME.equals(result.getName()));

		verify(facultyRepository).findByName(facultyEditDto.getName());
	}

	@Test
	void testGetAllFaculty() {
		Faculty faculty = createObjects.createFaculty();

		when(facultyRepository.findAll()).thenReturn(Arrays.asList(faculty));

		List<FacultyReadDto> result = facultyService.getAllFaculty();

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertTrue(1 == result.size());
		assertTrue(NAME.equals(result.get(0).getName()));

		verify(facultyRepository).findAll();
	}

	@Test
	void testUpdateFaculty() {
		Faculty faculty = createObjects.createFaculty();
		FacultyAddEditDto facultyEditDto = createObjects.createFacultyEditDto();
		facultyEditDto.setName(NAME + NAME);
		facultyEditDto.setUniversityId(UUID.randomUUID());

		when(facultyRepository.findById(faculty.getId())).thenReturn(Optional.of(faculty));
		when(universityRepository.findById(facultyEditDto.getUniversityId()))
				.thenReturn(Optional.of(faculty.getUniversity()));
		when(facultyRepository.saveAndFlush(faculty)).thenReturn(faculty);

		FacultyReadDto result = facultyService.updateFaculty(faculty.getId(), facultyEditDto).get();

		assertEquals((NAME + NAME), (result.getName()));
		assertTrue((NAME + NAME).equals(result.getName()));

		verify(facultyRepository).findById(faculty.getId());
		verify(universityRepository).findById(facultyEditDto.getUniversityId());
		verify(facultyRepository).saveAndFlush(faculty);
	}

	@Test
	void testDeleteFacultyById() {
		Faculty faculty = createObjects.createFaculty();

		when(facultyRepository.findById(faculty.getId())).thenReturn(Optional.of(faculty));
		facultyService.deleteFacultyById(faculty.getId());

		verify(facultyRepository).findById(faculty.getId());
		verify(facultyRepository).delete(faculty);
		verify(facultyRepository).flush();
	}

}

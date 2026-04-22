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
import spring.dto.UniversityAddEditDto;
import spring.dto.UniversityReadDto;
import spring.mapper.UniversityAddEditMapper;
import spring.mapper.UniversityReadMapper;
import spring.model.University;
import spring.repository.UniversityRepository;

@SpringBootTest
class UniversityServiceTest {

	@Autowired
	private UniversityService universityService;

	@Autowired
	private UniversityAddEditMapper universityAddEditMapper;

	@Autowired
	private UniversityReadMapper universityReadMapper;

	@MockBean
	private UniversityRepository universityRepository;

	private static final String NAME = "namenamename";
	private CreateObjects createObjects = new CreateObjects();

	@Test
	void testAddUniversity() {
		University university = createObjects.createUniversity();
		UniversityAddEditDto universityEditDto = createObjects.createUniversityEditDto();

		when(universityRepository.save(any(University.class))).thenReturn(university);

		UniversityReadDto result = universityService.addUniversity(universityEditDto);

		assertThat(result).isNotNull();
		assertTrue(NAME.equals(result.getName()));
		assertThat(result.getName()).isEqualTo(NAME);

		verify(universityRepository).save(any(University.class));
	}

	@Test
	void testGetUniversityById_exists() {
		University university = createObjects.createUniversity();

		when(universityRepository.findById(university.getId())).thenReturn(Optional.of(university));

		UniversityReadDto result = universityService.getUniversityById(university.getId()).get();

		assertTrue(NAME.equals(result.getName()));

		verify(universityRepository).findById(university.getId());
	}

	@Test
	void testGetUniversityById_NotExists() {
		UUID random = UUID.randomUUID();

		when(universityRepository.findById(random)).thenReturn(Optional.empty());
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> universityService.getUniversityById(random));
		assertTrue(exception.getMessage().equals("Такого id нет. " + random));
	}

	@Test
	void testGetUniversityByName() {
		University university = createObjects.createUniversity();
		UniversityAddEditDto universityEditDto = createObjects.createUniversityEditDto();

		when(universityRepository.findByName(university.getName())).thenReturn(Optional.of(university));

		UniversityReadDto result = universityService.getUniversityByName(universityEditDto.getName()).get();

		assertTrue(NAME.equals(result.getName()));

		verify(universityRepository).findByName(universityEditDto.getName());
	}

	@Test
	void testGetAllUniversity() {
		University university = createObjects.createUniversity();

		when(universityRepository.findAll()).thenReturn(Arrays.asList(university));

		List<UniversityReadDto> result = universityService.getAllUniversitys();

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertTrue(1 == result.size());
		assertTrue(NAME.equals(result.get(0).getName()));

		verify(universityRepository).findAll();
	}

	@Test
	void testUpdateUniversity() {
		University university = createObjects.createUniversity();
		UniversityAddEditDto universityEditDto = createObjects.createUniversityEditDto();
		universityEditDto.setName(NAME + NAME);

		when(universityRepository.findById(university.getId())).thenReturn(Optional.of(university));
		when(universityRepository.saveAndFlush(university)).thenReturn(university);

		UniversityReadDto result = universityService.updateUniversity(university.getId(), universityEditDto).get();

		assertEquals((NAME + NAME), (result.getName()));
		assertTrue((NAME + NAME).equals(result.getName()));

		verify(universityRepository).findById(university.getId());
		verify(universityRepository).saveAndFlush(university);
	}

	@Test
	void testDeleteUniversityById() {
		University university = createObjects.createUniversity();

		when(universityRepository.findById(university.getId())).thenReturn(Optional.of(university));
		universityService.deleteUniversityById(university.getId());

		verify(universityRepository).findById(university.getId());
		verify(universityRepository).delete(university);
	}

}

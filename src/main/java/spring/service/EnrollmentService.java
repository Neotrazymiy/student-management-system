package spring.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import spring.dto.EnrollmentAddEditDto;
import spring.dto.EnrollmentReadDto;
import spring.mapper.EnrollmentAddEditMapper;
import spring.mapper.EnrollmentReadMapper;
import spring.repository.EnrollmentRepository;

@Service
@AllArgsConstructor
public class EnrollmentService {

	private final EnrollmentRepository enrollmentRepository;
	private final EnrollmentAddEditMapper enrollmentAddEditMapper;
	private final EnrollmentReadMapper enrollmentReadMapper;

	@Transactional
	public EnrollmentReadDto addEnrollment(EnrollmentAddEditDto enrollment) {
		return Optional.of(enrollment).map(enrollmentAddEditMapper::toEntity).map(enrollmentRepository::save)
				.map(enrollmentReadMapper::toDto).orElseThrow(() -> new RuntimeException());
	}

	@Transactional(readOnly = true)
	public Optional<EnrollmentReadDto> getEnrollmentById(UUID id) {
		return Optional.ofNullable(enrollmentRepository.findById(id).map(enrollmentReadMapper::toDto)
				.orElseThrow(() -> new RuntimeException("Такого id нет. " + id)));
	}

	@Transactional(readOnly = true)
	public Optional<EnrollmentReadDto> getEnrollmentByGrade(String grade) {
		return enrollmentRepository.findByGrade(grade).map(enrollmentReadMapper::toDto);
	}

	@Transactional(readOnly = true)
	public List<EnrollmentReadDto> getAllEnrollments() {
		return enrollmentRepository.findAll().stream().map(enrollmentReadMapper::toDto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public Page<EnrollmentReadDto> getAllPageEnrollments(Pageable pageable) {
		return enrollmentRepository.findAll(pageable).map(enrollmentReadMapper::toDto);
	}

	@Transactional
	public Optional<EnrollmentReadDto> updateEnrollment(UUID id, EnrollmentAddEditDto updateEnrollment) {
		return enrollmentRepository.findById(id).map(entity -> {
			enrollmentAddEditMapper.updateEntityFromDto(updateEnrollment, entity);
			return enrollmentRepository.saveAndFlush(entity);
		}).map(enrollmentReadMapper::toDto);
	}

	@Transactional
	public boolean deleteEnrollmentById(UUID id) {
		return enrollmentRepository.findById(id).map(entity -> {
			enrollmentRepository.delete(entity);
			return true;
		}).orElse(false);
	}

}

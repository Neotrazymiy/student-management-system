package spring.mapper;

import org.mapstruct.Mapper;

import spring.dto.EnrollmentReadDto;
import spring.model.Enrollment;

@Mapper(componentModel = "spring", uses = { StudentReadMapper.class, CourseReadMapper.class })
public interface EnrollmentReadMapper {

	EnrollmentReadDto toDto(Enrollment enrollment);

}

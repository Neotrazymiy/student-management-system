package spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import spring.dto.EnrollmentAddEditDto;
import spring.model.Enrollment;

@Mapper(componentModel = "spring", uses = { StudentAddEditMapper.class, CourseAddEditMapper.class })
public interface EnrollmentAddEditMapper {

	@Mapping(target = "id", ignore = true)
	Enrollment toEntity(EnrollmentAddEditDto dto);

	EnrollmentAddEditDto toDto(Enrollment entity);

	@Mapping(target = "id", ignore = true)
	void updateEntityFromDto(EnrollmentAddEditDto dto, @MappingTarget Enrollment entity);

}

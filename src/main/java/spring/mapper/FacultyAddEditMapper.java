package spring.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import spring.dto.FacultyAddEditDto;
import spring.dto.FacultyReadDto;
import spring.model.Faculty;

@Mapper(componentModel = "spring")
public interface FacultyAddEditMapper {

	@Mapping(target = "university", ignore = true)
	@Mapping(target = "departments", ignore = true)
	@Mapping(target = "id", ignore = true)
	Faculty toEntity(FacultyAddEditDto dto);

	@InheritInverseConfiguration(name = "toEntity")
	FacultyReadDto toDto(Faculty dto);

	@Mapping(target = "university", ignore = true)
	@Mapping(target = "departments", ignore = true)
	@Mapping(target = "id", ignore = true)
	void updateEntityFromDto(FacultyAddEditDto dto, @MappingTarget Faculty entity);

}

package spring.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import spring.dto.UniversityAddEditDto;
import spring.model.University;

@Mapper(componentModel = "spring")
public interface UniversityAddEditMapper {

	@Mapping(target = "facultis", ignore = true)
	@Mapping(target = "id", ignore = true)
	University toEntity(UniversityAddEditDto dto);

	@InheritInverseConfiguration(name = "toEntity")
	UniversityAddEditDto toDto(University entity);

	@Mapping(target = "facultis", ignore = true)
	@Mapping(target = "id", ignore = true)
	void updateEntityFromDto(UniversityAddEditDto dto, @MappingTarget University entity);

}

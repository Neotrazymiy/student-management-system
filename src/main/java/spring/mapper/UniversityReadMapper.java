package spring.mapper;

import org.mapstruct.Mapper;

import spring.dto.UniversityReadDto;
import spring.model.University;

@Mapper(componentModel = "spring")
public interface UniversityReadMapper {

	UniversityReadDto toDto(University university);
}

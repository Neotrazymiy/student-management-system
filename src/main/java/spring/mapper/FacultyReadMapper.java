package spring.mapper;

import org.mapstruct.Mapper;

import spring.dto.FacultyReadDto;
import spring.model.Faculty;

@Mapper(componentModel = "spring", uses = UniversityReadMapper.class)
public interface FacultyReadMapper {

	FacultyReadDto toDto(Faculty faculty);
}

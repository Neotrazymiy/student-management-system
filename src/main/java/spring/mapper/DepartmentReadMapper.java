package spring.mapper;

import org.mapstruct.Mapper;

import spring.dto.DepartmentReadDto;
import spring.model.Department;

@Mapper(componentModel = "spring", uses = FacultyReadMapper.class)
public interface DepartmentReadMapper {

	DepartmentReadDto toDto(Department department);

}

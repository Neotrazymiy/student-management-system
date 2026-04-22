package spring.mapper;

import org.mapstruct.Mapper;

import spring.dto.StudentReadDto;
import spring.model.Student;

@Mapper(componentModel = "spring", uses = { UserReadMapper.class, GroupReadMapper.class })
public interface StudentReadMapper {

	StudentReadDto toDto(Student student);

}

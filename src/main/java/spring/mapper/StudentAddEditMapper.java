package spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import spring.dto.StudentAddEditDto;
import spring.model.Student;

@Mapper(componentModel = "spring", uses = UserAddEditMapper.class)
public abstract class StudentAddEditMapper {

	@Mapping(target = "group", ignore = true)
	@Mapping(target = "enrollments", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "lessons", ignore = true)
	public abstract Student toEntity(StudentAddEditDto dto);

	@Mapping(target = "groupId", source = "group.id")
	public abstract StudentAddEditDto toDto(Student entity);

	@Mapping(target = "group", ignore = true)
	@Mapping(target = "user", qualifiedByName = "updateUser")
	@Mapping(target = "enrollments", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "lessons", ignore = true)
	public abstract void updateEntityFromDto(StudentAddEditDto dto, @MappingTarget Student entity);

}

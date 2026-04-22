package spring.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import spring.dto.LessonReadDto;
import spring.model.Lesson;

@Mapper(componentModel = "spring", uses = { CourseReadMapper.class, RoomReadMapper.class, GroupReadMapper.class,
		StudentReadMapper.class, UserReadMapper.class })
public interface LessonReadMapper {

	LessonReadDto toDto(Lesson lesson);

	List<LessonReadDto> toDtoList(List<Lesson> lessons);

}

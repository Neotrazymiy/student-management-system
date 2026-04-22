package spring.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.model.LessonStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonReadDto {

	private UUID id;
	private LocalDate date;
	private LocalTime startTime;
	private LocalTime endTime;
	private LessonStatus status;
	private CourseReadDto course;
	private RoomReadDto room;
	private List<GroupReadDto> groups;
	private List<StudentReadDto> students;

}

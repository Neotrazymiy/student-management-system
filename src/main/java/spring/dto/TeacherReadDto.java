package spring.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherReadDto {

	private UUID id;
	private UserReadDto user;
	private List<LessonReadDto> lessons;
	private List<CourseReadDto> courses;
	private DepartmentReadDto department;

}

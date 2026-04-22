package spring.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseReadDto {

	private UUID id;
	private String courseName;
	private String description;
	private DepartmentReadDto department;
	private List<GroupReadDto> groups;

}

package spring.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentReadDto {

	private UUID id;
	private String name;
	private FacultyReadDto faculty;

}

package spring.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupReadDto {

	private UUID id;
	private String name;
	private DepartmentReadDto department;

}

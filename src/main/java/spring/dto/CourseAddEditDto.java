package spring.dto;

import java.util.UUID;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseAddEditDto {

	@Pattern(regexp = "^[A-Za-z]+$", message = "буквы должны быть только на латынице")
	@Size(min = 3, max = 34, message = "название предмета должено быть в диапазоне от 3 до 34 симвлов")
	private String courseName;

	@Pattern(regexp = "^[A-Za-z0-9]+$")
	private String description;

	private UUID departmentId;
	private UUID groupId;

}

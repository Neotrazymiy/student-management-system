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
public class DepartmentAddEditDto {

	@Pattern(regexp = "^[A-Za-z]+$", message = "буквы должны быть только на латынице")
	@Size(min = 8, max = 70, message = "название департамента должно быть в диапазоне от 8 до 70 симвлов")
	private String name;

	private UUID facultyId;

}

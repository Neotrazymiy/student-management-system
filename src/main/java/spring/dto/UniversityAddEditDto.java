package spring.dto;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UniversityAddEditDto {

	@Pattern(regexp = "^[A-Za-z]+$", message = "буквы должны быть только на латынице")
	@Size(min = 3, max = 100, message = "название университета должно быть в диапазоне от 3 до 100 симвлов")
	private String name;
}

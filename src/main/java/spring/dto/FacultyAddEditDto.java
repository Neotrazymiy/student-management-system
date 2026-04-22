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
public class FacultyAddEditDto {

	@Pattern(regexp = "^[A-Za-z]+$", message = "буквы должны быть только на латынице")
	@Size(min = 8, max = 50, message = "название факультета должно быть в диапазоне от 8 до 50 симвлов")
	private String name;

	private UUID universityId;
}

package spring.dto;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleAddEditDto {

	@Pattern(regexp = "^ROLE_[A-Z]+$", message = "буквы должны быть только большие на латынице, и начинаться с ROLE_")
	@Size(min = 9, max = 50, message = "название роли должно быть в диапазоне от 9 до 50 симвлов")
	private String name;

}

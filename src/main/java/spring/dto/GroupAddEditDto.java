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
public class GroupAddEditDto {

	@Pattern(regexp = "^[A-Za-z0-9]+$", message = "буквы должны быть только на латынице")
	@Size(min = 3, max = 15, message = "название группы должно быть в диапазоне от 3 до 15 симвлов")
	private String name;

	private UUID departmentId;

}

package spring.dto;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomAddEditDto {

	@Pattern(regexp = "^[1-9][0-9]*_(?:[A-Z]|[А-ЯЁ])$", message = "буквы должны быть только большие, а название начинаться с цыфры (число_БУКВА)")
	@Size(min = 3, max = 10, message = "название аудитории должно быть в диапазоне от 3 до 10 симвлов")
	private String number;

}

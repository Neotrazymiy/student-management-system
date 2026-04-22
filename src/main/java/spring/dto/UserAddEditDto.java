package spring.dto;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAddEditDto {

	@Email(message = "email должен иметь символ - @")
	@Size(min = 3, max = 25, message = "email должен быть в диапазоне от 3 до 25 симвлов")
	private String email;

	@Pattern(regexp = "^[A-Za-z0-9_]{3,}$")
	@Size(min = 3, max = 25, message = "логин должен быть в диапазоне от 3 до 25 симвлов")
	private String userName;

	private Boolean enabled;

	@Pattern(regexp = "^$|^[A-Za-z0-9]{3,25}$", message = "пароль должен быть от 3 до 25 символов")
	private String passwordHash;

	@Pattern(regexp = "^[А-Яа-яA-Za-z]{3,}$")
	@Size(min = 3, max = 25, message = "имя должено быть в диапазоне от 3 до 20 симвлов")
	private String firstName;

	@Pattern(regexp = "^[А-Яа-яA-Za-z]{3,}$")
	@Size(min = 3, max = 25, message = "фамилия должена быть в диапазоне от 3 до 20 симвлов")
	private String lastName;

	private List<RoleAddEditDto> roles;
	private List<UUID> roleIds;

}

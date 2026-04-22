package spring.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReadDto {

	private UUID id;
	private String userName;
	private String email;
	private Boolean enabled;
	private String passwordHash;
	private String firstName;
	private String lastName;
	private List<RoleReadDto> roles;

}

package spring.dto;

import java.util.UUID;

import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentAddEditDto {

	@Valid
	private UserAddEditDto user;

	private UUID groupId;

}

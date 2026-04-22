package spring.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentReadDto {

	private UUID id;
	private UserReadDto user;
	private GroupReadDto group;

}

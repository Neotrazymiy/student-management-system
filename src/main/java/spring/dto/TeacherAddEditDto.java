package spring.dto;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherAddEditDto {

	@Valid
	private UserAddEditDto user;

	private List<UUID> lessonIds;
	private List<UUID> courseIds;
	private UUID departmentId;

}

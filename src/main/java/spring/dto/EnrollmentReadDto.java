package spring.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.model.EnrollmentStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentReadDto {

	private UUID id;
	private String grade;
	private EnrollmentStatus status;
	private StudentReadDto student;
	private CourseReadDto course;

}

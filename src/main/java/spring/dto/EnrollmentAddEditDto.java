package spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.model.EnrollmentStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentAddEditDto {

	private String grade;
	private EnrollmentStatus status;
	private StudentAddEditDto student;
	private CourseAddEditDto course;

}

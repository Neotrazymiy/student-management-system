package spring.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "enrollment")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Enrollment {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(nullable = false)
	private String grade;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id")
	private Student student;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id")
	private Course course;
}

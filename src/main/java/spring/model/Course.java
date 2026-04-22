package spring.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "course")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Course {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(name = "course_name", nullable = false)
	private String courseName;

	@Column(name = "course_description")
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department_id")
	private Department department;

	@OneToMany(mappedBy = "course")
	private List<Enrollment> enrollments = new ArrayList<>();

	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Lesson> lessons = new ArrayList<>();

	@ManyToMany(mappedBy = "courses")
	private List<Teacher> teachers = new ArrayList<>();

	@ManyToMany(mappedBy = "courses")
	private List<Group> groups = new ArrayList<>();

}

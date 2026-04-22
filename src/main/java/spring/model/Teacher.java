package spring.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.auxiliaryObjects.HasUser;

@Table(name = "teacher")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Teacher implements HasUser { // HasUser для консьюмера в HalpMap

	@Id
	@GeneratedValue
	private UUID id;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToMany
	@JoinTable(name = "teacher_lesson", joinColumns = @JoinColumn(name = "teacher_id"), inverseJoinColumns = @JoinColumn(name = "lesson_id"))
	private List<Lesson> lessons = new ArrayList<>();

	@ManyToMany
	@JoinTable(name = "teacher_course", joinColumns = @JoinColumn(name = "teacher_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
	private List<Course> courses = new ArrayList<>();

	@OneToOne
	@JoinColumn(name = "department_id")
	private Department department;

}

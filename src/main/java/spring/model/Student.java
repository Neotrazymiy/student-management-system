package spring.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.auxiliaryObjects.HasUser;

@Table(name = "student")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Student implements HasUser { // HasUser для консьюмера в HalpMap

	@Id
	@GeneratedValue
	private UUID id;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "groupe_id")
	private Group group;

	@ManyToMany
	@JoinTable(name = "lesson_student_mapping", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "lesson_id"))
	private List<Lesson> lessons = new ArrayList<>();

	@OneToMany(mappedBy = "student")
	private List<Enrollment> enrollments = new ArrayList<>();
}

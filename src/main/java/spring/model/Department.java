package spring.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "department")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Department {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(nullable = false)
	private String name;

	@ManyToOne(optional = false)
	@JoinColumn(name = "faculty_id")
	private Faculty faculty;

	@OneToMany(mappedBy = "department")
	private List<Course> courses = new ArrayList<>();

	@OneToMany(mappedBy = "department")
	private List<Group> groups = new ArrayList<>();

	@OneToMany(mappedBy = "department")
	private List<Teacher> teachers = new ArrayList<>();

}

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

@Table(name = "faculty")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Faculty {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(nullable = false)
	private String name;

	@ManyToOne
	@JoinColumn(name = "university_id", nullable = false)
	private University university;

	@OneToMany(mappedBy = "faculty")
	private List<Department> departments = new ArrayList<>();
}

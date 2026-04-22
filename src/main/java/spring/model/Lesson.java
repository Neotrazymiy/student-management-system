package spring.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "lesson")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Lesson {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(nullable = false)
	private LocalDate date;

	@Column(name = "start_time", nullable = false)
	private LocalTime startTime;

	@Column(name = "end_time", nullable = false)
	private LocalTime endTime;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private LessonStatus status;

	@ManyToOne(optional = false)
	@JoinColumn(name = "course_id")
	private Course course;

	@ManyToOne(optional = false)
	@JoinColumn(name = "room_id")
	private Room room;

	@ManyToMany(mappedBy = "lessons")
	private List<Group> groups = new ArrayList<>();

	@ManyToMany(mappedBy = "lessons")
	private List<Teacher> teachers = new ArrayList<>();

	@ManyToMany(mappedBy = "lessons")
	private List<Student> students;

}

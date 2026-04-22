package spring.filter.lesson;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import spring.model.Lesson;

public class LessonDateSpecific {

	public static Specification<Lesson> dateBetween(LocalDate from, LocalDate to) {
		return (root, query, cb) -> from == null && to == null ? null : cb.between(root.get("date"), from, to);
	}

}

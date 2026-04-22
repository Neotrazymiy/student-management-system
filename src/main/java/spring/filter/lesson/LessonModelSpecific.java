package spring.filter.lesson;

import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import spring.model.Lesson;

public class LessonModelSpecific {

	public static Specification<Lesson> hasCourse(UUID courseId) {
		return (root, query, cb) -> courseId == null ? null : cb.equal(root.get("course").get("id"), courseId);
	}

	public static Specification<Lesson> hasTeacher(UUID teacherId) {
		return (root, query, cb) -> {
			if (teacherId == null) {
				return null;
			}
			query.distinct(true);
			return cb.equal(root.join("teachers").get("id"), teacherId);
		};
	}

	public static Specification<Lesson> hasGroup(UUID groupId) {
		return (root, query, cb) -> {
			if (groupId == null) {
				return null;
			}
			query.distinct(true);
			return cb.equal(root.join("groups").get("id"), groupId);
		};
	}

	public static Specification<Lesson> hasRoom(UUID roomId) {
		return (root, query, cb) -> roomId == null ? null : cb.equal(root.get("room").get("id"), roomId);
	}

	public static Specification<Lesson> hasDepartment(UUID departmentId) {
		return (root, query, cb) -> {
			if (departmentId == null) {
				return null;
			}
			query.distinct(true);
			return cb.equal(root.join("course").join("department").get("id"), departmentId);
		};
	}

}

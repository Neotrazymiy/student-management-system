package spring.auxiliaryObjects;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import spring.model.Role;
import spring.model.RoleElement;
import spring.model.Student;
import spring.model.Teacher;
import spring.model.User;
import spring.repository.StudentRepository;
import spring.repository.TeacherRepository;

@Service
@RequiredArgsConstructor
public class HelpMapsService {
	private final StudentRepository studentRepository;
	private final TeacherRepository teacherRepository;

	private final Map<RoleElement, Consumer<UUID>> deleteObjectFromRole = new HashMap<>();
	private final Map<RoleElement, Supplier<? extends HasUser>> createObject = new HashMap<>();
	private final Map<RoleElement, JpaRepository<? extends HasUser, UUID>> saveObject = new HashMap<>();
	private final Map<RoleElement, Function<UUID, Optional<? extends HasUser>>> findRoleFromEntityByUser = new EnumMap<>(
			RoleElement.class);

	@PostConstruct
	public void init() {
		deleteObjectFromRole.put(RoleElement.ROLE_STUDENT,
				userId -> studentRepository.findByUserId(userId).ifPresent(studentRepository::delete));
		deleteObjectFromRole.put(RoleElement.ROLE_TEACHER,
				userId -> teacherRepository.findByUserId(userId).ifPresent(teacherRepository::delete));

		createObject.put(RoleElement.ROLE_STUDENT, Student::new);
		createObject.put(RoleElement.ROLE_TEACHER, Teacher::new);

		saveObject.put(RoleElement.ROLE_STUDENT, studentRepository);
		saveObject.put(RoleElement.ROLE_TEACHER, teacherRepository);

		findRoleFromEntityByUser.put(RoleElement.ROLE_TEACHER, teacherRepository::findByUserId);
		findRoleFromEntityByUser.put(RoleElement.ROLE_STUDENT, studentRepository::findByUserId);

	}

	public boolean deleteObjectFrom(User user) {
		return user.getRoles().stream().map(role -> RoleElement.valueOf(role.getName())).map(deleteObjectFromRole::get)
				.filter(consumer -> consumer != null).map(consumer -> {
					consumer.accept(user.getId());
					return true;
				}).findAny().orElse(false);
	}

	public boolean roleWithoutObject(Role role) {
		if (role.getName().equals(RoleElement.ROLE_QUEST.name()) || role.getName().equals(RoleElement.ROLE_ADMIN.name())
				|| role.getName().equals(RoleElement.ROLE_METHODIST.name())) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("null")
	public HasUser createObject(Role role, User user) {
		RoleElement roleElement = RoleElement.valueOf(role.getName());
		Supplier<? extends HasUser> entityObject = createObject.get(roleElement);
		if (entityObject == null) {
			throw new RuntimeException("Роль не соответствует создаваемому объекту.");
		}
		HasUser entity = entityObject.get();
		entity.setUser(user);
		return entity;
	}

	@SuppressWarnings("unchecked")
	public void saveObject(Role role, HasUser hasUser) {
		RoleElement roleElement = RoleElement.valueOf(role.getName());
		JpaRepository<HasUser, UUID> entityObject = (JpaRepository<HasUser, UUID>) saveObject.get(roleElement);
		if (entityObject != null) {
			entityObject.save(hasUser);
		} else {
			throw new RuntimeException("По данной роли репозиторий не найден.");
		}
	}

	public Optional<RoleElement> findRoleFromObjectByUser(UUID userId) {
		return findRoleFromEntityByUser.entrySet().stream().filter(e -> e.getValue().apply(userId).isPresent())
				.map(Map.Entry::getKey).findFirst();
	}

}

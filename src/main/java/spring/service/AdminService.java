package spring.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import spring.auxiliaryObjects.HasUser;
import spring.auxiliaryObjects.HelpMapsService;
import spring.auxiliaryObjects.HelpsMethod;
import spring.dto.UserReadDto;
import spring.mapper.UserReadMapper;
import spring.model.Course;
import spring.model.Group;
import spring.model.Role;
import spring.model.RoleElement;
import spring.model.Student;
import spring.model.Teacher;
import spring.model.User;
import spring.repository.CourseRepository;
import spring.repository.GroupRepository;
import spring.repository.RoleRepository;
import spring.repository.StudentRepository;
import spring.repository.TeacherRepository;
import spring.repository.UserRepository;

@Service
@AllArgsConstructor
public class AdminService {

	private final UserRepository userRepository;
	private final StudentRepository studentRepository;
	private final RoleRepository roleRepository;
	private final TeacherRepository teacherRepository;
	private final GroupRepository groupRepository;
	private final UserReadMapper userReadMapper;
	private final CourseRepository courseRepository;
	private final HelpMapsService helpMapsService;
	private HelpsMethod helpsMethod;

	@Transactional
	public void makeStudentWithUser(UUID userId) {
		Student student = new Student();
		userRepository.findById(userId).ifPresent(student::setUser);
		List<Role> roles = student.getUser().getRoles();
		Role roleQuest = roleRepository.findByName(RoleElement.ROLE_QUEST.name())
				.orElseThrow(() -> new RuntimeException("Такой роли нет." + RoleElement.ROLE_QUEST.name()));
		if (roles.contains(roleQuest)) {
			roles.clear();
		}
		student.getUser().getRoles().add(roleRepository.findByName(RoleElement.ROLE_STUDENT.name())
				.orElseThrow(() -> new RuntimeException("Такой роли нет." + RoleElement.ROLE_STUDENT.name())));
		studentRepository.save(student);
	}

	@Transactional
	public void makeTeacherWithUser(UUID userId) {
		Teacher teacher = new Teacher();
		userRepository.findById(userId).ifPresent(teacher::setUser);
		List<Role> roles = teacher.getUser().getRoles();
		Role roleQuest = roleRepository.findByName(RoleElement.ROLE_QUEST.name())
				.orElseThrow(() -> new RuntimeException("Такой роли нет." + RoleElement.ROLE_QUEST.name()));
		if (roles.contains(roleQuest)) {
			roles.clear();
		}
		teacher.getUser().getRoles().add(roleRepository.findByName(RoleElement.ROLE_TEACHER.name())
				.orElseThrow(() -> new RuntimeException("Такой роли нет." + RoleElement.ROLE_TEACHER.name())));
		teacherRepository.save(teacher);
	}

	@Transactional
	public void deleteStudentAndRoleWithUser(UUID studentId) {
		Student student = studentRepository.findById(studentId)
				.orElseThrow(() -> new RuntimeException("Такого id нет. " + studentId));
		User user = student.getUser();
		user.getRoles().removeIf(role -> RoleElement.ROLE_STUDENT.name().equals(role.getName()));
		helpsMethod.set_ROLE_QUEST_IfRoleNull(user);
		studentRepository.delete(student);
		userRepository.save(user);
	}

	@Transactional
	public void deleteTeacherAndRoleWithUser(UUID teacherId) {
		Teacher teacher = teacherRepository.findById(teacherId)
				.orElseThrow(() -> new RuntimeException("Такого id нет. " + teacherId));
		User user = teacher.getUser();
		user.getRoles().removeIf(role -> RoleElement.ROLE_TEACHER.name().equals(role.getName()));
		helpsMethod.set_ROLE_QUEST_IfRoleNull(user);
		teacherRepository.delete(teacher);
		userRepository.save(user);
	}

	@Transactional
	public UserReadDto updateRoles(UUID userId, List<UUID> roleIds) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Такого id нет. " + userId));
		List<Role> roles = roleRepository.findAllById(roleIds);
		String newRole = roles.get(0).getName();
		String oldRole = helpMapsService.findRoleFromObjectByUser(userId)
				.orElseThrow(() -> new RuntimeException("Такого id нет. " + userId)).name();
		if (roles.size() == 1 && !newRole.equals(oldRole)) {
			helpMapsService.deleteObjectFrom(user);
			user.setRoles(roles);
			if (!helpMapsService.roleWithoutObject(roles.get(0))) {
				HasUser hasUser = helpMapsService.createObject(roles.get(0), user);
				helpMapsService.saveObject(roles.get(0), hasUser);
			}
		} else {
			user.setRoles(roles);
		}
		return userReadMapper.toDto(userRepository.saveAndFlush(user));
	}

	@Transactional
	public void makeUserStudent(UUID studentId, UUID groupId) {
		Student student = studentRepository.findById(studentId)
				.orElseThrow(() -> new RuntimeException("Такого id нет. " + studentId));
		groupRepository.findById(groupId).ifPresent(student::setGroup);
		studentRepository.saveAndFlush(student);
	}

	@Transactional(readOnly = true)
	public boolean checkChooseRole(UserReadDto user, RoleElement roleElement) {
		return user.getRoles().stream().anyMatch(r -> r.getName().equals(roleElement.name()));
	}

	@Transactional
	public void deleteRoleByUserId(UUID userId) {
		User user = userRepository.findById(userId).get();
		user.getRoles().removeIf(role -> role.getName().equals(RoleElement.ROLE_ADMIN.name())
				|| role.getName().equals(RoleElement.ROLE_METHODIST.name()));
		helpsMethod.set_ROLE_QUEST_IfRoleNull(user);
		userRepository.save(user);
	}

	@Transactional
	public void addRoleAdminUser(UUID userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Такого id нет. " + userId));
		user.getRoles().clear();
		user.getRoles().add(roleRepository.findByName(RoleElement.ROLE_ADMIN.name())
				.orElseThrow(() -> new RuntimeException("Такой роли нет.")));
		userRepository.save(user);
	}

	@Transactional
	public void addRoleMethodistUser(UUID userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Такого id нет. " + userId));
		user.getRoles().clear();
		user.getRoles().add(roleRepository.findByName(RoleElement.ROLE_METHODIST.name())
				.orElseThrow(() -> new RuntimeException("Такой роли нет.")));
		userRepository.save(user);
	}

	@Transactional
	public void removeGroup(UUID courseId, String groupName) {
		Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Курс не найден."));
		Group group = groupRepository.findByName(groupName)
				.orElseThrow(() -> new RuntimeException("Группа не найдена."));
		course.getGroups().remove(group);
		group.getCourses().remove(course);
		courseRepository.save(course);
	}

}

package spring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import spring.auxiliaryObjects.CreateObjects;
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

@SpringBootTest
class AdminServiceTest {

	@Autowired
	private AdminService adminService;

	@Autowired
	private UserReadMapper userReadMapper;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private TeacherRepository teacherRepository;

	@MockBean
	private StudentRepository studentRepository;

	@MockBean
	private RoleRepository roleRepository;

	@MockBean
	private GroupRepository groupRepository;

	@MockBean
	private CourseRepository courseRepository;

	@MockBean
	private HelpMapsService helpMapsService;

	@MockBean
	private HelpsMethod helpsMethod;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;

	private CreateObjects createObjects = new CreateObjects();

	@Test
	void makeStudentWithUsertest() {
		User userQuest = createObjects.createUser();
		Role questRole = userQuest.getRoles().get(0);
		Role studentRole = createObjects.createRole();
		studentRole.setName(RoleElement.ROLE_STUDENT.name());

		Student student = createObjects.createStudent();
		student.getUser().getRoles().set(0, studentRole);

		when(userRepository.findById(userQuest.getId())).thenReturn(Optional.of(userQuest));
		when(roleRepository.findByName(RoleElement.ROLE_QUEST.name())).thenReturn(Optional.of(questRole));
		when(roleRepository.findByName(RoleElement.ROLE_STUDENT.name())).thenReturn(Optional.of(studentRole));
		when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

		adminService.makeStudentWithUser(userQuest.getId());

		verify(userRepository).findById(userQuest.getId());
		verify(roleRepository).findByName(RoleElement.ROLE_QUEST.name());
		verify(roleRepository).findByName(RoleElement.ROLE_STUDENT.name());
		verify(studentRepository).save(any(Student.class));

		assertFalse(userQuest.getRoles().contains(questRole));
		assertTrue(userQuest.getRoles().contains(studentRole));
	}

	@Test
	void makeTeacherWithUsertest() {
		User userQuest = createObjects.createUser();
		Role questRole = userQuest.getRoles().get(0);
		Role teacherRole = createObjects.createRole();
		teacherRole.setName(RoleElement.ROLE_TEACHER.name());

		Student student = createObjects.createStudent();
		student.getUser().getRoles().set(0, teacherRole);

		when(userRepository.findById(userQuest.getId())).thenReturn(Optional.of(userQuest));
		when(roleRepository.findByName(RoleElement.ROLE_QUEST.name())).thenReturn(Optional.of(questRole));
		when(roleRepository.findByName(RoleElement.ROLE_TEACHER.name())).thenReturn(Optional.of(teacherRole));
		when(teacherRepository.save(any(Teacher.class))).thenAnswer(inv -> inv.getArgument(0));

		adminService.makeTeacherWithUser(userQuest.getId());

		verify(userRepository).findById(userQuest.getId());
		verify(roleRepository).findByName(RoleElement.ROLE_QUEST.name());
		verify(roleRepository).findByName(RoleElement.ROLE_TEACHER.name());
		verify(teacherRepository).save(any(Teacher.class));

		assertFalse(userQuest.getRoles().contains(questRole));
		assertTrue(userQuest.getRoles().contains(teacherRole));
	}

	@Test
	void deleteStudentAndRoleWithUserTest() {
		Student student = createObjects.createStudent();
		student.getUser().getRoles().get(0).setName(RoleElement.ROLE_STUDENT.name());

		when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
		when(userRepository.save(student.getUser())).thenReturn(student.getUser());

		adminService.deleteStudentAndRoleWithUser(student.getId());

		verify(userRepository).save(student.getUser());
		verify(studentRepository).delete(student);
		verify(helpsMethod).set_ROLE_QUEST_IfRoleNull(student.getUser());

		assertTrue(student.getUser().getRoles().stream()
				.noneMatch(role -> role.getName().equals(RoleElement.ROLE_STUDENT.name())));

	}

	@Test
	void deleteTeacherAndRoleWithUserTest() {
		Teacher teacher = createObjects.createTeacher();
		teacher.getUser().getRoles().get(0).setName(RoleElement.ROLE_TEACHER.name());

		when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
		when(userRepository.save(teacher.getUser())).thenReturn(teacher.getUser());

		adminService.deleteTeacherAndRoleWithUser(teacher.getId());

		verify(userRepository).save(teacher.getUser());
		verify(teacherRepository).delete(teacher);
		verify(helpsMethod).set_ROLE_QUEST_IfRoleNull(teacher.getUser());

		assertTrue(teacher.getUser().getRoles().stream()
				.noneMatch(role -> role.getName().equals(RoleElement.ROLE_TEACHER.name())));
	}

	@Test
	void updateRoleTest() {
		Student student = createObjects.createStudent();
		HasUser teacher = createObjects.createTeacher();

		Role newRole = createObjects.createRole();
		newRole.setName(RoleElement.ROLE_STUDENT.name());

		List<UUID> roleIds = new ArrayList<>();
		roleIds.add(newRole.getId());
		List<Role> newRoles = new ArrayList<>();
		newRoles.add(newRole);

		Role oldRole = createObjects.createRole();
		oldRole.setName(RoleElement.ROLE_TEACHER.name());

		when(userRepository.findById(student.getUser().getId())).thenReturn(Optional.of(student.getUser()));
		when(roleRepository.findAllById(roleIds)).thenReturn(newRoles);
		when(helpMapsService.findRoleFromObjectByUser(student.getUser().getId()))
				.thenReturn(Optional.of(RoleElement.ROLE_TEACHER));
		when(helpMapsService.deleteObjectFrom(student.getUser())).thenReturn(true);
		when(helpMapsService.roleWithoutObject(newRole)).thenReturn(false);
		when(helpMapsService.createObject(newRole, student.getUser())).thenReturn(teacher);
		when(userRepository.saveAndFlush(student.getUser())).thenReturn(student.getUser());

		UserReadDto userReadDto = adminService.updateRoles(student.getUser().getId(), roleIds);

		verify(userRepository).findById(student.getUser().getId());
		verify(roleRepository).findAllById(roleIds);
		verify(helpMapsService).findRoleFromObjectByUser(student.getUser().getId());
		verify(helpMapsService).deleteObjectFrom(student.getUser());
		verify(helpMapsService).roleWithoutObject(newRole);
		verify(helpMapsService).createObject(newRole, student.getUser());
		verify(helpMapsService).saveObject(newRole, teacher);
		verify(userRepository).saveAndFlush(student.getUser());

		assertTrue(RoleElement.ROLE_STUDENT.name().equals(newRole.getName()));
		assertEquals(student.getUser().getId(), userReadDto.getId());
		assertTrue(RoleElement.ROLE_STUDENT.name().equals(userReadDto.getRoles().get(0).getName()));
	}

	@Test
	void makeUserStudentTest() {
		Student student = createObjects.createStudent();
		Group group = createObjects.createGroup();

		when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
		when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
		when(studentRepository.saveAndFlush(any(Student.class))).thenReturn(student);

		adminService.makeUserStudent(student.getId(), group.getId());

		verify(studentRepository).findById(student.getId());
		verify(groupRepository).findById(group.getId());
		verify(studentRepository).saveAndFlush(any(Student.class));

		ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
		verify(studentRepository).saveAndFlush(captor.capture());
		assertEquals(group, captor.getValue().getGroup());
	}

	@Test
	void checkChooseRoleTest() {
		UserReadDto userReadDto = createObjects.createUserDto(createObjects.createRoleDto(RoleElement.ROLE_STUDENT));
		assertTrue(adminService.checkChooseRole(userReadDto, RoleElement.ROLE_STUDENT));
	}

	@Test
	void deleteRoleByUserIdTest() {
		User user = createObjects.createUser();
		user.getRoles().get(0).setName(RoleElement.ROLE_ADMIN.name());

		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(userRepository.save(any(User.class))).thenReturn(user);
		doAnswer(invocation -> {
			User u = invocation.getArgument(0);
			u.getRoles().clear();
			u.getRoles().add(createObjects.createRole());
			return null;
		}).when(helpsMethod).set_ROLE_QUEST_IfRoleNull(any(User.class));

		adminService.deleteRoleByUserId(user.getId());

		verify(userRepository).findById(user.getId());
		verify(helpsMethod).set_ROLE_QUEST_IfRoleNull(user);
		verify(userRepository).save(any(User.class));

		assertFalse(user.getRoles().get(0).getName().equals(RoleElement.ROLE_ADMIN.name()));
		assertTrue(user.getRoles().get(0).getName().equals(RoleElement.ROLE_QUEST.name()));
	}

	@Test
	void addRoleAdminUserTest() {
		User user = createObjects.createUser();
		Role newRole = createObjects.createRole();
		newRole.setName(RoleElement.ROLE_ADMIN.name());

		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(roleRepository.findByName(newRole.getName())).thenReturn(Optional.of(newRole));
		doAnswer(invocation -> {
			User u = invocation.getArgument(0);
			u.getRoles().clear();
			u.getRoles().add(newRole);
			return null;
		}).when(userRepository).save(any(User.class));

		adminService.addRoleAdminUser(user.getId());

		verify(userRepository).findById(user.getId());
		verify(roleRepository).findByName(newRole.getName());
		verify(userRepository).save(any(User.class));

		assertFalse(user.getRoles().get(0).getName().equals(RoleElement.ROLE_QUEST.name()));
		assertTrue(user.getRoles().get(0).getName().equals(newRole.getName()));
	}

	@Test
	void addRoleMethodistUserTest() {
		User user = createObjects.createUser();
		Role newRole = createObjects.createRole();
		newRole.setName(RoleElement.ROLE_METHODIST.name());

		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(roleRepository.findByName(newRole.getName())).thenReturn(Optional.of(newRole));
		doAnswer(invocation -> {
			User u = invocation.getArgument(0);
			u.getRoles().clear();
			u.getRoles().add(newRole);
			return null;
		}).when(userRepository).save(any(User.class));

		adminService.addRoleMethodistUser(user.getId());

		verify(userRepository).findById(user.getId());
		verify(roleRepository).findByName(newRole.getName());
		verify(userRepository).save(any(User.class));

		assertFalse(user.getRoles().get(0).getName().equals(RoleElement.ROLE_QUEST.name()));
		assertTrue(user.getRoles().get(0).getName().equals(newRole.getName()));
	}

	@Test
	void removeGroupTest() {
		UUID courseId = UUID.randomUUID();
		String groupName = "name";

		Course course = new Course();
		Group group = new Group();

		course.getGroups().add(group);
		group.getCourses().add(course);

		when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
		when(groupRepository.findByName(groupName)).thenReturn(Optional.of(group));
		when(courseRepository.save(course)).thenReturn(course);

		adminService.removeGroup(courseId, groupName);

		assertFalse(course.getGroups().contains(group));
		assertFalse(group.getCourses().contains(course));

		verify(courseRepository).findById(courseId);
		verify(groupRepository).findByName(groupName);
		verify(courseRepository).save(course);

	}

}

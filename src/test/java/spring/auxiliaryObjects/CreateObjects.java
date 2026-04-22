package spring.auxiliaryObjects;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import spring.dto.CourseAddEditDto;
import spring.dto.CourseReadDto;
import spring.dto.DepartmentAddEditDto;
import spring.dto.DepartmentReadDto;
import spring.dto.EnrollmentAddEditDto;
import spring.dto.EnrollmentReadDto;
import spring.dto.FacultyAddEditDto;
import spring.dto.FacultyReadDto;
import spring.dto.GroupAddEditDto;
import spring.dto.GroupReadDto;
import spring.dto.LessonAddEditDto;
import spring.dto.LessonReadDto;
import spring.dto.RoleAddEditDto;
import spring.dto.RoleReadDto;
import spring.dto.RoomAddEditDto;
import spring.dto.RoomReadDto;
import spring.dto.StudentAddEditDto;
import spring.dto.StudentReadDto;
import spring.dto.TeacherAddEditDto;
import spring.dto.TeacherReadDto;
import spring.dto.UniversityAddEditDto;
import spring.dto.UniversityReadDto;
import spring.dto.UserAddEditDto;
import spring.dto.UserReadDto;
import spring.model.Course;
import spring.model.DateFilter;
import spring.model.Department;
import spring.model.Enrollment;
import spring.model.EnrollmentStatus;
import spring.model.Faculty;
import spring.model.Group;
import spring.model.Lesson;
import spring.model.LessonStatus;
import spring.model.Role;
import spring.model.RoleElement;
import spring.model.Room;
import spring.model.Student;
import spring.model.Teacher;
import spring.model.University;
import spring.model.User;

public class CreateObjects {

	private static final String NAME = "namenamename";
	private static final UUID ID = UUID.randomUUID();

	public University createUniversity() {
		University university = new University();
		university.setId(ID);
		university.setName(NAME);
		return university;
	}

	public UniversityReadDto createUniversityDto() {
		return new UniversityReadDto(ID, NAME);
	}

	public UniversityAddEditDto createUniversityEditDto() {
		return new UniversityAddEditDto(NAME);
	}

	public Faculty createFaculty() {
		Faculty faculty = new Faculty();
		faculty.setId(ID);
		faculty.setName(NAME);
		faculty.setUniversity(createUniversity());
		return faculty;
	}

	public FacultyReadDto createFacultyDto() {
		return new FacultyReadDto(ID, NAME, createUniversityDto());
	}

	public FacultyAddEditDto createFacultyEditDto() {
		return new FacultyAddEditDto(NAME, createUniversityDto().getId());
	}

	public Department createDepartment() {
		Department department = new Department();
		department.setId(ID);
		department.setName(NAME);
		department.setFaculty(createFaculty());
		return department;
	}

	public DepartmentReadDto createDepartmentDto() {
		return new DepartmentReadDto(ID, NAME, createFacultyDto());
	}

	public DepartmentAddEditDto createDepartmentEditDto() {
		return new DepartmentAddEditDto(NAME, createFacultyDto().getId());
	}

	public Group createGroup() {
		Group group = new Group();
		group.setId(ID);
		group.setName(NAME);
		group.setDepartment(createDepartment());
		return group;
	}

	public GroupReadDto createGroupDto() {
		return new GroupReadDto(ID, NAME, createDepartmentDto());
	}

	public GroupAddEditDto createGroupEditDto() {
		return new GroupAddEditDto(NAME, createDepartmentDto().getId());
	}

	public Course createCourse() {
		Course course = new Course();
		course.setId(ID);
		course.setCourseName(NAME);
		course.setDescription(NAME);
		course.setDepartment(createDepartment());
		List<Group> groups = new ArrayList<>();
		groups.add(createGroup());
		course.setGroups(groups);
		return course;
	}

	public CourseReadDto createCourseDto() {
		List<GroupReadDto> groups = new ArrayList<>();
		groups.add(createGroupDto());
		return new CourseReadDto(ID, NAME, NAME, createDepartmentDto(), groups);
	}

	public CourseAddEditDto createCourseEditDto() {
		return new CourseAddEditDto(NAME, NAME, createDepartmentDto().getId(), createGroupDto().getId());
	}

	public Room createRoom() {
		Room room = new Room();
		room.setId(ID);
		room.setNumber(NAME);
		return room;
	}

	public RoomReadDto createRoomDto() {
		return new RoomReadDto(ID, NAME);
	}

	public RoomAddEditDto createRoomEditDto() {
		return new RoomAddEditDto(NAME);
	}

	public Role createRole() {
		Role role = new Role();
		role.setId(ID);
		role.setName(RoleElement.ROLE_QUEST.name());
		return role;
	}

	public RoleReadDto createRoleDto(RoleElement roleElement) {
		return new RoleReadDto(ID, roleElement.name());
	}

	public RoleAddEditDto createRoleEditDto() {
		return new RoleAddEditDto(RoleElement.ROLE_QUEST.name());
	}

	public User createUser() {
		User user = new User();
		user.setId(ID);
		user.setUserName(NAME);
		user.setFirstName(NAME);
		user.setLastName(NAME);
		user.setEmail(NAME);
		user.setEnabled(true);
		user.setPasswordHash(NAME);
		List<Role> roles = new ArrayList<>();
		roles.add(createRole());
		user.setRoles(roles);
		return user;
	}

	public UserReadDto createUserDto(RoleReadDto roleReadDto) {
		List<RoleReadDto> roles = new ArrayList<>();
		roles.add(roleReadDto);
		return new UserReadDto(ID, NAME, NAME, true, NAME, NAME, NAME, roles);
	}

	public UserAddEditDto createUserEditDto() {
		List<UUID> roleIds = new ArrayList<>();
		roleIds.add(createRoleDto(RoleElement.ROLE_QUEST).getId());
		List<RoleAddEditDto> roles = new ArrayList<>();
		roles.add(createRoleEditDto());
		return new UserAddEditDto(NAME, NAME, true, NAME, NAME, NAME, roles, roleIds);
	}

	public Student createStudent() {
		Student student = new Student();
		student.setId(ID);
		student.setUser(createUser());
		student.setGroup(createGroup());
		return student;
	}

	public StudentReadDto createStudentDto(UserReadDto userReadDto) {
		return new StudentReadDto(ID, userReadDto, createGroupDto());
	}

	public StudentAddEditDto createStudentEditDto() {
		return new StudentAddEditDto(createUserEditDto(), createGroupDto().getId());
	}

	public Lesson createLesson() {
		List<Group> groups = new ArrayList<>();
		groups.add(createGroup());
		List<Student> students = new ArrayList<>();
		students.add(createStudent());
		Lesson lesson = new Lesson();
		lesson.setId(ID);
		lesson.setCourse(createCourse());
		lesson.setDate(LocalDate.now());
		lesson.setEndTime(LocalTime.now());
		lesson.setStartTime(LocalTime.now());
		lesson.setStatus(LessonStatus.MOVED);
		lesson.setRoom(createRoom());
		lesson.setGroups(groups);
		lesson.setStudents(students);
		return lesson;
	}

	public LessonReadDto createLessonDto() {
		List<GroupReadDto> groups = new ArrayList<>();
		groups.add(createGroupDto());
		List<StudentReadDto> students = new ArrayList<>();
		students.add(createStudentDto(createUserDto(createRoleDto(RoleElement.ROLE_STUDENT))));
		return new LessonReadDto(ID, LocalDate.now(), LocalTime.now(), LocalTime.now(), LessonStatus.PLANNED,
				createCourseDto(), createRoomDto(), groups, students);
	}

	public LessonAddEditDto createLessonEditDto() {
		GroupReadDto group = createGroupDto();
		List<UUID> groupIds = new ArrayList<>();
		groupIds.add(group.getId());
		StudentReadDto student = createStudentDto(createUserDto(createRoleDto(RoleElement.ROLE_STUDENT)));
		List<UUID> studentIds = new ArrayList<>();
		studentIds.add(student.getId());
		return new LessonAddEditDto(LocalDate.now(), LocalDate.now(), LocalTime.now(), LocalTime.now(), studentIds,
				groupIds, DateFilter.DAY, LessonStatus.MOVED, createDepartment().getId(), createCourseDto().getId(),
				createRoomDto().getId(),
				createTeacherDto(createUserDto(createRoleDto(RoleElement.ROLE_TEACHER))).getId(), group.getId(),
				createLessonDto().getId());
	}

	public Teacher createTeacher() {
		List<Course> courses = new ArrayList<>();
		courses.add(createCourse());
		List<Lesson> lessons = new ArrayList<>();
		lessons.add(createLesson());
		Teacher teacher = new Teacher();
		teacher.setId(ID);
		teacher.setUser(createUser());
		teacher.setCourses(courses);
		teacher.setLessons(lessons);
		teacher.setDepartment(createDepartment());
		return teacher;
	}

	public TeacherReadDto createTeacherDto(UserReadDto userReadDto) {
		List<CourseReadDto> courses = new ArrayList<>();
		courses.add(createCourseDto());
		List<LessonReadDto> lessons = new ArrayList<>();
		lessons.add(createLessonDto());
		return new TeacherReadDto(ID, userReadDto, lessons, courses, createDepartmentDto());
	}

	public TeacherAddEditDto createTeacherEditDto() {
		List<UUID> ids = new ArrayList<>();
		ids.add(ID);
		return new TeacherAddEditDto(createUserEditDto(), ids, ids, createDepartmentDto().getId());
	}

	public Enrollment createEnrollment() {
		Enrollment enrollment = new Enrollment();
		enrollment.setId(ID);
		enrollment.setCourse(createCourse());
		enrollment.setStudent(createStudent());
		enrollment.setGrade(NAME);
		enrollment.setStatus(EnrollmentStatus.ACTIVE);
		return enrollment;
	}

	public EnrollmentReadDto craeteEnrollmentDto() {
		return new EnrollmentReadDto(ID, NAME, EnrollmentStatus.ACTIVE,
				createStudentDto(createUserDto(createRoleDto(RoleElement.ROLE_STUDENT))), createCourseDto());
	}

	public EnrollmentAddEditDto createEnrollmentEditDto() {
		return new EnrollmentAddEditDto(NAME, EnrollmentStatus.ACTIVE, createStudentEditDto(), createCourseEditDto());
	}

}

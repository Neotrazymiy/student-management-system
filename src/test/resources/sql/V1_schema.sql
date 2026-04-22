CREATE TABLE IF NOT EXISTS university (
	id UUID PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	code VARCHAR(250) UNIQUE
);

CREATE TABLE IF NOT EXISTS faculty (
	id UUID PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	university_id UUID NOT NULL,
	CONSTRAINT fk_facity_university FOREIGN KEY (university_id) REFERENCES university (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS department (
	id UUID PRIMARY KEY,
	name VARCHAR(50) UNIQUE NOT NULL,
	faculty_id UUID NOT NULL,
	CONSTRAINT fk_department_faculty FOREIGN KEY (faculty_id) REFERENCES faculty (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS groupe (
	id UUID PRIMARY KEY,
	name VARCHAR(50) UNIQUE NOT NULL,
	department_id UUID,
	CONSTRAINT fk_group_department FOREIGN KEY (department_id) REFERENCES department (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS userr (
	id UUID PRIMARY KEY,
	user_name VARCHAR(50) UNIQUE NOT NULL,
	email VARCHAR(50) NOT NULL,
	password_hash VARCHAR(100) UNIQUE NOT NULL,
	enabled BOOLEAN NOT NULL DEFAULT FALSE,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS course (
	id UUID PRIMARY KEY,
	course_name VARCHAR(50) UNIQUE NOT NULL,
	course_description VARCHAR(50),
	department_id UUID,
	CONSTRAINT fk_course_department FOREIGN KEY (department_id) REFERENCES department (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS room (
	id UUID PRIMARY KEY,
	number VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS role (
	id UUID PRIMARY KEY,
	name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS lesson (
	id UUID PRIMARY KEY,
	date DATE NOT NULL,
	start_time TIME NOT NULL,
	end_time TIME NOT NULL,
	status VARCHAR(50) NOT NULL,
	course_id UUID NOT NULL,
	room_id UUID NOT NULL,
	CONSTRAINT uq_lesson_time_room UNIQUE (date, start_time, end_time, room_id),
	CONSTRAINT uq_lesson_course_room UNIQUE (course_id, room_id, start_time, date),
	CONSTRAINT fk_lesson_course FOREIGN KEY (course_id) REFERENCES course (id) ON DELETE CASCADE,
	CONSTRAINT fk_lesson_room FOREIGN KEY (room_id) REFERENCES room (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS student (
	id UUID PRIMARY KEY,
	user_id UUID UNIQUE NOT NULL,
	groupe_id UUID,
	CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES userr (id) ON DELETE CASCADE,
	CONSTRAINT fk_student_groupe FOREIGN KEY (groupe_id) REFERENCES groupe (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS teacher (
	id UUID PRIMARY KEY,
	user_id UUID UNIQUE NOT NULL,
	department_id UUID,
	CONSTRAINT fk_teacher_user FOREIGN KEY (user_id) REFERENCES userr (id) ON DELETE CASCADE,
	CONSTRAINT fk_teacher_department FOREIGN KEY (department_id) REFERENCES department (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS enrollment (
	id UUID PRIMARY KEY,
	grade VARCHAR(50),
	status VARCHAR(50) NOT NULL,
	student_id UUID NOT NULL,
	course_id UUID NOT NULL,
	CONSTRAINT fk_enrollment_student FOREIGN KEY (student_id) REFERENCES student (id) ON DELETE CASCADE,
	CONSTRAINT fk_enrollment_course FOREIGN KEY (course_id) REFERENCES course (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS teacher_course (
	course_id UUID NOT NULL,
	teacher_id UUID NOT NULL,
	PRIMARY KEY (course_id, teacher_id),
	CONSTRAINT fk_ct_course FOREIGN KEY (course_id) REFERENCES course (id) ON DELETE CASCADE,
	CONSTRAINT fk_ct_teacher FOREIGN KEY (teacher_id) REFERENCES teacher (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS course_groupe (
	course_id UUID NOT NULL,
	groupe_id UUID NOT NULL,
	PRIMARY KEY (course_id, groupe_id),
	CONSTRAINT fk_cg_course FOREIGN KEY (course_id) REFERENCES course (id) ON DELETE CASCADE,
	CONSTRAINT fk_cg_groupe FOREIGN KEY (groupe_id) REFERENCES groupe(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS lesson_groupe (
	lesson_id UUID NOT NULL,
	groupe_id UUID NOT NULL,
	PRIMARY KEY (lesson_id, groupe_id),
	CONSTRAINT fk_lg_lesson FOREIGN KEY (lesson_id) REFERENCES lesson (id) ON DELETE CASCADE,
	CONSTRAINT fk_lg_groupe FOREIGN KEY (groupe_id) REFERENCES groupe (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS teacher_lesson (
	lesson_id UUID NOT NULL,
	teacher_id UUID NOT NULL,
	PRIMARY KEY (lesson_id, teacher_id),
	CONSTRAINT fk_lt_lesson FOREIGN KEY (lesson_id) REFERENCES lesson (id) ON DELETE CASCADE,
	CONSTRAINT fk_lt_teacher FOREIGN KEY (teacher_id) REFERENCES teacher (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_role (
	user_id UUID NOT NULL,
	role_id UUID NOT NULL,
	PRIMARY KEY (user_id, role_id),
	CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES userr (id) ON DELETE CASCADE,
	CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS lesson_student_mapping (
    lesson_id UUID NOT NULL,
    student_id UUID NOT NULL,
    PRIMARY KEY (lesson_id, student_id),
    CONSTRAINT fk_lesson FOREIGN KEY (lesson_id) REFERENCES lesson(id) ON DELETE CASCADE,
    CONSTRAINT fk_student FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE
);
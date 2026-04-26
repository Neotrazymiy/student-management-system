CREATE TABLE IF NOT EXISTS s.university (
	id UUID PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	code VARCHAR(250) UNIQUE
);

CREATE TABLE IF NOT EXISTS s.faculty (
	id UUID PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	university_id UUID NOT NULL,
	CONSTRAINT fk_facity_university FOREIGN KEY (university_id) REFERENCES s.university (id)
);

CREATE TABLE IF NOT EXISTS s.department (
	id UUID PRIMARY KEY,
	name VARCHAR(50) UNIQUE NOT NULL,
	faculty_id UUID NOT NULL,
	CONSTRAINT fk_department_faculty FOREIGN KEY (faculty_id) REFERENCES s.faculty (id)
);

CREATE TABLE IF NOT EXISTS s.groupe (
	id UUID PRIMARY KEY,
	name VARCHAR(50) UNIQUE NOT NULL,
	department_id UUID,
	CONSTRAINT fk_group_department FOREIGN KEY (department_id) REFERENCES s.department (id)
);

CREATE TABLE IF NOT EXISTS s.userr (
	id UUID PRIMARY KEY,
	user_name VARCHAR(50) UNIQUE NOT NULL,
	email VARCHAR(50) NOT NULL,
	password_hash VARCHAR(100) UNIQUE NOT NULL,
	enabled BOOLEAN NOT NULL DEFAULT FALSE,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	failed_attempts INTEGER NOT NULL DEFAULT 0,
	blocking_time TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS s.course (
	id UUID PRIMARY KEY,
	course_name VARCHAR(50) UNIQUE NOT NULL,
	course_description VARCHAR(50),
	department_id UUID,
	CONSTRAINT fk_course_department FOREIGN KEY (department_id) REFERENCES s.department (id)
);

CREATE TABLE IF NOT EXISTS s.room (
	id UUID PRIMARY KEY,
	number VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS s.role (
	id UUID PRIMARY KEY,
	name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS s.lesson (
	id UUID PRIMARY KEY,
	date DATE NOT NULL,
	start_time TIME NOT NULL,
	end_time TIME NOT NULL,
	status VARCHAR(50) NOT NULL,
	course_id UUID NOT NULL,
	room_id UUID NOT NULL,
	CONSTRAINT uq_lesson_time_room UNIQUE (date, start_time, end_time, room_id),
	CONSTRAINT fk_lesson_course FOREIGN KEY (course_id) REFERENCES s.course (id) ON DELETE CASCADE,
	CONSTRAINT fk_lesson_room FOREIGN KEY (room_id) REFERENCES s.room (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS s.student (
	id UUID PRIMARY KEY,
	user_id UUID UNIQUE NOT NULL,
	groupe_id UUID,
	CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES s.userr (id),
	CONSTRAINT fk_student_groupe FOREIGN KEY (groupe_id) REFERENCES s.groupe (id)
);

CREATE TABLE IF NOT EXISTS s.teacher (
	id UUID PRIMARY KEY,
	user_id UUID UNIQUE NOT NULL,
	department_id UUID,
	CONSTRAINT fk_teacher_user FOREIGN KEY (user_id) REFERENCES s.userr (id),
	CONSTRAINT fk_teacher_department FOREIGN KEY (department_id) REFERENCES s.department (id)
);

CREATE TABLE IF NOT EXISTS s.enrollment (
	id UUID PRIMARY KEY,
	grade VARCHAR(50),
	status VARCHAR(50) NOT NULL,
	student_id UUID NOT NULL,
	course_id UUID NOT NULL,
	CONSTRAINT fk_enrollment_student FOREIGN KEY (student_id) REFERENCES s.student (id) ON DELETE CASCADE,
	CONSTRAINT fk_enrollment_course FOREIGN KEY (course_id) REFERENCES s.course (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS s.teacher_course (
	course_id UUID NOT NULL,
	teacher_id UUID NOT NULL,
	PRIMARY KEY (course_id, teacher_id),
	CONSTRAINT fk_ct_course FOREIGN KEY (course_id) REFERENCES s.course (id) ON DELETE CASCADE,
	CONSTRAINT fk_ct_teacher FOREIGN KEY (teacher_id) REFERENCES s.teacher (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS s.course_groupe (
	course_id UUID NOT NULL,
	groupe_id UUID NOT NULL,
	PRIMARY KEY (course_id, groupe_id),
	CONSTRAINT fk_cg_course FOREIGN KEY (course_id) REFERENCES s.course (id) ON DELETE CASCADE,
	CONSTRAINT fk_cg_groupe FOREIGN KEY (groupe_id) REFERENCES s.groupe(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS s.lesson_groupe (
	lesson_id UUID NOT NULL,
	groupe_id UUID NOT NULL,
	PRIMARY KEY (lesson_id, groupe_id),
	CONSTRAINT fk_lg_lesson FOREIGN KEY (lesson_id) REFERENCES s.lesson (id) ON DELETE CASCADE,
	CONSTRAINT fk_lg_groupe FOREIGN KEY (groupe_id) REFERENCES s.groupe (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS s.teacher_lesson (
	lesson_id UUID NOT NULL,
	teacher_id UUID NOT NULL,
	PRIMARY KEY (lesson_id, teacher_id),
	CONSTRAINT fk_lt_lesson FOREIGN KEY (lesson_id) REFERENCES s.lesson (id) ON DELETE CASCADE,
	CONSTRAINT fk_lt_teacher FOREIGN KEY (teacher_id) REFERENCES s.teacher (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS s.user_role (
	user_id UUID NOT NULL,
	role_id UUID NOT NULL,
	PRIMARY KEY (user_id, role_id),
	CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES s.userr (id) ON DELETE CASCADE,
	CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES s.role (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS s.lesson_student_mapping (
    lesson_id UUID NOT NULL,
    student_id UUID NOT NULL,
    PRIMARY KEY (lesson_id, student_id),
    CONSTRAINT fk_lesson FOREIGN KEY (lesson_id) REFERENCES s.lesson(id) ON DELETE CASCADE,
    CONSTRAINT fk_student FOREIGN KEY (student_id) REFERENCES s.student(id) ON DELETE CASCADE
);
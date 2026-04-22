package spring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import spring.auxiliaryObjects.CreateObjects;
import spring.model.Lesson;
import spring.model.Student;
import spring.model.Teacher;

@SpringBootTest
class EmailServiceTest {

	@Autowired
	private EmailService emailService;

	@MockBean
	private JavaMailSender javaMailSender;

	private final CreateObjects createObjects = new CreateObjects();
	private static final String NAME = "namenamename";

	@Test
	void sendMailChangeLessonStudent() {
		Lesson lesson = createObjects.createLesson();
		Student student = createObjects.createStudent();

		emailService.sendLessonChangeEmail(student, lesson);

		ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(javaMailSender).send(captor.capture());

		SimpleMailMessage message = captor.getValue();
		assertTrue(message.getTo()[0].equals(NAME));
		assertTrue(message.getSubject().equals("Изменения в уроке"));
		assertEquals(message.getText(), "Занятие было изменено \n\n Дата: " + lesson.getDate() + "\nВремя: "
				+ lesson.getStartTime() + " - " + lesson.getEndTime() + "\nКурс: " + NAME);
	}

	@Test
	void sendMailChangeLessonTeacher() {
		Lesson lesson = createObjects.createLesson();
		Teacher teacher = createObjects.createTeacher();

		emailService.sendLessonChangeEmail(teacher, lesson);

		ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(javaMailSender).send(captor.capture());

		SimpleMailMessage message = captor.getValue();
		assertTrue(message.getTo()[0].equals(NAME));
		assertTrue(message.getSubject().equals("Изменения в уроке"));
		assertEquals(message.getText(), "Занятие было изменено \n\n Дата: " + lesson.getDate() + "\nВремя: "
				+ lesson.getStartTime() + " - " + lesson.getEndTime() + "\nКурс: " + NAME);
	}

}

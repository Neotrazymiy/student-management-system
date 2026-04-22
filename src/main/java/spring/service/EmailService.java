package spring.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import spring.model.Lesson;
import spring.model.Student;
import spring.model.Teacher;

@Service
@AllArgsConstructor
public class EmailService {

	private final JavaMailSender javaMailSender;

	public void sendLessonChangeEmail(Student student, Lesson lesson) {
		String title = "Изменения в уроке";

		String body = "Занятие было изменено \n\n Дата: " + lesson.getDate() + "\nВремя: " + lesson.getStartTime()
				+ " - " + lesson.getEndTime() + "\nКурс: " + lesson.getCourse().getCourseName();
		sendSimpleEmail(student.getUser().getEmail(), title, body);
	}

	public void sendLessonChangeEmail(Teacher teacher, Lesson lesson) {
		String title = "Изменения в уроке";

		String body = "Занятие было изменено \n\n Дата: " + lesson.getDate() + "\nВремя: " + lesson.getStartTime()
				+ " - " + lesson.getEndTime() + "\nКурс: " + lesson.getCourse().getCourseName();
		sendSimpleEmail(teacher.getUser().getEmail(), title, body);
	}

	@Async
	public void sendMailChangeLessonAsync(Student student, Lesson lesson) {
		sendLessonChangeEmail(student, lesson);
	}

	@Async
	public void sendMailChangeLessonAsync(Teacher teacher, Lesson lesson) {
		sendLessonChangeEmail(teacher, lesson);
	}

	private void sendSimpleEmail(String to, String subject, String text) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			javaMailSender.send(message);
			System.out.println("Email sent to: " + to);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

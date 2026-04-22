package spring.event;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.AllArgsConstructor;
import spring.model.Lesson;
import spring.service.EmailService;

@Component
@AllArgsConstructor
public class ChangeLessonListener {

	private final EmailService emailService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(ChangeLessonEvent event) {
		Lesson lesson = event.getLesson();

		lesson.getStudents().stream().forEach(student -> emailService.sendMailChangeLessonAsync(student, lesson));
		lesson.getGroups().stream().flatMap(group -> group.getStudents().stream()).collect(Collectors.toSet())
				.forEach(student -> emailService.sendMailChangeLessonAsync(student, lesson));
		if (lesson.getTeachers() != null) {
			lesson.getTeachers().stream().forEach(teacher -> emailService.sendMailChangeLessonAsync(teacher, lesson));
		}
	}

}

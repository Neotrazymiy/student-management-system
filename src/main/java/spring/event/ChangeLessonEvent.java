package spring.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import spring.model.Lesson;

@AllArgsConstructor
@Data
public class ChangeLessonEvent {

	private final Lesson lesson;

}

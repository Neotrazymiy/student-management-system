package spring.service;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import spring.dto.LessonReadDto;

@Service
@AllArgsConstructor
public class CalendarService {

	public byte[] createCalendar(List<LessonReadDto> lessons) throws Exception {
		Calendar calendar = new Calendar();
		calendar.getProperties().add(new ProdId("-//DistributionOfLessonsAtTheUniversitty//LessonSystem//UK"));
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);

		lessons.stream().map(lesson -> {
			VEvent event = createEvent(lesson);
			calendar.getComponents().add(event);
			return lesson;
		});
		return calendar.toString().getBytes(StandardCharsets.UTF_8);

	}

	private VEvent createEvent(LessonReadDto lesson) {
		java.util.Calendar startCal = java.util.Calendar.getInstance();
		startCal.setTime(
				Date.from(lesson.getDate().atTime(lesson.getEndTime()).atZone(ZoneId.systemDefault()).toInstant()));

		java.util.Calendar endCal = java.util.Calendar.getInstance();
		endCal.setTime(
				Date.from(lesson.getDate().atTime(lesson.getEndTime()).atZone(ZoneId.systemDefault()).toInstant()));

		DateTime startTime = new DateTime(startCal.getTime());
		DateTime endTime = new DateTime(endCal.getTime());

		String eventName = lesson.getCourse().getCourseName();
		VEvent event = new VEvent(startTime, endTime, eventName);

		event.getProperties().add(new Uid(lesson.getId().toString()));
		event.getProperties().add(new Description("Курс: " + lesson.getCourse().getCourseName()));
		if (lesson.getRoom() != null) {
			event.getProperties().add(new Location(lesson.getRoom().getNumber()));
		}
		return event;
	}

}

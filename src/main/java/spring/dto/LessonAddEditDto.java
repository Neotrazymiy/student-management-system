package spring.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.model.DateFilter;
import spring.model.LessonStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonAddEditDto {

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	@FutureOrPresent(message = "дата должна быть или сегоднешняя, или в будущем")
	private LocalDate from;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	@FutureOrPresent(message = "дата должна быть или сегоднешняя, или в будущем")
	private LocalDate to;

	@DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
	private LocalTime startTime;

	@DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
	private LocalTime endTime;

	@Size(max = 30, message = "студентов на уроке в аудитории должно быть не больше 30")
	private List<UUID> studentIds = new ArrayList<>();

	@Size(max = 5, message = "групп на уроке должно быть не больше 5")
	private List<UUID> groupIds = new ArrayList<>();

	private DateFilter dateFilter;
	private LessonStatus status;
	private UUID departmentId;
	private UUID courseId;
	private UUID roomId;
	private UUID teacherId;
	private UUID groupId;
	private UUID lessonId;

}

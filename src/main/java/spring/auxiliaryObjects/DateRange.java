package spring.auxiliaryObjects;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DateRange {

	private final LocalDate date;
	private final LocalDate to;
}

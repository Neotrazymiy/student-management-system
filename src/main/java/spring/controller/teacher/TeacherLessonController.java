package spring.controller.teacher;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.AllArgsConstructor;
import spring.auxiliaryObjects.RedirectUtil;
import spring.auxiliaryObjects.RequestPageSizeData;
import spring.dto.LessonAddEditDto;
import spring.dto.LessonReadDto;
import spring.service.CalendarService;
import spring.service.DepartmentService;
import spring.service.LessonService;

@Controller
@AllArgsConstructor
@RequestMapping("/teacher/lessonsSchedule")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherLessonController {

	private final LessonService lessonService;
	private final DepartmentService departmentService;
	private final CalendarService calendarService;

	@GetMapping("")
	public String teacherSchedule(@ModelAttribute RequestPageSizeData pageSizeData,
			@AuthenticationPrincipal UserDetails user, @Valid @ModelAttribute("filter") LessonAddEditDto filter,
			BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			filter.setFrom(null);
			filter.setTo(null);
			redirectAttributes.addFlashAttribute("filter", filter);
			return "redirect:/teacher/lessonsSchedule";
		}

		Page<LessonReadDto> pageLesson = lessonService.getPageTeacherSchedule(
				PageRequest.of(pageSizeData.getPage(), pageSizeData.getSize()), user.getUsername(), filter);

		model.addAttribute("pageLesson", pageLesson);
		model.addAttribute("filter", filter);
		model.addAttribute("currentPage", pageSizeData.getPage());
		model.addAttribute("pageSize", pageSizeData.getSize());
		model.addAttribute("totalPage", pageLesson.getTotalPages());
		model.addAttribute("departments", departmentService.getAllDepartments());
		model.addAttribute("basePath", "/teacher/lessonsSchedule");

		return "teacher/lessons/teacherSchedule";
	}

	@PostMapping("/{id}/update")
	public String teacherUpdate(@PathVariable UUID id, @Valid @ModelAttribute("filter") LessonAddEditDto dto,
			BindingResult bindingResult, @ModelAttribute RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes, Model model) {

		lessonService.updateLesson(id, dto);

		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		if (dto.getFrom() != null) {
			redirectAttributes.addAttribute("from", dto.getFrom().toString());
		}
		if (dto.getTo() != null) {
			redirectAttributes.addAttribute("to", dto.getTo().toString());
		}
		redirectAttributes.addAttribute("dateFilter", dto.getDateFilter());
		redirectAttributes.addAttribute("departmentId", dto.getDepartmentId());

		return "redirect:/teacher/lessonsSchedule";
	}

	@GetMapping("/export")
	public ResponseEntity<byte[]> exportStudentSchedule(@AuthenticationPrincipal UserDetails user,
			@Valid @ModelAttribute("filter") LessonAddEditDto filter) throws Exception {

		List<LessonReadDto> lessonReadDtos = lessonService.getTeacherScheduleForExport(user.getUsername(), filter);
		byte[] ics = calendarService.createCalendar(lessonReadDtos);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=schedule.ics")
				.contentType(MediaType.parseMediaType("text/calendar")).body(ics);
	}

}

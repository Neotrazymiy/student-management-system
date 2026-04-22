package spring.controller.student;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import spring.auxiliaryObjects.RequestPageSizeData;
import spring.controller.BaseLessonController;
import spring.dto.LessonAddEditDto;
import spring.dto.LessonReadDto;

@Controller
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentLessonController extends BaseLessonController {

	@GetMapping("/lessons")
	public String lesson(@ModelAttribute RequestPageSizeData pageSizeData,
			@RequestParam(defaultValue = "0") int groupPage, @RequestParam(defaultValue = "5") int groupSize,
			@Valid @ModelAttribute("filter") LessonAddEditDto filter, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model) {
		return lesson(pageSizeData, groupPage, groupSize, filter, bindingResult, redirectAttributes, model,
				"student/lessons");
	}

	@GetMapping("/studentSchedule")
	public String studentSchedule(@ModelAttribute RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetails user,
			@Valid @ModelAttribute("filter") LessonAddEditDto filter, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			filter.setFrom(null);
			filter.setTo(null);
			redirectAttributes.addFlashAttribute("filter", filter);
			return "redirect:/student/studentSchedule";
		}
		Page<LessonReadDto> pageLesson = lessonService.getPageStudentSchedule(
				PageRequest.of(pageSizeData.getPage(), pageSizeData.getSize()), user.getUsername(), filter);

		model.addAttribute("pageLesson", pageLesson);
		model.addAttribute("filter", filter);
		model.addAttribute("currentPage", pageSizeData.getPage());
		model.addAttribute("pageSize", pageSizeData.getSize());
		model.addAttribute("totalPage", pageLesson.getTotalPages());
		model.addAttribute("basePath", "/student/studentSchedule");
		model.addAttribute("courses", courseService.getAllCourses());
		model.addAttribute("teachers", teacherService.getAllTeachers());
		model.addAttribute("rooms", roomService.getAllRooms());
		model.addAttribute("departments", departmentService.getAllDepartments());

		return "student/lessons/studentSchedule";
	}

	@GetMapping("/studentSchedule/export")
	public ResponseEntity<byte[]> exportStudentSchedule(@AuthenticationPrincipal UserDetails user,
			@Valid @ModelAttribute("filter") LessonAddEditDto filter) throws Exception {
		List<LessonReadDto> lessonReadDtos = lessonService.getStudentScheduleForExport(user.getUsername(), filter);
		byte[] ics = calendarService.createCalendar(lessonReadDtos);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=schedule.ics")
				.contentType(MediaType.parseMediaType("text/calendar")).body(ics);
	}

}

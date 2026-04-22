package spring.controller.teacher;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;
import spring.service.CourseService;

@Controller
@AllArgsConstructor
@RequestMapping("/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherCourseController {

	private final CourseService courseService;

	@GetMapping("/courses")
	public String courses(Model model) {
		model.addAttribute("courses", courseService.getAllCourses());
		return "teacher/courses/courses";
	}

}

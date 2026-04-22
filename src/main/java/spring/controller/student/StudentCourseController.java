package spring.controller.student;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;
import spring.service.CourseService;

@Controller
@AllArgsConstructor
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentCourseController {

	private final CourseService courseService;

	@GetMapping("/courses")
	public String courses(Model model) {
		model.addAttribute("courses", courseService.getAllCourses());
		return "student/courses/courses";
	}

}

package spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.AllArgsConstructor;
import spring.service.CourseService;

@Controller
@AllArgsConstructor
public class CourseController {

	private final CourseService courseService;

	@GetMapping("/courses")
	public String course(Model model) {
		model.addAttribute("courses", courseService.getAllCourses());
		return "courses";
	}

}

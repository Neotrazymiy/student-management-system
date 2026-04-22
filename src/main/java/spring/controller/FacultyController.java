package spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import spring.service.FacultyService;

@Controller
@RequiredArgsConstructor
public class FacultyController {

	private final FacultyService facultyService;

	@GetMapping("/facultys")
	public String faculty(Model model) {
		model.addAttribute("facultys", facultyService.getAllFaculty());
		return "facultys";
	}

}

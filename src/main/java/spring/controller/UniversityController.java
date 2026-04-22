package spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import spring.service.UniversityService;

@Controller
@RequiredArgsConstructor
public class UniversityController {

	private final UniversityService universityService;

	@GetMapping("/universitys")
	public String university(Model model) {
		model.addAttribute("universitys", universityService.getAllUniversitys());
		return "universitys";
	}
}

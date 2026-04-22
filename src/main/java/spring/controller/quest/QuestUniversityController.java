package spring.controller.quest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;
import spring.service.UniversityService;

@Controller
@AllArgsConstructor
@RequestMapping("/quest")
@PreAuthorize("hasRole('QUEST')")
public class QuestUniversityController {

	private final UniversityService universityService;

	@GetMapping("/universitys")
	public String university(Model model) {
		model.addAttribute("universitys", universityService.getAllUniversitys());
		return "quest/universitys";
	}
}

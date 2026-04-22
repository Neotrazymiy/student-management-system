package spring.controller.quest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;
import spring.service.FacultyService;

@Controller
@AllArgsConstructor
@RequestMapping("/quest")
@PreAuthorize("hasRole('QUEST')")
public class QuestFacultyController {

	private final FacultyService facultyService;

	@GetMapping("/facultys")
	public String faculty(Model model) {
		model.addAttribute("facultys", facultyService.getAllFaculty());
		return "quest/facultys";
	}

}

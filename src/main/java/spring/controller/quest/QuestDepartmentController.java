package spring.controller.quest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;
import spring.service.DepartmentService;

@Controller
@AllArgsConstructor
@RequestMapping("/quest")
@PreAuthorize("hasRole('QUEST')")
public class QuestDepartmentController {

	private final DepartmentService departmentService;

	@GetMapping("/departments")
	public String department(Model model) {
		model.addAttribute("departments", departmentService.getAllDepartments());
		return "quest/departments";
	}

}

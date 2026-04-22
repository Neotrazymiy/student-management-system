package spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import spring.service.DepartmentService;

@Controller
@RequiredArgsConstructor
public class DepartmentController {

	private final DepartmentService departmentService;

	@GetMapping("/departments")
	public String department(Model model) {
		model.addAttribute("departments", departmentService.getAllDepartments());
		return "departments";
	}

}

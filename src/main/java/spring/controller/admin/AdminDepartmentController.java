package spring.controller.admin;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.AllArgsConstructor;
import spring.dto.DepartmentAddEditDto;
import spring.service.DepartmentService;
import spring.service.FacultyService;

@Controller
@AllArgsConstructor
@RequestMapping("/admin/departments")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDepartmentController {

	private final DepartmentService departmentService;
	private final FacultyService facultyService;

	@GetMapping("")
	public String departments(Model model, @ModelAttribute("newDepartment") DepartmentAddEditDto departmentDto) {
		model.addAttribute("departments", departmentService.getAllDepartments());
		model.addAttribute("newDepartment", departmentDto);
		model.addAttribute("facultys", facultyService.getAllFaculty());
		return "admin/departments/departments";
	}

	@PostMapping("/{id}/update")
	public String updateDepartment(@PathVariable UUID id, @Valid @ModelAttribute DepartmentAddEditDto departmentDto,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/admin/departments";
		}
		departmentService.updateDepartment(id, departmentDto);
		return "redirect:/admin/departments";
	}

	@PostMapping("/{id}/delete")
	public String deleteDepartment(@PathVariable UUID id, Model model) {
		departmentService.deleteDepartmentById(id);
		return "redirect:/admin/departments";
	}

	@PostMapping("/new")
	public String createDepartment(@Valid @ModelAttribute("newDepartment") DepartmentAddEditDto departmentDto,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("newDepartment", departmentDto);
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/admin/departments";
		}
		departmentService.addDepartment(departmentDto);
		return "redirect:/admin/departments";
	}

}

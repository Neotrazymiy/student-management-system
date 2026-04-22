package spring.controller.admin;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.AllArgsConstructor;
import spring.dto.FacultyAddEditDto;
import spring.service.FacultyService;
import spring.service.UniversityService;

@Component
@AllArgsConstructor
@RequestMapping("/admin/facultys")
@PreAuthorize("hasRole('ADMIN')")
public class AdminFacultyController {

	private final FacultyService facultyService;
	private final UniversityService universityService;

	@GetMapping("")
	public String facyltys(Model model, @ModelAttribute("newFaculty") FacultyAddEditDto facultyDto) {
		model.addAttribute("facultys", facultyService.getAllFaculty());
		model.addAttribute("newFaculty", facultyDto);
		model.addAttribute("universitys", universityService.getAllUniversitys());
		return "admin/facultys/facultys";
	}

	@PostMapping("/{id}/update")
	public String updateFaculty(@PathVariable UUID id, @Valid @ModelAttribute FacultyAddEditDto facultyDto,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		facultyService.updateFaculty(id, facultyDto);
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/admin/facultys";
		}
		return "redirect:/admin/facultys";
	}

	@PostMapping("/{id}/delete")
	public String deleteFaculty(@PathVariable UUID id, Model model) {
		facultyService.deleteFacultyById(id);
		return "redirect:/admin/facultys";
	}

	@PostMapping("/new")
	public String createFacylty(@Valid @ModelAttribute FacultyAddEditDto facultyDto, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("newFaculty", facultyDto);
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/admin/facultys";
		}
		facultyService.addFaculty(facultyDto);
		return "redirect:/admin/facultys";
	}

}

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import spring.auxiliaryObjects.RequestPageSizeData;
import spring.controller.BaseCourseController;
import spring.dto.CourseAddEditDto;

@Controller
@RequestMapping("/admin/courses")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCourseController extends BaseCourseController {

	@GetMapping("")
	public String courses(@ModelAttribute RequestPageSizeData pageSizeData,
			@ModelAttribute("newCourse") CourseAddEditDto courseDto, Model model,
			@RequestParam(defaultValue = "0") int groupPage, @RequestParam(defaultValue = "5") int groupSize) {
		return courses(pageSizeData, courseDto, model, groupPage, groupSize, "admin/courses");
	}

	@PostMapping("/{id}/update")
	public String updateCourse(@PathVariable UUID id, @Valid @ModelAttribute CourseAddEditDto courseDto,
			BindingResult bindingResult, @ModelAttribute RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes) {
		return updateCourse(id, courseDto, bindingResult, pageSizeData, redirectAttributes, "redirect:/admin/courses");
	}

	@PostMapping("/{id}/delete")
	public String deleteCourse(@PathVariable UUID id, @ModelAttribute RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes) {
		return deleteCourse(id, pageSizeData, redirectAttributes, "redirect:/admin/courses");
	}

	@PostMapping("/{courseId}/groups/{groupName}/delete")
	public String removeGroupFromCourse(@PathVariable UUID courseId, @PathVariable String groupName,
			@ModelAttribute RequestPageSizeData pageSizeData, RedirectAttributes redirectAttributes) {
		return removeGroupFromCourse(courseId, groupName, pageSizeData, redirectAttributes, "redirect:/admin/courses");
	}

	@PostMapping("/new")
	public String createCourse(@Valid @ModelAttribute("newCourse") CourseAddEditDto courseDto,
			BindingResult bindingResult, @ModelAttribute RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes) {
		return createCourse(courseDto, bindingResult, pageSizeData, redirectAttributes, "redirect:/admin/courses");
	}

}

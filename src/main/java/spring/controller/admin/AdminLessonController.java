package spring.controller.admin;

import java.util.UUID;

import javax.validation.Valid;

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
import spring.controller.BaseLessonController;
import spring.dto.LessonAddEditDto;

@Controller
@RequestMapping("/admin/lessons")
public class AdminLessonController extends BaseLessonController {

	@GetMapping("")
	public String lesson(Model model, @ModelAttribute("newLesson") LessonAddEditDto newLesson,
			@RequestParam(required = false) String source, @ModelAttribute LessonAddEditDto dto,
			@ModelAttribute LessonAddEditDto updateDto, RequestPageSizeData pageSizeData) {
		return lesson(model, newLesson, source, dto, updateDto, pageSizeData, "admin/lessons");
	}

	@PostMapping("/{id}/update")
	public String updateLesson(@PathVariable UUID id, @Valid @ModelAttribute LessonAddEditDto dto,
			BindingResult bindingResult, RedirectAttributes redirectAttributes, RequestPageSizeData pageSizeData) {
		return updateLesson(id, dto, bindingResult, redirectAttributes, pageSizeData, "redirect:/admin/lessons");
	}

	@PostMapping("/{id}/delete")
	public String deleteLesson(@PathVariable UUID id, RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes) {
		return deleteLesson(id, pageSizeData, redirectAttributes, "redirect:/admin/lessons");
	}

	@PostMapping("/new")
	public String createLesson(@Valid @ModelAttribute("newLesson") LessonAddEditDto newLesson,
			BindingResult bindingResult, RequestPageSizeData pageSizeData, RedirectAttributes redirectAttributes) {
		return createLesson(newLesson, bindingResult, pageSizeData, redirectAttributes, "redirect:/admin/lessons");
	}

}

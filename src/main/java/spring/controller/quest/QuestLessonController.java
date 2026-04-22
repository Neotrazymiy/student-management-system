package spring.controller.quest;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import spring.auxiliaryObjects.RequestPageSizeData;
import spring.controller.BaseLessonController;
import spring.dto.LessonAddEditDto;

@Controller
@RequestMapping("/quest")
@PreAuthorize("hasRole('QUEST')")
public class QuestLessonController extends BaseLessonController {

	@GetMapping("/lessons")
	public String lesson(@ModelAttribute RequestPageSizeData pageSizeData,
			@RequestParam(defaultValue = "0") int groupPage, @RequestParam(defaultValue = "5") int groupSize,
			@Valid @ModelAttribute("filter") LessonAddEditDto filter, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model) {
		return lesson(pageSizeData, groupPage, groupSize, filter, bindingResult, redirectAttributes, model,
				"quest/lessons");
	}

}

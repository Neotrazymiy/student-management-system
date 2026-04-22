package spring.controller.methodist;

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

import spring.auxiliaryObjects.RequestPageSizeData;
import spring.controller.BaseGroupController;
import spring.dto.GroupAddEditDto;

@Controller
@RequestMapping("/methodist/groups")
@PreAuthorize("hasRole('METHODIST')")
public class MethodistGroupController extends BaseGroupController {

	@GetMapping("")
	public String groups(@ModelAttribute("newGroup") GroupAddEditDto groupDto,
			@ModelAttribute RequestPageSizeData pageSizeData, Model model) {
		return groups(pageSizeData, groupDto, model, "methodist/groups");
	}

	@PostMapping("/{id}/update")
	public String updateGroup(@PathVariable UUID id, @Valid @ModelAttribute GroupAddEditDto groupDto,
			BindingResult bindingResult, @ModelAttribute RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes) {
		return updateGroup(id, groupDto, bindingResult, pageSizeData, redirectAttributes, "redirect:/methodist/groups");
	}

	@PostMapping("/new")
	public String createGroup(@Valid @ModelAttribute("newGroup") GroupAddEditDto groupDto, BindingResult bindingResult,
			@ModelAttribute RequestPageSizeData pageSizeData, RedirectAttributes redirectAttributes) {
		return createGroup(groupDto, bindingResult, pageSizeData, redirectAttributes, "redirect:/methodist/groups");
	}

}

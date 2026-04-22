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
import spring.auxiliaryObjects.RedirectUtil;
import spring.auxiliaryObjects.RequestPageSizeData;
import spring.controller.BaseGroupController;
import spring.dto.GroupAddEditDto;
import spring.service.GroupService;

@Controller
@AllArgsConstructor
@RequestMapping("/admin/groups")
@PreAuthorize("hasRole('ADMIN')")
public class AdminGroupController extends BaseGroupController {

	private final GroupService groupService;

	@GetMapping("")
	public String groups(@ModelAttribute RequestPageSizeData pageSizeData,
			@ModelAttribute("newGroup") GroupAddEditDto groupDto, Model model) {
		return groups(pageSizeData, groupDto, model, "admin/groups");
	}

	@PostMapping("/{id}/update")
	public String updateGroup(@PathVariable UUID id, @Valid @ModelAttribute GroupAddEditDto groupDto,
			BindingResult bindingResult, @ModelAttribute RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes) {
		return updateGroup(id, groupDto, bindingResult, pageSizeData, redirectAttributes, "redirect:/admin/groups");
	}

	@PostMapping("/{id}/delete")
	public String deleteGroup(@PathVariable UUID id, Model model, @ModelAttribute RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes) {
		groupService.deleteGroupById(id);
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return "redirect:/admin/groups";
	}

	@PostMapping("/new")
	public String createGroup(@Valid @ModelAttribute("newGroup") GroupAddEditDto groupDto, BindingResult bindingResult,
			@ModelAttribute RequestPageSizeData pageSizeData, RedirectAttributes redirectAttributes) {
		return createGroup(groupDto, bindingResult, pageSizeData, redirectAttributes, "redirect:/admin/groups");
	}

}

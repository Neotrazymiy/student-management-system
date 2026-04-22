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
import spring.dto.RoleAddEditDto;
import spring.service.RoleService;

@Controller
@AllArgsConstructor
@RequestMapping("/admin/roles")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoleController {

	private final RoleService roleService;

	@GetMapping("")
	public String roles(@ModelAttribute("newRole") RoleAddEditDto roleDto, Model model) {
		model.addAttribute("roles", roleService.getAllRoles());
		model.addAttribute("newRole", roleDto);
		return "admin/roles/roles";
	}

	@PostMapping("/{id}/update")
	public String updateRole(@PathVariable UUID id, @Valid @ModelAttribute RoleAddEditDto roleDto,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/admin/roles";
		}
		roleService.updateRole(id, roleDto);
		return "redirect:/admin/roles";
	}

	@PostMapping("/{id}/delete")
	public String deleteRole(@PathVariable UUID id, Model model) {
		roleService.deleteRoleById(id);
		return "redirect:/admin/roles";
	}

	@PostMapping("/new")
	public String createRole(@Valid @ModelAttribute("newRole") RoleAddEditDto roleDto, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("newRole", roleDto);
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/admin/roles";
		}
		roleService.addRole(roleDto);
		return "redirect:/admin/roles";
	}

}

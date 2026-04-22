package spring.controller.admin;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.AllArgsConstructor;
import spring.auxiliaryObjects.RedirectUtil;
import spring.auxiliaryObjects.RequestPageSizeData;
import spring.dto.RoleIdsDto;
import spring.dto.TeacherAddEditDto;
import spring.dto.TeacherReadDto;
import spring.dto.UserReadDto;
import spring.model.RoleElement;
import spring.service.AdminService;
import spring.service.RoleService;
import spring.service.TeacherService;
import spring.service.UserService;

@Controller
@AllArgsConstructor
@RequestMapping("/admin/teachers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTeacherController {

	private final TeacherService teacherService;
	private final RoleService roleService;
	private final UserService userService;
	private final AdminService adminService;

	@GetMapping("")
	public String teacher(@ModelAttribute RequestPageSizeData pageSizeData, Model model) {
		Page<TeacherReadDto> pageTeacher = teacherService
				.getAllPageTeachers(PageRequest.of(pageSizeData.getPage(), pageSizeData.getSize()));
		model.addAttribute("pageTeacher", pageTeacher);
		model.addAttribute("currentPage", pageSizeData.getPage());
		model.addAttribute("pageSize", pageSizeData.getSize());
		model.addAttribute("totalPage", pageTeacher.getTotalPages());
		model.addAttribute("basePath", "/admin/teachers");
		return "admin/teachers/teachers";
	}

	@GetMapping("/{id}")
	public String editTeacher(@PathVariable UUID id, Model model) {
		return teacherService.getTeacherById(id).map(teacher -> {
			model.addAttribute("teacher", teacher);
			return "admin/teachers/edit-teacher";
		}).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@PostMapping("/{id}/update")
	public String updateTeacher(@PathVariable UUID id, @Valid @ModelAttribute TeacherAddEditDto teacherDto,
			BindingResult bindingResult, @ModelAttribute RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/admin/teachers/{id}";
		}
		teacherService.updateTeacher(id, teacherDto);
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return "redirect:/admin/teachers";
	}

	@PostMapping("/{id}/delete")
	public String deleteTeacher(@PathVariable UUID id, @ModelAttribute RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes) {
		adminService.deleteTeacherAndRoleWithUser(id);
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return "redirect:/admin/teachers";
	}

	@GetMapping("/{id}/roles")
	public String editRoleTeacher(@PathVariable UUID id, @RequestParam(required = false) String returnUrl,
			Model model) {
		model.addAttribute("user", userService.getUserById(id).get());
		model.addAttribute("teacher", teacherService.getTeacherByUserId(id));
		model.addAttribute("role", roleService.getAllRoles());
		model.addAttribute("returnUrl", returnUrl);
		return "admin/teachers/edit-roleTeacher";
	}

	@PostMapping("/{id}/roles")
	public String updateTeacherRoles(@PathVariable UUID id, @ModelAttribute RoleIdsDto roleIds,
			@RequestParam(required = false) String returnUrl) {
		UserReadDto user = adminService.updateRoles(id, roleIds.getRoleIds());
		if (user.getRoles().size() == 1 && !adminService.checkChooseRole(user, RoleElement.ROLE_TEACHER)) {
			return "redirect:/admin/teachers";
		}
		if (returnUrl != null && !returnUrl.isEmpty()) {
			return "redirect:" + returnUrl;
		}
		return "redirect:/admin/teachers";
	}
}

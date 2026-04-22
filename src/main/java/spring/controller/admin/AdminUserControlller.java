package spring.controller.admin;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.AllArgsConstructor;
import spring.auxiliaryObjects.RedirectUtil;
import spring.auxiliaryObjects.RequestPageSizeData;
import spring.dto.RoleReadDto;
import spring.dto.UserAddEditDto;
import spring.dto.UserReadDto;
import spring.model.RoleElement;
import spring.service.AdminService;
import spring.service.RoleService;
import spring.service.UserService;

@Controller
@AllArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserControlller {

	private final UserService userService;
	private final AdminService adminService;
	private final RoleService roleService;

	@GetMapping("/admins")
	public String user(@ModelAttribute RequestPageSizeData pageSizeData, Model model) {
		Pageable pageable = PageRequest.of(pageSizeData.getPage(), pageSizeData.getSize());

		List<RoleReadDto> rolesToExclude = Arrays.asList(
				roleService.getRoleByName(RoleElement.ROLE_TEACHER.name()).get(),
				roleService.getRoleByName(RoleElement.ROLE_STUDENT.name()).get());

		Page<UserReadDto> pageUser = userService.getUsersExcludingRoles(rolesToExclude, pageable);
		model.addAttribute("pageUser", pageUser);
		model.addAttribute("currentPage", pageSizeData.getPage());
		model.addAttribute("pageSize", pageSizeData.getSize());
		model.addAttribute("totalPage", pageUser.getTotalPages());
		model.addAttribute("basePath", "/admin/admins");
		return "admin/users/users";
	}

	@GetMapping("/users/{id}")
	public String editUser(@PathVariable UUID id, Model model) {
		return userService.getUserById(id).map(user -> {
			model.addAttribute("user", user);
			model.addAttribute("userId", id);
			return "admin/users/edit-user";
		}).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@PostMapping("/users/{id}/update")
	public String updateUser(@PathVariable UUID id, @Valid @ModelAttribute UserAddEditDto user,
			BindingResult bindingResult, @ModelAttribute RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/admin/users/{id}";
		}
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return userService.updateUser(id, user).map(re -> "redirect:/admin/admins")
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/users/new")
	public String newUser(Model model, @ModelAttribute("user") UserAddEditDto user) {
		UserReadDto dto = new UserReadDto();
		model.addAttribute("user", user);
		model.addAttribute("userId", dto.getId());
		return "admin/users/edit-user";
	}

	@PostMapping("/users")
	public String createUser(@Valid @ModelAttribute("user") UserAddEditDto user, BindingResult bindingResult,
			@ModelAttribute RequestPageSizeData pageSizeData, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("user", user);
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/admin/users/new";
		}
		userService.addUser(user);
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return "redirect:/admin/admins";
	}

	@PostMapping("/teachers/{id}/new")
	public String newTeacher(@PathVariable UUID id, @ModelAttribute RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes) {
		adminService.makeTeacherWithUser(id);
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return "redirect:/admin/admins";
	}

	@PostMapping("/students/{id}/new")
	public String newStudent(@PathVariable UUID id, @ModelAttribute RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes) {
		adminService.makeStudentWithUser(id);
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return "redirect:/admin/admins";
	}

	@PostMapping("/users/{id}/delete")
	public String deleteUser(@PathVariable UUID id, @ModelAttribute RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes) {
		if (!userService.deleteUserById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return "redirect:/admin/admins";
	}

	@PostMapping("/admins/{id}/new")
	public String addAdminUser(@PathVariable UUID id, @ModelAttribute RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes) {
		adminService.addRoleAdminUser(id);
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return "redirect:/admin/admins";
	}

	@PostMapping("/admins/{id}/delete")
	public String deleteAdmin(@PathVariable UUID id, @ModelAttribute RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes) {
		adminService.deleteRoleByUserId(id);
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return "redirect:/admin/admins";
	}

	@PostMapping("/methodist/{id}/new")
	public String addMethodistUser(@PathVariable UUID id, @ModelAttribute RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes) {
		adminService.addRoleMethodistUser(id);
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return "redirect:/admin/admins";
	}

}

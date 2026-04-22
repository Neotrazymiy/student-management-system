package spring.controller.admin;

import java.util.UUID;

import javax.validation.Valid;

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
import spring.controller.BaseStudentController;
import spring.dto.GroupIdDto;
import spring.dto.RoleIdsDto;
import spring.dto.StudentAddEditDto;
import spring.dto.UserReadDto;
import spring.model.RoleElement;
import spring.service.AdminService;
import spring.service.RoleService;
import spring.service.UserService;

@Controller
@AllArgsConstructor
@RequestMapping("/admin/students")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStudentController extends BaseStudentController {

	private final AdminService adminService;
	private final RoleService roleService;
	private final UserService userService;

	@GetMapping("")
	public String student(@ModelAttribute RequestPageSizeData pageSizeData,
			@RequestParam(defaultValue = "0") int groupPage, @RequestParam(defaultValue = "5") int groupSize,
			@RequestParam(required = false) UUID groupId, Model model) {
		return student(pageSizeData, groupPage, groupSize, groupId, model, "admin/students");
	}

	@GetMapping("/{id}")
	public String editStudent(@PathVariable UUID id, Model model) {
		return studentService.getStudentById(id).map(student -> {
			model.addAttribute("student", student);
			return "admin/students/edit-student";
		}).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@PostMapping("/{id}/update")
	public String updateStudent(@PathVariable UUID id, @Valid @ModelAttribute StudentAddEditDto studentDto,
			BindingResult bindingResult, @ModelAttribute RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/admin/students/{id}";
		}
		studentService.updateStudent(id, studentDto);
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return "redirect:/admin/students";
	}

	@PostMapping("/{id}/delete")
	public String deleteStudent(@PathVariable UUID id, @ModelAttribute RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes) {
		adminService.deleteStudentAndRoleWithUser(id);
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return "redirect:/admin/students";
	}

	@GetMapping("/{id}/group-edit")
	public String editGroup(@PathVariable UUID id, @RequestParam(required = false) String returnUrl, Model model) {
		model.addAttribute("student", studentService.getStudentById(id).get());
		model.addAttribute("groups", groupService.getAllGroups());
		model.addAttribute("returnUrl", returnUrl);
		return "admin/students/edit-group";
	}

	@PostMapping("/{id}/group-edit")
	public String updateStudentGroup(@PathVariable UUID id, @ModelAttribute GroupIdDto groupId,
			@RequestParam(required = false) String returnUrl) {
		adminService.makeUserStudent(id, groupId.getGroupId());
		if (returnUrl != null && !returnUrl.isEmpty()) {
			return "redirect:" + returnUrl;
		}
		return "redirect:/admin/edit-student";
	}

	@GetMapping("/{id}/roles")
	public String editRole(@PathVariable UUID id, @RequestParam(required = false) String returnUrl, Model model) {
		model.addAttribute("user", userService.getUserById(id).get());
		model.addAttribute("student", studentService.getStudentByUserId(id));
		model.addAttribute("role", roleService.getAllRoles());
		model.addAttribute("returnUrl", returnUrl);
		return "admin/students/edit-roleStudent";
	}

	@PostMapping("/{id}/roles")
	public String updateStudentRoles(@PathVariable UUID id, @ModelAttribute RoleIdsDto roleIds,
			@RequestParam(required = false) String returnUrl) {
		UserReadDto user = adminService.updateRoles(id, roleIds.getRoleIds());
		if (user.getRoles().size() == 1 && !adminService.checkChooseRole(user, RoleElement.ROLE_STUDENT)) {
			return "redirect:/admin/students";
		}
		if (returnUrl != null && !returnUrl.isEmpty()) {
			return "redirect:" + returnUrl;
		}
		return "redirect:/admin/students";
	}

}

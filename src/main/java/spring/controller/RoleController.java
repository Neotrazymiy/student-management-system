package spring.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import spring.dto.RoleReadDto;
import spring.service.RoleService;

@Controller
@RequiredArgsConstructor
public class RoleController {

	private final RoleService roleService;

	@GetMapping("/roles")
	public String role(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			Model model) {
		Page<RoleReadDto> pageRole = roleService.getAllPageRoles(PageRequest.of(page, size));
		model.addAttribute("pageRole", pageRole);
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("totalPage", pageRole.getTotalPages());
		model.addAttribute("basePath", "/roles");
		return "roles";
	}
}
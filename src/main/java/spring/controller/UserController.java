package spring.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import spring.dto.UserReadDto;
import spring.service.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/users")
	public String user(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			Model model) {
		Page<UserReadDto> pageUser = userService.getAllPageUsers(PageRequest.of(page, size));
		model.addAttribute("pageUser", pageUser);
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("totalPage", pageUser.getTotalPages());
		model.addAttribute("basePath", "/users");
		return "users";
	}

}

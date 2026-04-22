package spring.controller.quest;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.AllArgsConstructor;
import spring.dto.UserAddEditDto;
import spring.service.UserService;

@Controller
@AllArgsConstructor
@RequestMapping("/quest/registration")
public class QuestRegistrationController {

	private final UserService userService;

	@GetMapping("")
	public String registration(Model model, @ModelAttribute("user") UserAddEditDto user) {
		model.addAttribute("user", user);
		return "registration";
	}

	@PostMapping("/edit")
	public String create(@Valid @ModelAttribute("user") UserAddEditDto user, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("user", user);
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/quest/registration";
		}
		userService.addUser(user);
		return "login";
	}

}

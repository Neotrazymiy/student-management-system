package spring.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler
	public String deleteException(RuntimeException r, Model model) {
		model.addAttribute("exception", r.getMessage());
		return "admin/exception";
	}
}

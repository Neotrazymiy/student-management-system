package spring.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import spring.dto.EnrollmentReadDto;
import spring.service.EnrollmentService;

@Controller
@RequiredArgsConstructor
public class EnrollmentController {

	private final EnrollmentService enrollmentService;

	@GetMapping("/enrollments")
	public String enrollment(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			Model model) {
		Page<EnrollmentReadDto> pageEnrollment = enrollmentService.getAllPageEnrollments(PageRequest.of(page, size));
		model.addAttribute("pageEnrollment", pageEnrollment);
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("totalPage", pageEnrollment.getTotalPages());
		model.addAttribute("basePath", "/enrollments");
		return "enrollments";
	}

}

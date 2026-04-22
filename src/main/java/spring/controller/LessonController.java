package spring.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import spring.dto.LessonReadDto;
import spring.service.LessonService;

@Controller
@RequiredArgsConstructor
public class LessonController {

	private final LessonService lessonService;

	@GetMapping("/lessons")
	public String lesson(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			Model model) {
		Page<LessonReadDto> pageLesson = lessonService.getAllPageLessons(PageRequest.of(page, size));
		model.addAttribute("pageLesson", pageLesson);
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("totalPage", pageLesson.getTotalPages());
		model.addAttribute("basePath", "/lessons");
		return "lessons";
	}

}

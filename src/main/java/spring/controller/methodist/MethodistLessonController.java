package spring.controller.methodist;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import spring.auxiliaryObjects.RequestPageSizeData;
import spring.controller.BaseLessonController;
import spring.dto.GroupReadDto;
import spring.dto.LessonAddEditDto;
import spring.dto.StudentReadDto;

@Controller
@RequestMapping("/methodist/lessons")
public class MethodistLessonController extends BaseLessonController {

	@GetMapping("")
	public String lesson(Model model, @ModelAttribute("newLesson") LessonAddEditDto newLesson,
			@ModelAttribute("source") String source, @ModelAttribute("dto") LessonAddEditDto dto,
			@ModelAttribute("updateDto") LessonAddEditDto updateDto, RequestPageSizeData pageSizeData) {
		return lesson(model, newLesson, source, dto, updateDto, pageSizeData, "methodist/lessons");
	}

	@PostMapping("/{id}/update")
	public String updateLesson(@PathVariable UUID id, @Valid @ModelAttribute LessonAddEditDto updateDto,
			BindingResult bindingResult, RedirectAttributes redirectAttributes, RequestPageSizeData pageSizeData) {
		return updateLesson(id, updateDto, bindingResult, redirectAttributes, pageSizeData,
				"redirect:/methodist/lessons");
	}

	@PostMapping("/{id}/delete")
	public String deleteLesson(@PathVariable UUID id, RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes) {
		return deleteLesson(id, pageSizeData, redirectAttributes, "redirect:/methodist/lessons");
	}

	@PostMapping("/new")
	public String createLesson(@Valid @ModelAttribute("newLesson") LessonAddEditDto newLesson,
			BindingResult bindingResult, RequestPageSizeData pageSizeData, RedirectAttributes redirectAttributes) {
		return createLesson(newLesson, bindingResult, pageSizeData, redirectAttributes, "redirect:/methodist/lessons");
	}

	@PostMapping("/choiceStudents")
	public String choiceStudentLesson(@ModelAttribute("dto") LessonAddEditDto dto,
			@RequestParam(required = false) String source, Model model, RequestPageSizeData pageSizeData) {
		List<UUID> uuids = source.equals("updateDto") ? methodistService.availableStudentsUpdate(dto)
				: methodistService.availableStudentsCreate(dto);
		Page<StudentReadDto> pageStudent = studentService.getPageStudentByIds(uuids,
				PageRequest.of(pageSizeData.getPage(), pageSizeData.getSize()));

		model.addAttribute("pageStudent", pageStudent);
		model.addAttribute("currentPage", pageSizeData.getPage());
		model.addAttribute("pageSize", pageSizeData.getSize());
		model.addAttribute("totalPage", pageStudent.getTotalPages());
		model.addAttribute("basePath", "/methodist/lessons/choiceStudents");
		model.addAttribute("currentStudents",
				source.equals("updateDto") ? studentService.getStudentByIds(dto.getStudentIds())
						: new ArrayList<StudentReadDto>());
		model.addAttribute("dto", dto);
		model.addAttribute("source", source);
		return "methodist/lessons/addStudents";
	}

	@PostMapping("/returnStudents")
	public String returnStudentLesson(@RequestParam(required = false) String source,
			@Valid @ModelAttribute("dto") LessonAddEditDto dto, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/methodist/lessons/choiceStudents";
		}
		boolean existsDto = source.equals("dto");
		redirectAttributes.addFlashAttribute("newLesson", existsDto ? dto : null);
		redirectAttributes.addFlashAttribute("dto", existsDto ? dto : new LessonAddEditDto());
		redirectAttributes.addFlashAttribute("updateDto", existsDto ? new LessonAddEditDto() : dto);
		redirectAttributes.addFlashAttribute("source", source);
		return "redirect:/methodist/lessons";
	}

	@PostMapping("/choiceGroups")
	public String choiceGroups(@ModelAttribute("dto") LessonAddEditDto dto, RequestPageSizeData pageSizeData,
			@RequestParam(required = false) String source, Model model) {
		List<UUID> uuids = source.equals("updateDto") ? methodistService.availableGroupsUpdate(dto)
				: methodistService.availableGroupsCreate(dto);
		Page<GroupReadDto> pageGroup = groupService.getPageGroupsByIds(uuids,
				PageRequest.of(pageSizeData.getPage(), pageSizeData.getSize()));

		model.addAttribute("pageGroup", pageGroup);
		model.addAttribute("currentPage", pageSizeData.getPage());
		model.addAttribute("pageSize", pageSizeData.getSize());
		model.addAttribute("totalPage", pageGroup.getTotalPages());
		model.addAttribute("basePath", "/methodist/lessons/choiceGroups");
		model.addAttribute("currentGroups", source.equals("updateDto") ? groupService.getGroupsByIds(dto.getGroupIds())
				: new ArrayList<GroupReadDto>());
		model.addAttribute("dto", dto);
		model.addAttribute("source", source);
		return "methodist/lessons/addGroups";
	}

	@PostMapping("/returnGroups")
	public String returnGroupLesson(@RequestParam(required = false) String source,
			@Valid @ModelAttribute("dto") LessonAddEditDto dto, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/methodist/lessons/choiceGroups";
		}
		boolean existsDto = source.equals("dto");
		redirectAttributes.addFlashAttribute("newLesson", existsDto ? dto : null);
		redirectAttributes.addFlashAttribute("dto", existsDto ? dto : new LessonAddEditDto());
		redirectAttributes.addFlashAttribute("updateDto", existsDto ? new LessonAddEditDto() : dto);
		redirectAttributes.addFlashAttribute("source", source);
		return "redirect:/methodist/lessons";
	}

}

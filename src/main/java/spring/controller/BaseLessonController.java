package spring.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import spring.auxiliaryObjects.RedirectUtil;
import spring.auxiliaryObjects.RequestPageSizeData;
import spring.dto.GroupReadDto;
import spring.dto.LessonAddEditDto;
import spring.dto.LessonReadDto;
import spring.service.CalendarService;
import spring.service.CourseService;
import spring.service.DepartmentService;
import spring.service.GroupService;
import spring.service.LessonService;
import spring.service.MethodistService;
import spring.service.RoomService;
import spring.service.StudentService;
import spring.service.TeacherService;

public abstract class BaseLessonController {

	@Autowired
	protected LessonService lessonService;

	@Autowired
	protected GroupService groupService;

	@Autowired
	protected TeacherService teacherService;

	@Autowired
	protected CourseService courseService;

	@Autowired
	protected RoomService roomService;

	@Autowired
	protected DepartmentService departmentService;

	@Autowired
	protected StudentService studentService;

	@Autowired
	protected MethodistService methodistService;

	@Autowired
	protected CalendarService calendarService;

	public String lesson(RequestPageSizeData pageSizeData, int groupPage, int groupSize, LessonAddEditDto filter,
			BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model, String view) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			filter.setFrom(null);
			filter.setTo(null);
			redirectAttributes.addFlashAttribute("filter", filter);
			return "redirect:/" + view;
		}
		Page<LessonReadDto> pageLesson = lessonService
				.getPageLessonsfilter(PageRequest.of(pageSizeData.getPage(), pageSizeData.getSize()), filter);

		Page<GroupReadDto> pageGroup = groupService.getAllPageGroups(PageRequest.of(groupPage, groupSize));

		model.addAttribute("pageLesson", pageLesson);
		model.addAttribute("filter", filter);
		model.addAttribute("currentPage", pageSizeData.getPage());
		model.addAttribute("pageSize", pageSizeData.getSize());
		model.addAttribute("totalPage", pageLesson.getTotalPages());
		model.addAttribute("basePath", "/" + view);
		model.addAttribute("pageGroup", pageGroup);
		model.addAttribute("totalPages", pageGroup.getTotalPages());
		model.addAttribute("courses", courseService.getAllCourses());
		model.addAttribute("teachers", teacherService.getAllTeachers());
		model.addAttribute("rooms", roomService.getAllRooms());

		return view + "/lessonsFilter";
	}

	public String lesson(Model model, LessonAddEditDto newLesson, String source, LessonAddEditDto dto,
			LessonAddEditDto updateDto, RequestPageSizeData pageSizeData, String view) {

		Page<LessonReadDto> pageLesson = lessonService
				.getAllPageLessons(PageRequest.of(pageSizeData.getPage(), pageSizeData.getSize()));

		model.addAttribute("pageLesson", pageLesson);
		model.addAttribute("currentPage", pageSizeData.getPage());
		model.addAttribute("pageSize", pageSizeData.getSize());
		model.addAttribute("totalPage", pageLesson.getTotalPages());
		model.addAttribute("basePath", "/" + view);
		model.addAttribute("courses", courseService.getAllCourses());
		model.addAttribute("rooms", roomService.getAllRooms());
		model.addAttribute("lessonsStudents",
				lessonService.getAllMapLessons(PageRequest.of(pageSizeData.getPage(), pageSizeData.getSize())));
		model.addAttribute("newLesson", newLesson);
		model.addAttribute("source", source);
		model.addAttribute("studentsDto", studentService.getStudentByIds(dto != null ? dto.getStudentIds() : null));
		model.addAttribute("studentsUpdateDto",
				studentService.getStudentByIds(updateDto != null ? updateDto.getStudentIds() : null));
		model.addAttribute("groupsUpdateDto",
				groupService.getGroupsByIds(updateDto != null ? updateDto.getGroupIds() : null));
		model.addAttribute("groupsDto", groupService.getGroupsByIds(dto != null ? dto.getGroupIds() : null));
		model.addAttribute("dto", dto);
		model.addAttribute("updateDto", updateDto);
		model.addAttribute("onlyChoiceStudentsNew", dto != null && !dto.getStudentIds().isEmpty());
		model.addAttribute("onlyChoiceGroupsNew", dto != null && !dto.getGroupIds().isEmpty());
		model.addAttribute("choiceStudentsOrGroups", dto.getStudentIds().isEmpty() && dto.getGroupIds().isEmpty());
		model.addAttribute("onlyChoiceGroupsUpdate", updateDto.getGroupIds() != null
				&& !updateDto.getGroupIds().isEmpty() && updateDto.getStudentIds().isEmpty());
		model.addAttribute("onlyChoiceStudentsUpdate", updateDto.getStudentIds() != null
				&& !updateDto.getStudentIds().isEmpty() && updateDto.getGroupIds().isEmpty());

		return view + "/lessons";
	}

	public String updateLesson(UUID id, LessonAddEditDto updateDto, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, RequestPageSizeData pageSizeData, String view) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return view;
		}
		lessonService.updateLesson(id, updateDto);
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return view;
	}

	public String createLesson(LessonAddEditDto newLesson, BindingResult bindingResult,
			RequestPageSizeData pageSizeData, RedirectAttributes redirectAttributes, String view) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("newLesson", newLesson);
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return view;
		}
		if (newLesson.getGroupIds().size() > 0) {
			lessonService.addLessonForGroups(newLesson);
		} else if (newLesson.getStudentIds().size() > 0) {
			lessonService.addLessonForStudents(newLesson);
		}
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return view;
	}

	public String deleteLesson(UUID id, RequestPageSizeData pageSizeData, RedirectAttributes redirectAttributes,
			String view) {
		if (!lessonService.deleteLessonById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return view;
	}

}

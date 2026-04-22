package spring.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import spring.auxiliaryObjects.RedirectUtil;
import spring.auxiliaryObjects.RequestPageSizeData;
import spring.dto.CourseAddEditDto;
import spring.dto.CourseReadDto;
import spring.dto.GroupReadDto;
import spring.service.AdminService;
import spring.service.CourseService;
import spring.service.DepartmentService;
import spring.service.GroupService;
import spring.service.TeacherService;

@Controller
public abstract class BaseCourseController {

	@Autowired
	protected CourseService courseService;

	@Autowired
	protected AdminService adminService;

	@Autowired
	protected GroupService groupService;

	@Autowired
	protected DepartmentService departmentService;

	@Autowired
	protected TeacherService teacherService;

	public String course(RequestPageSizeData pageSizeData, Model model, String view) {
		Page<CourseReadDto> pageCourse = courseService
				.getAllPageCourses(PageRequest.of(pageSizeData.getPage(), pageSizeData.getSize()));
		model.addAttribute("pageCourse", pageCourse);
		model.addAttribute("currentPage", pageSizeData.getPage());
		model.addAttribute("pageSize", pageSizeData.getSize());
		model.addAttribute("totalPage", pageCourse.getTotalPages());
		model.addAttribute("basePath", view);
		return view;
	}

	public String courses(RequestPageSizeData pageSizeData, CourseAddEditDto courseDto, Model model, int groupPage,
			int groupSize, String view) {
		Page<CourseReadDto> pageCourse = courseService
				.getAllPageCourses(PageRequest.of(pageSizeData.getPage(), pageSizeData.getSize()));

		Page<GroupReadDto> pageGroup = groupService.getAllPageGroups(PageRequest.of(groupPage, groupSize));

		model.addAttribute("pageCourse", pageCourse);
		model.addAttribute("currentPage", pageSizeData.getPage());
		model.addAttribute("pageSize", pageSizeData.getSize());
		model.addAttribute("totalPage", pageCourse.getTotalPages());
		model.addAttribute("basePath", "/" + view);
		model.addAttribute("newCourse", courseDto);
		model.addAttribute("departments", departmentService.getAllDepartments());
		model.addAttribute("pageGroup", pageGroup);
		model.addAttribute("totalPages", pageGroup.getTotalPages());
		model.addAttribute("teachers", teacherService.getAllTeachers());

		return view + "/courses";
	}

	public String updateCourse(UUID id, CourseAddEditDto dto, BindingResult bindingResult,
			RequestPageSizeData pageSizeData, RedirectAttributes redirectAttributes, String view) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return view;
		}
		courseService.updateCourse(id, dto);
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return view;
	}

	public String deleteCourse(UUID id, RequestPageSizeData pageSizeData, RedirectAttributes redirectAttributes,
			String view) {
		if (!courseService.deleteCourseById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return view;
	}

	public String removeGroupFromCourse(UUID courseId, String groupName, RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes, String view) {
		adminService.removeGroup(courseId, groupName);
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return view;
	}

	public String createCourse(CourseAddEditDto dto, BindingResult bindingResult, RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes, String view) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("newCourse", dto);
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return view;
		}
		courseService.addCourse(dto);
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return view;
	}

}

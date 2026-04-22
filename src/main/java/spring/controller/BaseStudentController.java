package spring.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import spring.auxiliaryObjects.RequestPageSizeData;
import spring.dto.GroupReadDto;
import spring.dto.StudentReadDto;
import spring.service.GroupService;
import spring.service.StudentService;

@Controller
public abstract class BaseStudentController {

	@Autowired
	protected StudentService studentService;

	@Autowired
	protected GroupService groupService;

	public String student(RequestPageSizeData pageSizeData, int groupPage, int groupSize, UUID groupId, Model model,
			String view) {
		Page<StudentReadDto> pageStudent = studentService
				.getAllPageStudents(PageRequest.of(pageSizeData.getPage(), pageSizeData.getSize()), groupId);
		Page<GroupReadDto> pageGroup = groupService.getAllPageGroups(PageRequest.of(groupPage, groupSize));

		model.addAttribute("groupId", groupId);
		model.addAttribute("pageStudent", pageStudent);
		model.addAttribute("currentPage", pageSizeData.getPage());
		model.addAttribute("pageSize", pageSizeData.getSize());
		model.addAttribute("totalPage", pageStudent.getTotalPages());
		model.addAttribute("pageGroup", pageGroup);
		model.addAttribute("totalPages", pageGroup.getTotalPages());
		model.addAttribute("basePath", "/" + view);

		return view + "/students";
	}
}

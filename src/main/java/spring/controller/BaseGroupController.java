package spring.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import spring.auxiliaryObjects.RedirectUtil;
import spring.auxiliaryObjects.RequestPageSizeData;
import spring.dto.GroupAddEditDto;
import spring.dto.GroupReadDto;
import spring.service.DepartmentService;
import spring.service.GroupService;

@Controller
public abstract class BaseGroupController {

	@Autowired
	protected GroupService groupService;

	@Autowired
	protected DepartmentService departmentService;

	public String groups(RequestPageSizeData pageSizeData, GroupAddEditDto dto, Model model, String view) {
		Page<GroupReadDto> pageGroup = groupService
				.getAllPageGroups(PageRequest.of(pageSizeData.getPage(), pageSizeData.getSize()));

		model.addAttribute("pageGroup", pageGroup);
		model.addAttribute("currentPage", pageSizeData.getPage());
		model.addAttribute("pageSize", pageSizeData.getSize());
		model.addAttribute("totalPage", pageGroup.getTotalPages());
		model.addAttribute("basePath", "/" + view);
		model.addAttribute("newGroup", dto);
		model.addAttribute("departments", departmentService.getAllDepartments());

		return view + "/groups";
	}

	public String updateGroup(UUID id, GroupAddEditDto dto, BindingResult bindingResult,
			RequestPageSizeData pageSizeData, RedirectAttributes redirectAttributes, String view) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return view;
		}
		groupService.updateGroup(id, dto);
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return view;
	}

	public String createGroup(GroupAddEditDto dto, BindingResult bindingResult, RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes, String view) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("newGroup", dto);
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return view;
		}
		groupService.addGroup(dto);
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return view;
	}

}

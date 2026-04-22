package spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import spring.auxiliaryObjects.RequestPageSizeData;
import spring.dto.GroupAddEditDto;

@Controller
public class GroupController extends BaseGroupController {

	@GetMapping("/groups")
	public String group(@ModelAttribute GroupAddEditDto dto, @ModelAttribute RequestPageSizeData pageSizeData,
			Model model) {
		return groups(pageSizeData, dto, model, "group");
	}
}

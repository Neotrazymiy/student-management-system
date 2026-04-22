package spring.controller.teacher;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import spring.auxiliaryObjects.RequestPageSizeData;
import spring.controller.BaseGroupController;
import spring.dto.GroupAddEditDto;

@Controller
@RequestMapping("/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherGroupController extends BaseGroupController {

	@GetMapping("/groups")
	public String groups(@ModelAttribute GroupAddEditDto dto, @ModelAttribute RequestPageSizeData pageSizeData,
			Model model) {
		return groups(pageSizeData, dto, model, "teacher/groups");
	}

}

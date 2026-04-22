package spring.controller.teacher;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import spring.auxiliaryObjects.RequestPageSizeData;
import spring.controller.BaseStudentController;

@Controller
@RequestMapping("/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherStudentController extends BaseStudentController {

	@GetMapping("/students")
	public String student(@ModelAttribute RequestPageSizeData pageSizeData,
			@RequestParam(defaultValue = "0") int groupPage, @RequestParam(defaultValue = "5") int groupSize,
			@RequestParam(required = false) UUID groupId, Model model) {
		return student(pageSizeData, groupPage, groupSize, groupId, model, "teacher/students");
	}

}

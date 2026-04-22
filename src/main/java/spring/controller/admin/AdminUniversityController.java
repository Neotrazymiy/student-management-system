package spring.controller.admin;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.AllArgsConstructor;
import spring.dto.UniversityAddEditDto;
import spring.model.University;
import spring.service.UniversityService;
import spring.service.importt.CsvService;
import spring.service.importt.XlsxService;

@Controller
@AllArgsConstructor
@RequestMapping("/admin/universitys")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUniversityController {

	private final UniversityService universityService;
	private final CsvService csvService;
	private final XlsxService xlsxService;

	@GetMapping("")
	public String universitys(Model model, @ModelAttribute("newUniversity") UniversityAddEditDto universityDto) {
		model.addAttribute("universitys", universityService.getAllUniversitys());
		model.addAttribute("newUniversity", universityDto);
		return "admin/universitys/universitys";
	}

	@PostMapping("/{id}/update")
	public String updateUniversity(@PathVariable UUID id, @Valid @ModelAttribute UniversityAddEditDto universityDto,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/admin/universitys";
		}
		universityService.updateUniversity(id, universityDto);
		return "redirect:/admin/universitys";
	}

	@PostMapping("/{id}/delete")
	public String deleteUniversity(@PathVariable UUID id, Model model) {
		universityService.deleteUniversityById(id);
		return "redirect:/admin/universitys";
	}

	@PostMapping("/new")
	public String createUniversity(@Valid @ModelAttribute("newUniversity") UniversityAddEditDto universityDto,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("newUniversity", universityDto);
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/admin/universitys";
		}
		universityService.addUniversity(universityDto);
		return "redirect:/admin/universitys";
	}

	@PostMapping("/import")
	public String importUniversity(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		String filename = file.getOriginalFilename();
		if (filename.endsWith(".csv")) {
			csvService.importCsv(file, University.class);
		} else if (filename.endsWith(".xlsx")) {
			xlsxService.importXlsx(file, University.class);
		} else {
			redirectAttributes.addFlashAttribute("message", "Неподдерживаемый формат файла");
			return "redirect:/admin/universitys";
		}
		redirectAttributes.addFlashAttribute("message", "Университеты импортированы");
		return "redirect:/admin/universitys";
	}

}

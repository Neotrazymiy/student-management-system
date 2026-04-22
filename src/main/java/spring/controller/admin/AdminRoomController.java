package spring.controller.admin;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.AllArgsConstructor;
import spring.auxiliaryObjects.RedirectUtil;
import spring.auxiliaryObjects.RequestPageSizeData;
import spring.dto.RoomAddEditDto;
import spring.dto.RoomReadDto;
import spring.service.RoomService;

@Controller
@AllArgsConstructor
@RequestMapping("/admin/rooms")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoomController {

	private final RoomService roomService;

	@GetMapping("")
	public String rooms(@ModelAttribute("newRoom") RoomAddEditDto roomDto,
			@ModelAttribute RequestPageSizeData pageSizeData, Model model) {

		Page<RoomReadDto> pageRoom = roomService
				.getAllPageRooms(PageRequest.of(pageSizeData.getPage(), pageSizeData.getSize()));

		model.addAttribute("pageRoom", pageRoom);
		model.addAttribute("currentPage", pageSizeData.getPage());
		model.addAttribute("pageSize", pageSizeData.getSize());
		model.addAttribute("totalPage", pageRoom.getTotalPages());
		model.addAttribute("basePath", "/admin/rooms");
		model.addAttribute("newRoom", roomDto);

		return "admin/rooms/rooms";
	}

	@PostMapping("/{id}/update")
	public String updateRoom(@PathVariable UUID id, @Valid @ModelAttribute RoomAddEditDto roomDto,
			BindingResult bindingResult, @ModelAttribute RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/admin/rooms";
		}
		roomService.updateRoom(id, roomDto);
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return "redirect:/admin/rooms";
	}

	@PostMapping("/{id}/delete")
	public String deleteRoom(@PathVariable UUID id, Model model, @ModelAttribute RequestPageSizeData pageSizeData,
			RedirectAttributes redirectAttributes) {
		roomService.deleteRoomById(id);
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return "redirect:/admin/rooms";
	}

	@PostMapping("/new")
	public String createRoom(@Valid @ModelAttribute("newRoom") RoomAddEditDto roomDto, BindingResult bindingResult,
			@ModelAttribute RequestPageSizeData pageSizeData, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("newRoom", roomDto);
			redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
			return "redirect:/admin/rooms";
		}
		roomService.addRoom(roomDto);
		RedirectUtil.keepPageSize(redirectAttributes, pageSizeData);
		return "redirect:/admin/rooms";
	}

}

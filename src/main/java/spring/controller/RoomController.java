package spring.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import spring.dto.RoomReadDto;
import spring.service.RoomService;

@Controller
@RequiredArgsConstructor
public class RoomController {

	private final RoomService roomService;

	@GetMapping("/rooms")
	public String room(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			Model model) {
		Page<RoomReadDto> pageRoom = roomService.getAllPageRooms(PageRequest.of(page, size));
		model.addAttribute("pageRoom", pageRoom);
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("totalPage", pageRoom.getTotalPages());
		model.addAttribute("basePath", "/rooms");
		return "rooms";
	}

}

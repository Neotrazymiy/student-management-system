package spring.controller.quest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;
import spring.auxiliaryObjects.RequestPageSizeData;
import spring.dto.RoomReadDto;
import spring.service.RoomService;

@Controller
@AllArgsConstructor
@RequestMapping("/quest")
@PreAuthorize("hasRole('QUEST')")
public class QuestRoomController {

	private final RoomService roomService;

	@GetMapping("/rooms")
	public String room(@ModelAttribute RequestPageSizeData pageSizeData, Model model) {
		Page<RoomReadDto> pageRoom = roomService
				.getAllPageRooms(PageRequest.of(pageSizeData.getPage(), pageSizeData.getSize()));
		model.addAttribute("pageRoom", pageRoom);
		model.addAttribute("currentPage", pageSizeData.getPage());
		model.addAttribute("pageSize", pageSizeData.getSize());
		model.addAttribute("totalPage", pageRoom.getTotalPages());
		model.addAttribute("basePath", "/rooms");
		return "quest/rooms";
	}

}

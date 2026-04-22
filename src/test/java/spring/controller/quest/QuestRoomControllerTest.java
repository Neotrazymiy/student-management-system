package spring.controller.quest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import spring.auxiliaryObjects.CreateObjects;
import spring.config.SecurityConfig;
import spring.dto.RoomReadDto;
import spring.service.CastomUserDetailsService;
import spring.service.RoomService;

@WebMvcTest(controllers = QuestRoomController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class QuestRoomControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RoomService roomService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;

	private CreateObjects createObjects = new CreateObjects();

	@Test
	@WithMockUser(username = "quest", roles = { "QUEST" })
	void getRoomTest() throws Exception {
		List<RoomReadDto> roomReadDtos = new ArrayList<>();
		roomReadDtos.add(createObjects.createRoomDto());

		Page<RoomReadDto> page = new PageImpl<>(roomReadDtos);

		when(roomService.getAllPageRooms(any(PageRequest.class))).thenReturn(page);

		mockMvc.perform(get("/quest/rooms")).andExpect(status().isOk()).andExpect(view().name("quest/rooms"))
				.andExpect(model().attributeExists("pageRoom")).andExpect(model().attribute("currentPage", 0))
				.andExpect(model().attribute("pageSize", 10)).andExpect(model().attribute("totalPage", 1))
				.andExpect(model().attribute("basePath", "/rooms"));

		verify(roomService).getAllPageRooms(any(PageRequest.class));
	}

}

package spring.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
import spring.dto.RoomAddEditDto;
import spring.dto.RoomReadDto;
import spring.exception.DeleteException;
import spring.service.CastomUserDetailsService;
import spring.service.RoomService;

@WebMvcTest(controllers = AdminRoomController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
class AdminRoomControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RoomService roomService;

	@MockBean
	private CastomUserDetailsService castomUserDetailsService;

	private static final String NUMBER = "1_B";
	private static final UUID ROOM_ID = UUID.randomUUID();
	private CreateObjects createObjects = new CreateObjects();

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void getPageRoomTest() throws Exception {
		List<RoomReadDto> roomReadDtos = new ArrayList<>();
		roomReadDtos.add(createObjects.createRoomDto());

		Page<RoomReadDto> page = new PageImpl<>(roomReadDtos);

		when(roomService.getAllPageRooms(any(PageRequest.class))).thenReturn(page);

		mockMvc.perform(get("/admin/rooms")).andExpect(status().isOk()).andExpect(view().name("admin/rooms/rooms"))
				.andExpect(model().attributeExists("pageRoom")).andExpect(model().attribute("currentPage", 0))
				.andExpect(model().attribute("pageSize", 10)).andExpect(model().attribute("totalPage", 1))
				.andExpect(model().attribute("basePath", "/admin/rooms")).andExpect(model().attributeExists("newRoom"));

		verify(roomService).getAllPageRooms(any(PageRequest.class));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateRoomTest() throws Exception {
		mockMvc.perform(post("/admin/rooms/{id}/update", ROOM_ID).with(csrf()).param("page", "1").param("size", "10")
				.param("number", NUMBER)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/rooms?page=1&size=10"));

		ArgumentCaptor<RoomAddEditDto> captor = ArgumentCaptor.forClass(RoomAddEditDto.class);
		verify(roomService).updateRoom(eq(ROOM_ID), captor.capture());

		assertThat(NUMBER).isEqualTo(captor.getValue().getNumber());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void deleteRoomTest() throws Exception {
		mockMvc.perform(post("/admin/rooms/{id}/delete", ROOM_ID).with(csrf()).param("page", "1").param("size", "10"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/admin/rooms?page=1&size=10"));
		verify(roomService).deleteRoomById(ROOM_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void deleteRoom_MyException_Test() throws Exception {
		doThrow(new DeleteException(NUMBER)).when(roomService).deleteRoomById(ROOM_ID);

		mockMvc.perform(post("/admin/rooms/{id}/delete", ROOM_ID).with(csrf())).andExpect(status().isOk())
				.andExpect(view().name("admin/exception")).andExpect(model().attribute("exception", NUMBER));
		verify(roomService).deleteRoomById(ROOM_ID);
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void createRoom() throws Exception {
		RoomAddEditDto roomAddEditDto = new RoomAddEditDto();
		roomAddEditDto.setNumber(NUMBER);

		mockMvc.perform(post("/admin/rooms/new").with(csrf()).param("page", "1").param("size", "10").param("number",
				roomAddEditDto.getNumber())).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/rooms?page=1&size=10"));

		ArgumentCaptor<RoomAddEditDto> captor = ArgumentCaptor.forClass(RoomAddEditDto.class);
		verify(roomService).addRoom(captor.capture());

		assertThat(roomAddEditDto.getNumber()).isEqualTo(captor.getValue().getNumber());
	}

}

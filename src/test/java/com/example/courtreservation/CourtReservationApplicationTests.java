package com.example.courtreservation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest

@AutoConfigureMockMvc
class CourtReservationApplicationTests {
 	@Autowired
    private MockMvc mvc;
	@Test
	void contextLoads() {
	}

	@Test
	public void createCourt() throws Exception{
		ResultActions result = mvc.perform(post("/api/court/create-court-with-surface/{surfaceId}", 1).contentType(MediaType.APPLICATION_JSON).content("{\"description\":\"my court5\"}"));

		result.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().json("{\"id\":5,\"cs\":{\"id\":1,\"costInMinutes\":100},\"description\":\"my court5\"}"));
	}
	@Test
	public void getCourt() throws Exception{
		ResultActions result = mvc.perform(get("/api/court/single-court-view/3"));
		result.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().json("{\"id\":3,\"cs\":{\"id\":2,\"costInMinutes\":200},\"description\":\"east court\"}"));
	}

	@Test
	public void updateCourt() throws Exception {
		ResultActions result = mvc.perform(put("/api/court/update/{surfaceId}", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\":3,\"description\":\"east court updated\"}"));
		result.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.description").value("east court updated"));
	}

	@Test
	public void deleteCourt() throws Exception {
		mvc.perform(get("/api/court/all-courts-list")).andExpect(jsonPath("$", hasSize(4)));
		mvc.perform(delete("/api/court/delete/{courtId}", 4)).andExpect(status().isOk());
		mvc.perform(get("/api/court/all-courts-list")).andExpect(jsonPath("$", hasSize(3)));
	}

	@Test
	public void createReservation() throws Exception {
		ResultActions result = mvc.perform(post("/api/res/create-res/{surfaceId}/{phoneNumber}", 2, "1234")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"desc\":\"myres\", \"startDate\":\"2024-05-25T10:55:56.280082426\", \"endDate\":\"2024-05-25T14:55:56.280082426\", \"kindOfGame\":\"FOUR_PLAYERS\"}"));
		result.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.kindOfGame").value("FOUR_PLAYERS"));
	}

	@Test
	public void updateReservation() throws Exception {
		mvc.perform(post("/api/res/create-res/{surfaceId}/{phoneNumber}", 2, "1234")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"desc\":\"myres\", \"startDate\":\"2024-05-25T10:55:56.280082426\", \"endDate\":\"2024-05-25T14:55:56.280082426\", \"kindOfGame\":\"TWO_PLAYERS\"}"));
		ResultActions result2 = mvc.perform(put("/api/res/update/{courtId}/{phoneNum}", 1, "1234")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\":1,\"desc\":\"myres\", \"startDate\":\"2024-05-25T10:55:56.280082426\", \"endDate\":\"2024-05-25T11:55:56.280082426\", \"kindOfGame\":\"FOUR_PLAYERS\"}"));
		result2.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.kindOfGame").value("FOUR_PLAYERS"))
				.andExpect(jsonPath("$.totalCost").value(9000));
	}

	@Test
	public void deleteReservation() throws Exception {
		mvc.perform(post("/api/res/create-res/{surfaceId}/{phoneNumber}", 2, "1234")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"desc\":\"myres\", \"startDate\":\"2024-05-25T10:55:56.280082426\", \"endDate\":\"2024-05-25T14:55:56.280082426\", \"kindOfGame\":\"FOUR_PLAYERS\"}"));
		mvc.perform(get("/api/res/all-res-list")).andExpect(jsonPath("$", hasSize(1)));
		mvc.perform(delete("/api/res/delete/{resId}", 1)).andExpect(status().isOk());
		mvc.perform(get("/api/res/all-res-list")).andExpect(jsonPath("$", hasSize(0)));
	}
}

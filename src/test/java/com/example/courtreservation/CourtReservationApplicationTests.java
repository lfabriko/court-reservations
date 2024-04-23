package com.example.courtreservation;

import com.example.courtreservation.controller.CourtRestController;
import com.example.courtreservation.entity.CourtSurface;
import com.example.courtreservation.entity.Court;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
//@WebMvcTest(CourtRestController.class)
@AutoConfigureMockMvc
class CourtReservationApplicationTests {
 @Autowired
    private MockMvc mvc;

 /*@MockBean
 CourtRestController courtRestController;*/

	//crud of court
	//crud of reservation
	//private EntityManagerFactory entityManagerFactory;
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
		System.out.println(result.toString());
		result.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(3))
				.andExpect(jsonPath("$.description").value("east court"))
				.andExpect(content().json("{\"id\":3,\"cs\":{\"id\":2,\"costInMinutes\":200},\"description\":\"east court\"}"));
	}

}

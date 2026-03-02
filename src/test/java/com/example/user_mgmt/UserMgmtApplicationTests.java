package com.example.user_mgmt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class UserMgmtApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	// @Test
	// void nonExistentResource() throws Exception {
	// 	mockMvc.perform(get("/nonexistent-endpoint"))
	// 			.andExpect(status().isNotFound())
	// 			.andExpect(jsonPath("$.status").value(404))
	// 			.andExpect(jsonPath("$.error").value("Not Found"));
	// }

	
}

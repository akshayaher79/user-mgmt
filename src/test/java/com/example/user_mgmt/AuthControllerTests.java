package com.example.user_mgmt;

import com.example.user_mgmt.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void signup() throws Exception {
        SignupRequest s = new SignupRequest();
        s.setFirstName("Test");
        s.setLastName("User");
        s.setEmail("test@example.com");
        s.setPassword("Password1!");

        mvc.perform(post("/api/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(s)))
            .andExpect(status().isCreated());

        mvc.perform(post("/api/auth/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(s)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void login() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setEmail("test@example.com");
        login.setPassword("Password1!");

        mvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void badLogin() throws Exception {
        LoginRequest badLogin = new LoginRequest();
        badLogin.setEmail("test@example.com");
        badLogin.setPassword("WrongPassword");

        mvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(badLogin)))
            .andExpect(status().isUnauthorized());
    }
}

package com.example.user_mgmt;

import com.example.user_mgmt.dto.UpdateUserRequest;
import com.example.user_mgmt.entity.User;
import com.example.user_mgmt.service.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void testGetMe() throws Exception {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole("ROLE_USER");

        when(userService.findByEmail("test@example.com")).thenReturn(testUser);

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"));
    }

    @Test
    void testGetMeWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void testGetUserById() throws Exception {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("User");
        testUser.setRole("ROLE_USER");

        when(userService.findById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void testUpdateOwnUser() throws Exception {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setRole("ROLE_USER");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("test@example.co.in");
        updatedUser.setFirstName("Updated");
        updatedUser.setRole("ROLE_USER");

        UpdateUserRequest updateReq = new UpdateUserRequest();
        updateReq.setFirstName("Updated");
        updateReq.setLastName("User");
        updateReq.setEmail("test@example.co.in");

        when(userService.findByEmail("test@example.com")).thenReturn(testUser);
        when(userService.update(1L, updateReq)).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void testUpdateOtherUserWithoutAdminRole() throws Exception {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setRole("ROLE_USER");

        UpdateUserRequest updateReq = new UpdateUserRequest();
        updateReq.setFirstName("Hacked");
        updateReq.setLastName("User");
        updateReq.setEmail("other@example.com");

        when(userService.findByEmail("test@example.com")).thenReturn(testUser);

        mockMvc.perform(put("/api/users/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void testUpdateUserAsAdmin() throws Exception {
        UpdateUserRequest updateReq = new UpdateUserRequest();
        updateReq.setFirstName("Admin-Updated");
        updateReq.setLastName("User");
        updateReq.setEmail("test.admin_updated@example.co.in");

        User updatedUser = new User();
        updatedUser.setId(2L); // User 1 is default admin
        updatedUser.setEmail("test.admin_updated@example.co.in");
        updatedUser.setFirstName("Admin-Updated");
        updatedUser.setRole("ROLE_USER");

        when(userService.update(2L, updateReq)).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Admin-Updated"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/2"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void testDeleteUserWithoutAdminRole() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isForbidden());
    }
}

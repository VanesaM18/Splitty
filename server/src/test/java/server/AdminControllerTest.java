package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Admin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import server.api.AdminController;
import server.services.AdminService;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Admin admin;

    @BeforeEach
    void setUp() {
        admin = new Admin("admin1", "password1", "");
    }

    @Test
    void getAllAdminsShouldReturnAdminList() throws Exception {
        Mockito.when(adminService.getAllAdmins()).thenReturn(Arrays.asList(admin));

        mockMvc.perform(get("/api/admin"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value(admin.getUsername()));
    }

    @Test
    void getAdminByUsernameShouldReturnAdmin() throws Exception {
        Mockito.when(adminService.getAdminByUsername(admin.getUsername())).thenReturn(Optional.of(admin));

        mockMvc.perform(get("/api/admin/{username}", admin.getUsername()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(admin.getUsername()));
    }

    @Test
    void addAdminShouldReturnSavedAdmin() throws Exception {
        Mockito.when(adminService.createAdmin(any(Admin.class))).thenReturn(Optional.of(admin));

        mockMvc.perform(post("/api/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(admin)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(admin.getUsername()));
    }

    @Test
    void deleteAdminShouldReturnSuccessStatus() throws Exception {
        Mockito.when(adminService.deleteAdminByUsername(admin.getUsername())).thenReturn(Optional.of(admin));

        mockMvc.perform(delete("/api/admin/{username}", admin.getUsername()))
            .andExpect(status().isOk())
            .andExpect(content().string("Deleted successfully"));
    }

    @Test
    void getAdminByNonExistentUsernameShouldReturnNotFound() throws Exception {
        Mockito.when(adminService.getAdminByUsername("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/admin/{username}", "nonexistent"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addAdminWithExistingUsernameShouldReturnBadRequest() throws Exception {
        Mockito.when(adminService.createAdmin(any(Admin.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(admin)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deleteNonExistentAdminShouldReturnBadRequest() throws Exception {
        Mockito.when(adminService.deleteAdminByUsername("nonexistent")).thenReturn(Optional.empty());


        mockMvc.perform(delete("/api/admin/{username}", "nonexistent"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addAdminWithMissingFieldsShouldReturnBadRequest() throws Exception {
        Admin incompleteAdmin = new Admin("", "password1", "");

        mockMvc.perform(post("/api/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incompleteAdmin)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void loginWithValidCredentialsShouldReturnSuccess() throws Exception {
        Mockito.when(adminService.authenticateAdmin(admin)).thenReturn(true);

        mockMvc.perform(post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(admin)))
            .andExpect(status().isOk())
            .andExpect(content().string("Login successfully"));
    }
}

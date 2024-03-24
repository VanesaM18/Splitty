package server.services;

import commons.Admin;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.database.AdminRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminServiceTest {
    @Mock
    AdminRepository adminRepository;
    @InjectMocks
    AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAdmins() {
        Admin admin = new Admin("username", "password", null);
        when(adminRepository.findAll()).thenReturn(List.of(admin));

        List<Admin> result = adminService.getAllAdmins();
        Assertions.assertEquals(List.of(admin), result);
    }

    @Test
    void testGetAdminByUsername() {
        Admin admin = new Admin("username", "password", null);
        when(adminRepository.findById(any())).thenReturn(Optional.of(admin));
        when(adminRepository.existsById(admin.getUsername())).thenReturn(true);

        Optional<Admin> result = adminService.getAdminByUsername("username");
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(admin, result.get());
    }

    @Test
    void testCreateAdmin() {
        Admin admin = new Admin("username", "password", null);
        when(adminRepository.save(any())).thenReturn(admin);
        when(adminRepository.existsById(any())).thenReturn(false);

        Optional<Admin> result = adminService.createAdmin(admin);
        Assertions.assertEquals(Optional.of(admin), result);
    }

    @Test
    void testDeleteAdminByUsername() {
        Admin admin = new Admin("username", "password", null);
        when(adminRepository.findById(any())).thenReturn(Optional.of(admin));
        when(adminRepository.existsById(any())).thenReturn(true);

        Optional<Admin> result = adminService.deleteAdminByUsername("username");
        verify(adminRepository).deleteById(any());
        Assertions.assertEquals(Optional.of(admin), result);
    }

    @Test
    void testAuthenticateAdmin() {
        Admin admin = new Admin("username", "password", null);
        when(adminRepository.findById(any())).thenReturn(Optional.of(admin));
        when(adminRepository.existsById(any())).thenReturn(true);

        boolean result = adminService.authenticateAdmin(admin);
        Assertions.assertTrue(result);
    }
}

package server.services;

import org.springframework.stereotype.Service;
import server.database.AdminRepository;

import java.util.Optional;

@Service
public class AdminService {
    private final AdminRepository adminRepository;

    /**
     * constructs a new AdminService with the provided AdminRepository
     * @param adminRepository repository used for admin-related operations
     */
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    /**
     * gets a list of all admins
     * @return list of all admins
     */
    public java.util.List<commons.Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    /**
     * gets an admin by their username
     * @param username username of the admin
     * @return an Optional containing the admin if found, or empty if not found
     */
    public Optional<commons.Admin> getAdminByUsername(String username) {
        if (username.isEmpty() || !adminRepository.existsById(username)){
            return Optional.empty();
        }
        return adminRepository.findById(username);
    }

    /**
     * creates a new admin
     * @param admin admin to be created
     * @return an Optional containing the created admin if successful, or empty if not
     */
    public Optional<commons.Admin> createAdmin(commons.Admin admin) {
        if (admin.getUsername() == null || admin.getUsername().isEmpty()
                || adminRepository.existsById(admin.getUsername())) {
            return Optional.empty();
        }
        return Optional.of(adminRepository.save(admin));
    }

    /**
     * deletes an admin by their username
     * @param username username of the admin to be deleted
     * @return an Optional containing the deleted admin if successful, or empty if not found
     */
    public Optional<commons.Admin> deleteAdminByUsername(String username) {
        if (isNullOrEmpty(username) || !adminRepository.existsById(username)) {
            return Optional.empty();
        }
        var admin = adminRepository.findById(username);
        adminRepository.deleteById(username);
        return admin;
    }

    /**
     *  Checks if a string is null or empty
     */
    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * checks if an admin is authenticated
     * @param admin admin to authenticate
     * @return true if admin is authenticated, false otherwise.
     */
    public boolean authenticateAdmin(commons.Admin admin) {
        if (admin.getUsername().isEmpty() || !adminRepository.existsById(admin.getUsername())) {
            return false;
        }
        commons.Admin exist = adminRepository.findById(admin.getUsername()).orElse(null);
        return exist != null && exist.getPassword().equals(admin.getPassword());
    }
}

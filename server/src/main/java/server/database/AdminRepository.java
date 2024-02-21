package server.database;

import org.springframework.data.jpa.repository.JpaRepository;

import commons.Admin;

public interface AdminRepository extends JpaRepository<Admin, String> {}
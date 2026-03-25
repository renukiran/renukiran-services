package com.renukiran.dto;

public class UserManagementResponse {
    
    private String name;
    private String email;
    private String status;
    private String role; // Added this to distinguish between Trainer and Staff in the list

    // ====== GETTERS ======
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public String getRole() { return role; }

    // ====== SETTERS ======
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setStatus(String status) { this.status = status; }
    public void setRole(String role) { this.role = role; }
}
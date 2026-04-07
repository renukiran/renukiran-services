package com.renukiran.dto;

import java.util.Set;

public class UserManagementResponse {
    
    private String name;
    private String email;
    private String status;
    private Set<String> roles;

    // ====== GETTERS ======
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public Set<String> getRoles() { return roles; }

    // ====== SETTERS ======
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setStatus(String status) { this.status = status; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
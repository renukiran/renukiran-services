package com.renukiran.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserManagementResponse {
    private String userId;
    private String name;
    private String email;
    private String status;
    private String role;
   // private boolean deleted;
}
package com.renukiran.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renukiran.dto.CourseResponse;
import com.renukiran.dto.UserManagementResponse;
import com.renukiran.service.UserManagementService;

@RestController
@RequestMapping("/allUsers")
public class UserManagementController {
    @Autowired
    private UserManagementService userManagementService;

    @GetMapping
    public List<UserManagementResponse> getAllUsers() {
        return userManagementService.getAllUsers();
    }

}

package com.renukiran.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.renukiran.dto.CourseResponse;
import com.renukiran.dto.UserManagementResponse;
import com.renukiran.service.UserManagementService;

@RestController
@RequestMapping("users")
public class UserManagementController {
    @Autowired
    private UserManagementService userManagementService;

    @GetMapping("/allUsers")
    public List<UserManagementResponse> getAllUsers() {
        return userManagementService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, @RequestParam Long deletedBy){
        userManagementService.deleteUser(id,deletedBy);
        return ResponseEntity.noContent().build();
    }
}

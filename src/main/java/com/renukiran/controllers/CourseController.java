package com.renukiran.controllers;

import com.renukiran.dto.CreateCourseRequest;
import com.renukiran.dto.CourseResponse;
import com.renukiran.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // ADMIN ONLY
    @PostMapping("/add")
    public CourseResponse addCourse(@Valid @RequestBody CreateCourseRequest request) {
        return courseService.addCourse(request);
    }

    // PUBLIC / ALL USERS
    @GetMapping
    public List<CourseResponse> getAllCourses() {
        return courseService.getAllCourses();
    }

    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "Course deleted successfully";
    }

    @PutMapping("/{id}")
    public CourseResponse updateCourse(@PathVariable Long id,
                                    @RequestBody CreateCourseRequest request) {
        return courseService.updateCourse(id, request);
    }
}
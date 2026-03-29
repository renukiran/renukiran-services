package com.renukiran.controllers;

import com.renukiran.dto.CreateCourseRequest;
import com.renukiran.dto.CourseResponse;
import com.renukiran.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // ADMIN ONLY — supports both POST /courses and POST /courses/add
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.addCourse(request));
    }

    @PostMapping("/add")
    public CourseResponse addCourse(@Valid @RequestBody CreateCourseRequest request) {
        return courseService.addCourse(request);
    }

    // PUBLIC / ALL USERS
    @GetMapping
    public List<CourseResponse> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public CourseResponse updateCourse(@PathVariable Long id,
                                    @RequestBody CreateCourseRequest request) {
        return courseService.updateCourse(id, request);
    }
}
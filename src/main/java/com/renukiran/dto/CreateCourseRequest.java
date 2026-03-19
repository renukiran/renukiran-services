package com.renukiran.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateCourseRequest {

    @NotBlank(message = "Course name is required")
    private String courseName;

    private String instructor;

    private int duration;

    // ====== GETTERS ======

    public String getCourseName() {
        return courseName;
    }

    public String getInstructor() {
        return instructor;
    }

    public int getDuration() {
        return duration;
    }

    // ====== SETTERS ======

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
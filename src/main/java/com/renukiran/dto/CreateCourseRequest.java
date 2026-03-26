package com.renukiran.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateCourseRequest {

    @NotBlank(message = "Course name is required")
    private String courseName;

    private String instructor;

    private Integer duration;

    private Integer maxBatchSize;

    private String category;

    private String description;

    private String status;

    private String mcqAssessment;

    private String practicalAssessment;

    private String caseStudyAssessment;

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

    public Integer getMaxBatchSize() {
        return maxBatchSize;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getMcqAssessment() {
        return mcqAssessment;
    }

    public String getPracticalAssessment() {
        return practicalAssessment;
    }

    public String getCaseStudyAssessment() {
        return caseStudyAssessment;
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

    public void setMaxBatchSize(Integer maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMcqAssessment(String mcqAssessment) {
        this.mcqAssessment = mcqAssessment;
    }

    public void setPracticalAssessment(String practicalAssessment) {
        this.practicalAssessment = practicalAssessment;
    }

    public void setCaseStudyAssessment(String caseStudyAssessment) {
        this.caseStudyAssessment = caseStudyAssessment;
    }
}
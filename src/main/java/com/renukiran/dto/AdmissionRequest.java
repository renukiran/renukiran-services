package com.renukiran.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
@Data
public class AdmissionRequest {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Gender is mandatory")
    @Pattern(regexp = "Male|Female", message = "Gender must be either Male or Female")
    private String gender;

    @NotNull(message = "Date of Birth is mandatory")
    @Past(message = "Date of Birth must be in the past")
    private LocalDate dob;

    @NotBlank(message = "Guardian name is mandatory")
    private String guardianName;

    private String qualification;

    @NotBlank(message = "Occupation is mandatory")
    private String occupation;

    @NotBlank(message = "Nationality is mandatory")
    private String nationality;

    private String religion;

    private String skills;

    @NotBlank(message = "Category is mandatory")
    private String category;

    @NotBlank(message = "Mobile number is mandatory")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Mobile number must be a valid 10-digit Indian number")
    private String mobile;

    @NotBlank(message = "Address is mandatory")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    private String referenceName;

    @NotNull(message = "Course ID is mandatory")
    private Long courseId;

    @NotBlank(message = "Batch number is mandatory")
    private String batchNo;

    @NotBlank(message = "Batch Timing is mandatory")
    private String timing;
}


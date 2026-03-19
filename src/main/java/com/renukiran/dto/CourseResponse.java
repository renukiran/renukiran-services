package com.renukiran.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CourseResponse {

    private Long id;
    private String courseName;
    private String instructor;
    private int duration;
}
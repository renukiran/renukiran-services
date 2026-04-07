package com.renukiran.service;

import com.renukiran.dto.CreateCourseRequest;
import com.renukiran.dto.CourseResponse;
import com.renukiran.entity.Course;
import com.renukiran.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public CourseResponse addCourse(CreateCourseRequest request) {

        Course course = Course.builder()
                .courseName(request.getCourseName())
                .instructor(request.getInstructor())
                .durationMonths(request.getDuration())
                .maxBatchSize(request.getMaxBatchSize())
                .category(request.getCategory())
                .description(request.getDescription())
                .status(request.getStatus())
                .mcqAssessment(request.getMcqAssessment())
                .practicalAssessment(request.getPracticalAssessment())
                .caseStudyAssessment(request.getCaseStudyAssessment())
                .passThreshold(request.getPassThreshold())
                .retentionRate(request.getRetentionRate())
                .build();

        Course saved = courseRepository.save(course);

        return CourseResponse.builder()
                .id(saved.getCourseId())
                .courseName(request.getCourseName())
                .instructor(request.getInstructor())
                .duration(request.getDuration())
                .maxBatchSize(request.getMaxBatchSize())
                .category(request.getCategory())
                .description(request.getDescription())
                .status(request.getStatus())
                .mcqAssessment(request.getMcqAssessment())
                .practicalAssessment(request.getPracticalAssessment())
                .caseStudyAssessment(request.getCaseStudyAssessment())
                .passThreshold(request.getPassThreshold())
                .retentionRate(request.getRetentionRate())
                .build();
    }

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(c -> CourseResponse.builder()
                        .id(c.getCourseId())
                        .courseName(c.getCourseName())
                        .instructor(c.getInstructor())
                        .duration(c.getDurationMonths())
                        .maxBatchSize(c.getMaxBatchSize())
                        .category(c.getCategory())
                        .description(c.getDescription())
                        .status(c.getStatus())
                        .mcqAssessment(c.getMcqAssessment())
                        .practicalAssessment(c.getPracticalAssessment())
                        .caseStudyAssessment(c.getCaseStudyAssessment())
                        .passThreshold(c.getPassThreshold())
                        .retentionRate(c.getRetentionRate())
                        .build())
                .collect(Collectors.toList());
    }

    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        return CourseResponse.builder()
                .id(course.getCourseId())
                .courseName(course.getCourseName())
                .instructor(course.getInstructor())
                .duration(course.getDurationMonths())
                .build();
    }

    public void deleteCourse(Long id) {

        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Course not found");
        }

        courseRepository.deleteById(id);
    }

    public CourseResponse updateCourse(Long id, CreateCourseRequest request) {

        // 1. Fetch existing course
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // 2. Update all fields from the request DTO to the Entity
        course.setCourseName(request.getCourseName());
        course.setInstructor(request.getInstructor());
        course.setDurationMonths(request.getDuration());
        course.setMaxBatchSize(request.getMaxBatchSize());
        course.setCategory(request.getCategory());
        course.setDescription(request.getDescription());
        course.setStatus(request.getStatus());
        course.setMcqAssessment(request.getMcqAssessment());
        course.setPracticalAssessment(request.getPracticalAssessment());
        course.setCaseStudyAssessment(request.getCaseStudyAssessment());
        course.setPassThreshold(request.getPassThreshold());
        course.setRetentionRate(request.getRetentionRate());

        // 3. Persist changes
        Course updated = courseRepository.save(course);

        // 4. Return the updated response
        return CourseResponse.builder()
                .id(updated.getCourseId())
                .courseName(updated.getCourseName())
                .instructor(updated.getInstructor())
                .duration(updated.getDurationMonths()) // Matches your Entity field
                .maxBatchSize(updated.getMaxBatchSize())
                .category(updated.getCategory())
                .description(updated.getDescription())
                .status(updated.getStatus())
                .mcqAssessment(updated.getMcqAssessment())
                .practicalAssessment(updated.getPracticalAssessment())
                .caseStudyAssessment(updated.getCaseStudyAssessment())
                .passThreshold(updated.getPassThreshold())
                .retentionRate(updated.getRetentionRate())
                .build();
    }
}

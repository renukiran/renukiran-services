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
                .build();

        Course saved = courseRepository.save(course);

        return CourseResponse.builder()
                .id(saved.getCourseId())
                .courseName(saved.getCourseName())
                .instructor(saved.getInstructor())
                .duration(saved.getDurationMonths())
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

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setCourseName(request.getCourseName());
        course.setInstructor(request.getInstructor());
        course.setDurationMonths(request.getDuration());

        Course updated = courseRepository.save(course);

        return CourseResponse.builder()
                .id(updated.getCourseId())
                .courseName(updated.getCourseName())
                .instructor(updated.getInstructor())
                .duration(updated.getDurationMonths())
                .build();
    }
}
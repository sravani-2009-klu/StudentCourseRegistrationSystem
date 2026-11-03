package com.registration.service;

import com.registration.collection.RegistrationRepository;
import com.registration.model.Course;
import com.registration.model.Faculty;
import java.util.List;

public class CourseService {
    private final RegistrationRepository repository;

    public CourseService(RegistrationRepository repository) {
        this.repository = repository;
    }

    public void addCourse(Course course) {
        if (course.getFacultyId() != null && !course.getFacultyId().isBlank()
                && repository.findFaculty(course.getFacultyId()) == null)
            throw new IllegalArgumentException("Faculty ID does not exist.");
        repository.addCourse(course);
    }

    public void addFaculty(Faculty faculty) { repository.addFaculty(faculty); }
    public Faculty findFaculty(String id) { return repository.findFaculty(id); }
    public Course findCourse(String id) { return repository.findCourse(id); }
    public List<Course> getAllCourses() { return repository.getCourses(); }
    public List<Faculty> getAllFaculties() { return repository.getFaculties(); }
}
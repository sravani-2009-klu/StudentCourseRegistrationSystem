package com.registration.service;

import com.registration.collection.RegistrationRepository;
import com.registration.model.Course;
import com.registration.model.Student;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class SearchSortService {
    private final RegistrationRepository repository;

    public SearchSortService(RegistrationRepository repository) {
        this.repository = repository;
    }

    public List<Student> searchStudents(String keyword) {
        String q = keyword.toLowerCase(Locale.ROOT);
        List<Student> results = new ArrayList<>();
        for (Student s : repository.getStudents()) {
            if (s.getId().toLowerCase(Locale.ROOT).contains(q)
                    || s.getName().toLowerCase(Locale.ROOT).contains(q)) {
                results.add(s);
            }
        }
        results.sort(Comparator.naturalOrder());
        return results;
    }

    public List<Course> searchCourses(String keyword) {
        String q = keyword.toLowerCase(Locale.ROOT);
        List<Course> results = new ArrayList<>();
        for (Course c : repository.getCourses()) {
            if (c.getId().toLowerCase(Locale.ROOT).contains(q)
                    || c.getName().toLowerCase(Locale.ROOT).contains(q)) {
                results.add(c);
            }
        }
        results.sort(Comparator.comparing(Course::getName,
                String.CASE_INSENSITIVE_ORDER));
        return results;
    }
}
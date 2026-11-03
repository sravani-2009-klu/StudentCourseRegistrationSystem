package com.registration.service;

import com.registration.collection.RegistrationRepository;
import com.registration.model.Registration;
import java.util.List;

public class RegistrationService {
    private final RegistrationRepository repository;

    public RegistrationService(RegistrationRepository repository) {
        this.repository = repository;
    }

    public Registration registerStudent(String studentId, String courseId) {
        return repository.register(studentId, courseId);
    }

    public List<Registration> getAllRegistrations() {
        return repository.getRegistrations();
    }
}
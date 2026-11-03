package com.registration.concurrency;

import com.registration.model.Registration;

import com.registration.service.RegistrationService;

import java.util.concurrent.Callable;

public class RegistrationTask implements Callable<Registration> {
    private final RegistrationService service;
    private final String studentId;
    private final String courseId;

    public RegistrationTask(RegistrationService service, String studentId, String courseId) {
        this.service = service;
        this.studentId = studentId;
        this.courseId = courseId;
    }

    @Override
    public Registration call() {
        return service.registerStudent(studentId, courseId);
    }
}
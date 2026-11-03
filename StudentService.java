package com.registration.service;

import com.registration.collection.RegistrationRepository;
import com.registration.model.Student;
import java.util.List;

public class StudentService {
    private final RegistrationRepository repository;

    public StudentService(RegistrationRepository repository) {
        this.repository = repository;
    }

    public void addStudent(Student student) { repository.addStudent(student); }
    public Student findStudent(String id) { return repository.findStudent(id); }
    public List<Student> getAllStudents() { return repository.getStudents(); }
}
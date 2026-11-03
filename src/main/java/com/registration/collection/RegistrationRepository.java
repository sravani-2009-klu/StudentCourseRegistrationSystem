package com.registration.collection;

import com.registration.model.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RegistrationRepository {
    private final Map<String, Student> students = new ConcurrentHashMap<>();
    private final Map<String, Course> courses = new ConcurrentHashMap<>();
    private final Map<String, Faculty> faculties = new ConcurrentHashMap<>();
    private final Map<String, Registration> registrations = new ConcurrentHashMap<>();
    private final Object registrationLock = new Object();

    public void addStudent(Student student) {
        requireId(student == null ? null : student.getId(), "Student");
        if (students.putIfAbsent(student.getId(), student) != null)
            throw new IllegalArgumentException("Student ID already exists.");
    }

    public void addCourse(Course course) {
        requireId(course == null ? null : course.getId(), "Course");
        if (course.getCapacity() < 1) throw new IllegalArgumentException("Capacity must be at least 1.");
        if (courses.putIfAbsent(course.getId(), course) != null)
            throw new IllegalArgumentException("Course ID already exists.");
    }

    public void addFaculty(Faculty faculty) {
        requireId(faculty == null ? null : faculty.getId(), "Faculty");
        if (faculties.putIfAbsent(faculty.getId(), faculty) != null)
            throw new IllegalArgumentException("Faculty ID already exists.");
    }

    private void requireId(String id, String type) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException(type + " ID cannot be empty.");
    }

    public Student findStudent(String id) { return students.get(id); }
    public Course findCourse(String id) { return courses.get(id); }
    public Faculty findFaculty(String id) { return faculties.get(id); }

    public List<Student> getStudents() {
        List<Student> list = new ArrayList<>(students.values());
        Collections.sort(list);
        return list;
    }

    public List<Course> getCourses() {
        List<Course> list = new ArrayList<>(courses.values());
        Collections.sort(list);
        return list;
    }

    public List<Faculty> getFaculties() {
        List<Faculty> list = new ArrayList<>(faculties.values());
        list.sort(Comparator.comparing(Faculty::getName, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    public List<Registration> getRegistrations() {
        List<Registration> list = new ArrayList<>(registrations.values());
        list.sort(Comparator.comparing(Registration::getRegistrationDate,
                Comparator.nullsLast(String::compareTo)));
        return list;
    }

    public Registration register(String studentId, String courseId) {
        synchronized (registrationLock) {
            if (!students.containsKey(studentId))
                throw new IllegalArgumentException("Student not found.");
            Course course = courses.get(courseId);
            if (course == null) throw new IllegalArgumentException("Course not found.");

            for (Registration r : registrations.values()) {
                if (studentId.equals(r.getStudentId()) && courseId.equals(r.getCourseId())
                        && "REGISTERED".equals(r.getStatus())) {
                    throw new IllegalArgumentException("Student is already registered for this course.");
                }
            }

            long occupied = registrations.values().stream()
                    .filter(r -> courseId.equals(r.getCourseId()))
                    .filter(r -> "REGISTERED".equals(r.getStatus()))
                    .count();

            if (occupied >= course.getCapacity())
                throw new IllegalStateException("Course is full.");

            Registration r = new Registration(UUID.randomUUID().toString(), studentId, courseId);
            registrations.put(r.getId(), r);
            return r;
        }
    }

    public void replaceAll(List<Student> newStudents, List<Course> newCourses,
                           List<Faculty> newFaculties, List<Registration> newRegistrations) {
        synchronized (registrationLock) {
            Map<String, Student> s = new ConcurrentHashMap<>();
            Map<String, Course> c = new ConcurrentHashMap<>();
            Map<String, Faculty> f = new ConcurrentHashMap<>();
            Map<String, Registration> r = new ConcurrentHashMap<>();

            for (Student item : newStudents) {
                if (item == null || item.getId() == null || item.getId().isBlank()
                        || s.putIfAbsent(item.getId(), item) != null)
                    throw new IllegalArgumentException("Invalid or duplicate student in JSON.");
            }
            for (Course item : newCourses) {
                if (item == null || item.getId() == null || item.getId().isBlank()
                        || item.getCapacity() < 1 || c.putIfAbsent(item.getId(), item) != null)
                    throw new IllegalArgumentException("Invalid or duplicate course in JSON.");
            }
            for (Faculty item : newFaculties) {
                if (item == null || item.getId() == null || item.getId().isBlank()
                        || f.putIfAbsent(item.getId(), item) != null)
                    throw new IllegalArgumentException("Invalid or duplicate faculty in JSON.");
            }
            for (Registration item : newRegistrations) {
                if (item == null || item.getId() == null || item.getId().isBlank()
                        || !s.containsKey(item.getStudentId()) || !c.containsKey(item.getCourseId())
                        || r.putIfAbsent(item.getId(), item) != null)
                    throw new IllegalArgumentException("Invalid registration in JSON.");
            }
            for (Course item : c.values()) {
                long occupied = r.values().stream()
                        .filter(x -> item.getId().equals(x.getCourseId()))
                        .filter(x -> "REGISTERED".equals(x.getStatus())).count();
                if (occupied > item.getCapacity())
                    throw new IllegalArgumentException("Saved registrations exceed course capacity.");
            }

            students.clear(); students.putAll(s);
            courses.clear(); courses.putAll(c);
            faculties.clear(); faculties.putAll(f);
            registrations.clear(); registrations.putAll(r);
        }
    }
}
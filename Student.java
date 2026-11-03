package com.registration.model;

import java.util.Objects;

public class Student implements Comparable<Student> {

    private String id;
    private String name;
    private String email;
    private String department;
    private int year;

    public Student() {
    }

    public Student(String id, String name, String email, String department, int year) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public int compareTo(Student other) {
        return String.CASE_INSENSITIVE_ORDER.compare(name, other.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof Student))
            return false;

        Student other = (Student) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Student{ID='" + id + "', name='" + name
                + "', email='" + email + "', department='"
                + department + "', year=" + year + "}";
    }
}
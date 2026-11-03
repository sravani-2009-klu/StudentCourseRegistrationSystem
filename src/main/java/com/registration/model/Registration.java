package com.registration.model;

import java.time.LocalDate;

import java.util.Objects;

public class Registration {

    private String id;
    private String studentId;
    private String courseId;
    private String registrationDate;
    private String status;

    public Registration() {
    }

    public Registration(String id, String studentId, String courseId) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.registrationDate = LocalDate.now().toString();
        this.status = "REGISTERED";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Registration)) {
            return false;
        }

        Registration other = (Registration) obj;

        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Registration{ID='" + id
                + "', studentId='" + studentId
                + "', courseId='" + courseId
                + "', date='" + registrationDate
                + "', status='" + status + "'}";
    }
}
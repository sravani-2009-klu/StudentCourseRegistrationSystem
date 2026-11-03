package com.registration.model;

import java.util.Objects;

public class Faculty {

    private String id;
    private String name;
    private String email;
    private String designation;
    private String department;

    public Faculty() {
    }

    public Faculty(String id, String name, String email,
                    String designation, String department) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.designation = designation;
        this.department = department;
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

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof Faculty))
            return false;

        Faculty other = (Faculty) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Faculty{ID='" + id + "', name='" + name
                + "', email='" + email + "', designation='"
                + designation + "', department='" + department + "'}";
    }
}
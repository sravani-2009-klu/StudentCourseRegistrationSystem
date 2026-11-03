package com.registration.persistance;

import com.fasterxml.jackson.databind.ObjectMapper;


import com.fasterxml.jackson.databind.SerializationFeature;
import com.registration.collection.RegistrationRepository;
import com.registration.model.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class JsonDataStore {
    private final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);
    private final Path file;

    public JsonDataStore(String fileName) {
        this.file = Paths.get(fileName).toAbsolutePath();
    }

    public static class Data {
        public List<Student> students = new ArrayList<>();
        public List<Course> courses = new ArrayList<>();
        public List<Faculty> faculties = new ArrayList<>();
        public List<Registration> registrations = new ArrayList<>();
        public Data() {}
    }

    public void save(RegistrationRepository repository) throws IOException {
        Data data = new Data();
        data.students = repository.getStudents();
        data.courses = repository.getCourses();
        data.faculties = repository.getFaculties();
        data.registrations = repository.getRegistrations();

        Path parent = file.getParent();
        if (parent != null) Files.createDirectories(parent);

        Path temp = file.resolveSibling(file.getFileName() + ".tmp");
        mapper.writeValue(temp.toFile(), data);
        try {
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException ex) {
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public boolean load(RegistrationRepository repository) throws IOException {
        if (!Files.exists(file)) return false;
        Data data = mapper.readValue(file.toFile(), Data.class);
        if (data.students == null) data.students = new ArrayList<>();
        if (data.courses == null) data.courses = new ArrayList<>();
        if (data.faculties == null) data.faculties = new ArrayList<>();
        if (data.registrations == null) data.registrations = new ArrayList<>();

        repository.replaceAll(data.students, data.courses,
                data.faculties, data.registrations);
        return true;
    }
}
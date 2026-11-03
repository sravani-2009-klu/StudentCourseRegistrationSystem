package com.registration.app;

import com.registration.collection.RegistrationRepository;

import com.registration.concurrency.RegistrationTask;
import com.registration.model.*;
import com.registration.persistance.JsonDataStore;
import com.registration.service.*;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        RegistrationRepository repository = new RegistrationRepository();
        StudentService studentService = new StudentService(repository);
        CourseService courseService = new CourseService(repository);
        RegistrationService registrationService = new RegistrationService(repository);
        SearchSortService searchService = new SearchSortService(repository);
        JsonDataStore dataStore = new JsonDataStore("data/registrations.json");
        ExecutorService executor = Executors.newFixedThreadPool(4);

        try {
            if (dataStore.load(repository)) System.out.println("Previous data loaded.");
            else System.out.println("No saved data found. Starting fresh.");

            boolean running = true;
            while (running) {
                menu();
                int choice = readInt("Choose an option: ");
                try {
                    switch (choice) {
                        case 1:
                            addStudent(studentService, dataStore, repository);
                            break;
                        case 2:
                            addFaculty(courseService, dataStore, repository);
                            break;
                        case 3:
                            addCourse(courseService, dataStore, repository);
                            break;
                        case 4:
                            printList(studentService.getAllStudents());
                            break;
                        case 5:
                            printList(courseService.getAllCourses());
                            break;
                        case 6:
                            searchStudents(searchService);
                            break;
                        case 7:
                            searchCourses(searchService);
                            break;
                        case 8:
                            registerStudent(registrationService, executor, dataStore, repository);
                            break;
                        case 9:
                            printList(registrationService.getAllRegistrations());
                            break;
                        case 10:
                            save(dataStore, repository);
                            break;
                        case 11:
                            save(dataStore, repository);
                            running = false;
                            break;
                        default:
                            System.out.println("Invalid menu choice.");
                    }
                } catch (IllegalArgumentException | IllegalStateException ex) {
                    System.out.println("Error: " + ex.getMessage());
                } catch (IOException ex) {
                    System.out.println("File error: " + ex.getMessage());
                }
            }
        } catch (IOException ex) {
            System.out.println("Could not load saved data: " + ex.getMessage());
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) executor.shutdownNow();
            } catch (InterruptedException ex) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            scanner.close();
        }
        System.out.println("Application closed.");
    }

    private static void menu() {
        System.out.println("\n===== STUDENT COURSE REGISTRATION SYSTEM =====");
        System.out.println("1. Add Student");
        System.out.println("2. Add Faculty");
        System.out.println("3. Add Course");
        System.out.println("4. Display Students (sorted)");
        System.out.println("5. Display Courses (sorted)");
        System.out.println("6. Search Student");
        System.out.println("7. Search Course");
        System.out.println("8. Register Student for Course (concurrent task)");
        System.out.println("9. Display Registrations");
        System.out.println("10. Save Data");
        System.out.println("11. Save and Exit");
    }

    private static String readText(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            try { return Integer.parseInt(readText(prompt)); }
            catch (NumberFormatException ex) { System.out.println("Please enter a whole number."); }
        }
    }

    private static void addStudent(StudentService service, JsonDataStore store,
                                   RegistrationRepository repository) throws IOException {
        String id = readText("Student ID: ");
        String name = readText("Student name: ");
        String email = readText("Email: ");
        String department = readText("Department: ");
        int year = readInt("Year (1-6): ");
        if (name.isBlank() || email.isBlank() || department.isBlank() || year < 1 || year > 6)
            throw new IllegalArgumentException("Enter valid student details and year 1-6.");
        service.addStudent(new Student(id, name, email, department, year));
        store.save(repository);
        System.out.println("Student added and saved.");
    }

    private static void addFaculty(CourseService service, JsonDataStore store,
                                   RegistrationRepository repository) throws IOException {
        String id = readText("Faculty ID: ");
        String name = readText("Faculty name: ");
        String email = readText("Email: ");
        String designation = readText("Designation: ");
        String department = readText("Department: ");
        if (name.isBlank() || email.isBlank() || designation.isBlank() || department.isBlank())
            throw new IllegalArgumentException("Faculty fields cannot be empty.");
        service.addFaculty(new Faculty(id, name, email, designation, department));
        store.save(repository);
        System.out.println("Faculty added and saved.");
    }

    private static void addCourse(CourseService service, JsonDataStore store,
                                  RegistrationRepository repository) throws IOException {
        String id = readText("Course ID: ");
        String name = readText("Course name: ");
        int capacity = readInt("Seat capacity: ");
        String facultyId = readText("Faculty ID (leave blank if unassigned): ");
        if (name.isBlank() || capacity < 1)
            throw new IllegalArgumentException("Course name and positive capacity are required.");
        service.addCourse(new Course(id, name, capacity, facultyId.isBlank() ? null : facultyId));
        store.save(repository);
        System.out.println("Course added and saved.");
    }

    private static void searchStudents(SearchSortService service) {
        String keyword = readText("Student ID or name to search: ");
        List<Student> results = service.searchStudents(keyword);
        if (results.isEmpty()) System.out.println("No matching students.");
        else printList(results);
    }

    private static void searchCourses(SearchSortService service) {
        String keyword = readText("Course ID or name to search: ");
        List<Course> results = service.searchCourses(keyword);
        if (results.isEmpty()) System.out.println("No matching courses.");
        else printList(results);
    }

    private static void registerStudent(RegistrationService service, ExecutorService executor,
                                        JsonDataStore store, RegistrationRepository repository)
            throws IOException {
        String studentId = readText("Student ID: ");
        String courseId = readText("Course ID: ");
        Future<Registration> future = executor.submit(
                new RegistrationTask(service, studentId, courseId));
        try {
            Registration registration = future.get();
            store.save(repository);
            System.out.println("Registration successful:");
            System.out.println(registration);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Registration was interrupted.");
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof IllegalArgumentException)
                throw new IllegalArgumentException(cause.getMessage());
            if (cause instanceof IllegalStateException)
                throw new IllegalStateException(cause.getMessage());
            throw new IllegalStateException("Registration failed: " + cause.getMessage());
        }
    }

    private static void save(JsonDataStore store, RegistrationRepository repository)
            throws IOException {
        store.save(repository);
        System.out.println("Data saved successfully.");
    }

    private static void printList(List<?> items) {
        if (items.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        items.forEach(System.out::println);
    }
}
package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Course;
import model.Registration;
import model.Student;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Controller: handles UI actions and maintains in-memory lists
 */
public class MainController {

    // Input fields
    @FXML private TextField studentNameField;
    @FXML private TextField courseNameField;

    // Buttons
    @FXML private Button addStudentBtn;
    @FXML private Button addCourseBtn;
    @FXML private Button registerBtn;
    @FXML private Button viewDataBtn;
    @FXML private Button deleteStudentBtn;
    @FXML private Button deleteCourseBtn;

    // ComboBoxes for registration
    @FXML private ComboBox<Student> studentCombo;
    @FXML private ComboBox<Course> courseCombo;

    // TableViews
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, Integer> studentIdCol;
    @FXML private TableColumn<Student, String> studentNameCol;

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, Integer> courseIdCol;
    @FXML private TableColumn<Course, String> courseNameCol;

    @FXML private TableView<Registration> registrationTable;
    @FXML private TableColumn<Registration, Integer> regIdCol;
    @FXML private TableColumn<Registration, String> regStudentCol;
    @FXML private TableColumn<Registration, String> regCourseCol;

    // In-memory storage (as requested)
    private final ArrayList<Student> students = new ArrayList<>();
    private final ArrayList<Course> courses = new ArrayList<>();
    private final ArrayList<Registration> registrations = new ArrayList<>();

    // Observable lists for UI
    private final ObservableList<Student> studentObs = FXCollections.observableArrayList();
    private final ObservableList<Course> courseObs = FXCollections.observableArrayList();
    private final ObservableList<Registration> regObs = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Setup table columns
        studentIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        courseIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        courseNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        regIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        regStudentCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        regCourseCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        // Bind tables to observable lists
        studentTable.setItems(studentObs);
        courseTable.setItems(courseObs);
        registrationTable.setItems(regObs);

        // Bind comboboxes
        studentCombo.setItems(studentObs);
        courseCombo.setItems(courseObs);

        // Simple cell factory for combobox display (uses toString()) - Students/Courses override toString()

        // Delete buttons initial state
        deleteStudentBtn.setDisable(true);
        deleteCourseBtn.setDisable(true);

        // Listen for table selection to enable delete
        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> deleteStudentBtn.setDisable(newV == null));
        courseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> deleteCourseBtn.setDisable(newV == null));
    }

    @FXML
    private void handleAddStudent() {
        String name = studentNameField.getText() == null ? "" : studentNameField.getText().trim();
        if (name.isEmpty()) {
            showAlert(AlertType.ERROR, "Validation Error", "Student name cannot be empty.");
            return;
        }
        Student s = new Student(name);
        students.add(s);
        studentObs.add(s);
        studentNameField.clear();
        showAlert(AlertType.INFORMATION, "Success", "Student added: " + s.getName());
    }

    @FXML
    private void handleAddCourse() {
        String name = courseNameField.getText() == null ? "" : courseNameField.getText().trim();
        if (name.isEmpty()) {
            showAlert(AlertType.ERROR, "Validation Error", "Course name cannot be empty.");
            return;
        }
        Course c = new Course(name);
        courses.add(c);
        courseObs.add(c);
        courseNameField.clear();
        showAlert(AlertType.INFORMATION, "Success", "Course added: " + c.getName());
    }

    @FXML
    private void handleRegister() {
        Student student = studentCombo.getValue();
        Course course = courseCombo.getValue();
        if (student == null || course == null) {
            showAlert(AlertType.ERROR, "Validation Error", "Please select both a student and a course.");
            return;
        }
        Registration reg = new Registration(student, course);
        // Prevent duplicates
        for (Registration r : registrations) {
            if (r.equals(reg)) {
                showAlert(AlertType.ERROR, "Duplicate", "This student is already registered for the selected course.");
                return;
            }
        }
        registrations.add(reg);
        regObs.add(reg);
        showAlert(AlertType.INFORMATION, "Success", "Registered " + student.getName() + " -> " + course.getName());
    }

    @FXML
    private void handleViewData() {
        // For this simple app data updates dynamically. We still refresh lists to ensure UI sync.
        studentObs.setAll(students);
        courseObs.setAll(courses);
        regObs.setAll(registrations);
        showAlert(AlertType.INFORMATION, "Data Refreshed", "Displayed data refreshed.");
    }

    @FXML
    private void handleDeleteStudent() {
        Student sel = studentTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        // remove registrations related to student
        Iterator<Registration> it = registrations.iterator();
        boolean changed = false;
        while (it.hasNext()) {
            Registration r = it.next();
            if (r.getStudentId() == sel.getId()) {
                it.remove();
                changed = true;
            }
        }
        students.removeIf(s -> s.getId() == sel.getId());
        studentObs.setAll(students);
        regObs.setAll(registrations);
        showAlert(AlertType.INFORMATION, "Deleted", "Deleted student: " + sel.getName() + (changed ? " (and related registrations)" : ""));
    }

    @FXML
    private void handleDeleteCourse() {
        Course sel = courseTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        // remove registrations related to course
        Iterator<Registration> it = registrations.iterator();
        boolean changed = false;
        while (it.hasNext()) {
            Registration r = it.next();
            if (r.getCourseId() == sel.getId()) {
                it.remove();
                changed = true;
            }
        }
        courses.removeIf(c -> c.getId() == sel.getId());
        courseObs.setAll(courses);
        regObs.setAll(registrations);
        showAlert(AlertType.INFORMATION, "Deleted", "Deleted course: " + sel.getName() + (changed ? " (and related registrations)" : ""));
    }

    private void showAlert(AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model: Registration linking a Student and a Course
 */
public class Registration {
    private static int idCounter = 1;

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty studentId = new SimpleIntegerProperty();
    private final IntegerProperty courseId = new SimpleIntegerProperty();
    private final StringProperty studentName = new SimpleStringProperty();
    private final StringProperty courseName = new SimpleStringProperty();

    public Registration(Student student, Course course) {
        this.id.set(idCounter++);
        this.studentId.set(student.getId());
        this.courseId.set(course.getId());
        this.studentName.set(student.getName());
        this.courseName.set(course.getName());
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public int getStudentId() {
        return studentId.get();
    }

    public IntegerProperty studentIdProperty() {
        return studentId;
    }

    public int getCourseId() {
        return courseId.get();
    }

    public IntegerProperty courseIdProperty() {
        return courseId;
    }

    public String getStudentName() {
        return studentName.get();
    }

    public StringProperty studentNameProperty() {
        return studentName;
    }

    public String getCourseName() {
        return courseName.get();
    }

    public StringProperty courseNameProperty() {
        return courseName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Registration)) return false;
        Registration other = (Registration) obj;
        return other.getStudentId() == getStudentId() && other.getCourseId() == getCourseId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getStudentId()) * 31 + Integer.hashCode(getCourseId());
    }
}

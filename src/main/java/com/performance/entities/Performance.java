package com.performance.entities;

import jakarta.persistence.*;

@Entity
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;


    private int marks;

    @Column(columnDefinition = "TEXT")
    private String comments;
    
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;


    // Constructors
    public Performance() {}

    
	public Performance(int id, Student student, Faculty faculty, Subject subject, int marks, String comments,
			Course course) {
		super();
		this.id = id;
		this.student = student;
		this.faculty = faculty;
		this.subject = subject;
		this.marks = marks;
		this.comments = comments;
		this.course = course;
	}


	// Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    

    public Subject getSubject() {
		return subject;
	}


	public void setSubject(Subject subject) {
		this.subject = subject;
	}


	public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }



	public Course getCourse() {
		return course;
	}



	public void setCourse(Course course) {
		this.course = course;
	}
    
}

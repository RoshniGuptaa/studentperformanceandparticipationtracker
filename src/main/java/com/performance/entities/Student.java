package com.performance.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String name;
	@Column(unique = true)
	private String rollNumber;
	
	
	@OneToOne
	@JoinColumn(name="user_id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "course_id")
	private Course course;

	private String semester;

	private String academicClass;  // example: "1st Year BTech CSE"

	@ManyToMany
	@JoinTable(
	    name = "student_subjects",
	    joinColumns = @JoinColumn(name = "student_id"),
	    inverseJoinColumns = @JoinColumn(name = "subject_id")
	)
	private List<Subject> subjects;


	public Student() {
		super();
		// TODO Auto-generated constructor stub
	}

	

	public Student(int id, String name, String rollNumber, User user, Course course, String semester,
			String academicClass) {
		super();
		this.id = id;
		this.name = name;
		this.rollNumber = rollNumber;
		this.user = user;
		this.course = course;
		this.semester = semester;
		this.academicClass = academicClass;
	}

    

	public Student(int id, String name, String rollNumber, User user, Course course, String semester,
			String academicClass, List<Subject> subjects) {
		super();
		this.id = id;
		this.name = name;
		this.rollNumber = rollNumber;
		this.user = user;
		this.course = course;
		this.semester = semester;
		this.academicClass = academicClass;
		this.subjects = subjects;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRollNumber() {
		return rollNumber;
	}

	public void setRollNumber(String rollNumber) {
		this.rollNumber = rollNumber;
	}

	

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}



	public Course getCourse() {
		return course;
	}



	public void setCourse(Course course) {
		this.course = course;
	}



	public String getSemester() {
		return semester;
	}



	public void setSemester(String semester) {
		this.semester = semester;
	}



	public String getAcademicClass() {
		return academicClass;
	}



	public void setAcademicClass(String academicClass) {
		this.academicClass = academicClass;
	}



	public List<Subject> getSubjects() {
		return subjects;
	}



	public void setSubjects(List<Subject> subjects) {
		this.subjects = subjects;
	}
	
	
	
}

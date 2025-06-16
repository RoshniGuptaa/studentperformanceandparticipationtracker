package com.performance.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Participation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;
	@ManyToOne
	@JoinColumn(name="student_id",nullable = false)
	@JsonIgnoreProperties({"subjects", "user","course"}) // ignore nested fields to stop recursion
	private Student student;
	
	private String activityType; //Workshop,hackathon
	
	private String title;
	private LocalDate date;
	private String level;//Intercollege,National
	private String role;
	//Participant,winner,Organiser
	@Column(columnDefinition = "TEXT")
	private String description;
	public Participation() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Participation(int id, Student student, String activityType, String title, LocalDate date, String level,
			String role, String description) {
		super();
		this.id = id;
		this.student = student;
		this.activityType = activityType;
		this.title = title;
		this.date = date;
		this.level = level;
		this.role = role;
		this.description = description;
	}
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
	public String getActivityType() {
		return activityType;
	}
	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
}

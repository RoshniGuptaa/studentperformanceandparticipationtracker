package com.performance.entities;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Faculty {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String name;
	private String department;
	private String email;
	
	@OneToOne
	@JoinColumn(name="user_id")
	private User user;

	@OneToMany(mappedBy = "faculty")
	private List<Subject> subjects;

	public Faculty() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Faculty(int id, String name, String department, String email, User user) {
		super();
		this.id = id;
		this.name = name;
		this.department = department;
		this.email = email;
		this.user = user;
	}

	public Faculty(int id, String name, String department, String email, User user, List<Subject> subjects) {
		super();
		this.id = id;
		this.name = name;
		this.department = department;
		this.email = email;
		this.user = user;
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

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	
	public List<Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<Subject> subjects) {
		this.subjects = subjects;
	}

	@Override
	public String toString() {
		return "Faculty [id=" + id + ", name=" + name + ", department=" + department + ", email=" + email + ", user="
				+ user.getId() + "]";
	}
	
	
}

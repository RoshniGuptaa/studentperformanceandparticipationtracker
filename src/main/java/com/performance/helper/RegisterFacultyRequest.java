package com.performance.helper;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegisterFacultyRequest {
	@NotBlank(message = "Username is required")
	private  String username;
	 @NotBlank(message = "Password is required")
    private  String password;
	 private  String name;
    
    @NotBlank(message = "Department is required")
    private String department;
    @Email(message = "Invalid email address")
    @NotBlank(message = "Email is required")
    private  String email;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
    
}

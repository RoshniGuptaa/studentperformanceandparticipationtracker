package com.performance.helper;

import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {

	@NotBlank
    private String username;

    @NotBlank
    private String password;
    
	/*
	 * private String role;
	 * 
	 * public String getRole() { return role; }
	 * 
	 * public void setRole(String role) { this.role = role; }
	 */

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
    
    

}

package com.performance.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String username;
	private String password;
	private String role;
	private boolean enabled=true;
	
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}


	public User(int id, String username, String password, String role, boolean enabled) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.role = role;
		this.enabled = enabled;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


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


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public boolean isEnabled() {
		return enabled;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}


	@Override
	public String toString() {
		return "User [id=" + id + ", useame=" + username + ", password=" + password + ", role=" + role + ", enabled="
				+ enabled + "]";
	}
	
	
	
	
}

package com.performance.helper;

public class JwtRequest {

	private String username;
	private String password;
	public String getUsername() {
		return this.username;
	}
	public void setEmail(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setResponse(String password) {
		this.password = password;
	}
	public JwtRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	public JwtRequest(String username, String password) {
		super();
		this.username = username;
		this.password=password;
	}
	
	
}

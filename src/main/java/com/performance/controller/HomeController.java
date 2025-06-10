package com.performance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.performance.dao.UserRepository;
import com.performance.entities.User;
import com.performance.helper.RegisterRequest;

import jakarta.validation.constraints.NotBlank;

@RestController
public class HomeController {

	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@PostMapping("/register")
	public ResponseEntity<String> registerAdmin(@RequestBody RegisterRequest req)
	{
		String username=req.getUsername();
		String password=req.getPassword();
		if(userRepository.findByUsername(username).isPresent())
			return ResponseEntity.badRequest().body("Username already exists");
		
		User user =new User();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));
		user.setRole("ROLE_ADMIN");
		user.setEnabled(true);
		
		userRepository.save(user);
		
		return ResponseEntity.ok("User Registered Successfullly");
	}
}

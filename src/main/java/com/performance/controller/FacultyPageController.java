package com.performance.controller;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/faculty")
@PreAuthorize("hasRole('FACULTY')")
public class FacultyPageController {

	@GetMapping("/dashboard")
    public String dashboardPage(Principal principal,Model model) {
    	
    	String username = principal.getName();
    	model.addAttribute("username", username);
        return "faculty/dashboard"; // Thymeleaf view
    }
}

package com.performance.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.performance.helper.RegisterParticipationRequest;

@Controller
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentPageController {

	
	@ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
    }
	@GetMapping("/dashboard")
	public String dashboard()
	{
		return "student/dashboard";
	}
	
	@GetMapping("/add-participation-form")
	public String showAddParticipationForm(Model model) {
	    model.addAttribute("participation", new RegisterParticipationRequest());
	    return "student/add_participation";
	}

	

}

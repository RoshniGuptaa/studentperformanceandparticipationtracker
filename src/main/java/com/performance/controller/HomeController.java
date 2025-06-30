package com.performance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.performance.dao.UserRepository;
import com.performance.entities.User;
import com.performance.helper.RegisterRequest;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;

@Controller
public class HomeController {

	@GetMapping("/")
	public String home()
	{
		return "index";
	}
	
	@GetMapping("/login")
	public String loginPage() {
	    return "login";
	}

	@GetMapping("/register")
	public String registerPage(Model model) {
	    model.addAttribute("user", new User());
	    return "register";
	}

	@GetMapping("/logout")
    public String logout(HttpServletResponse response) {

        // Create a blank cookie with same name and maxAge 0 to delete it
        Cookie jwtCookie = new Cookie("jwt", "");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false); // set to true if using HTTPS
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // Delete immediately

        response.addCookie(jwtCookie);

        // Redirect to login or home page
        return "redirect:/login?logout=true";
    }
	    
	@GetMapping("/forgot")
	public String showForgotPasswordForm(@ModelAttribute("username")String username,Model model) {
		model.addAttribute("username",username);
	    return "forgot_password";  // Your Thymeleaf page name
	}


}

package com.performance.controller;



import com.performance.config.JwtUtil;
import com.performance.dao.UserRepository;
import com.performance.entities.User;
import com.performance.helper.AuthRequest;
import com.performance.helper.AuthResponse;
import com.performance.helper.JwtRequest;
import com.performance.helper.JwtResponse;
import com.performance.helper.RegisterRequest;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.performance.config.CustomUserDetailsService;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    
    @Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@GetMapping("/auth/clear-jwt")
	public String clearJwtCookie(HttpServletRequest request, HttpServletResponse response) {
	    Cookie jwtClear = new Cookie("jwt", null);
	    jwtClear.setMaxAge(0); // delete immediately
	    jwtClear.setPath("/"); // must match the original path
	    jwtClear.setHttpOnly(true); // optional
	    response.addCookie(jwtClear);

	    return "redirect:/login"; // or a message page
	}

	
	@PostMapping("/register")
	public String registerAdmin(@ModelAttribute("user")User user ,RedirectAttributes redirectAttributes,@ModelAttribute("confirmpassword")String comfirmpassword )
	{
		
		if(userRepository.findByUsername(user.getUsername()).isPresent()) {
			redirectAttributes.addFlashAttribute("error", "Username already exists");
			return "redirect:/register";
	}
	
		if(!user.getPassword().equals(comfirmpassword))
		{
			redirectAttributes.addFlashAttribute("error", "Password do not match..");
			return "redirect:/register";
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
	    user.setEnabled(true);
	    user.setRole("ROLE_ADMIN");
		
		userRepository.save(user);
		
		userRepository.save(user);
	    redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
	    return "redirect:/login";
	}
	
	@PostMapping("/login")
    public String login(@RequestParam("username") String username,@RequestParam("password") String password ,HttpServletResponse response,RedirectAttributes redirectAttributes,HttpServletRequest request) {
        
		try {
			
		
		Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        UserDetails user = (UserDetails) auth.getPrincipal();
        
        String token = jwtUtil.generateToken(user);
        System.out.println(token+"token generated");
        //Set jwt as cookie
        Cookie cookie=new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(60*60);
        response.addCookie(cookie);
        System.out.println(cookie+"generated at time of login");
        
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            if ("ROLE_ADMIN".equals(role)) {
                return "redirect:/admin/dashboard";
            } else if ("ROLE_STUDENT".equals(role)) {
                return "redirect:/student/dashboard";
            } else if ("ROLE_FACULTY".equals(role)) {
                return "redirect:/faculty/dashboard";
            }
        }

        // default fallback
        return "redirect:/login";
        //return "redirect:/admin/dashboard";
		} catch (Exception e) {
			// TODO: handle exception
			redirectAttributes.addFlashAttribute("error", "Invalid username or password");
	        return "redirect:/login";
		}
    }
	
	
	@PostMapping("/forgot-password")
	public String forgotPassword(@RequestParam String username,
	                             @RequestParam String newPassword,
	                             @RequestParam String confirmPassword,
	                             RedirectAttributes redirectAttributes) {

	    if (!newPassword.equals(confirmPassword)) {
	        redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
	        return "redirect:/forgot";
	    }

	    Optional<User> optionalUser = userRepository.findByUsername(username);
	    if (optionalUser.isEmpty()) {
	        redirectAttributes.addFlashAttribute("error", "Username not found.");
	        return "redirect:/forgot";
	    }

	    User user = optionalUser.get();
	    user.setPassword(passwordEncoder.encode(newPassword));
	    userRepository.save(user);

	    redirectAttributes.addFlashAttribute("success", "Password reset successfully. Please login.");
	    return "redirect:/login";
	}

	
}

package com.performance.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.performance.dao.FacultyRepository;
import com.performance.dao.StudentRepository;
import com.performance.dao.UserRepository;
import com.performance.entities.Faculty;
import com.performance.entities.Student;
import com.performance.entities.User;
import com.performance.helper.RegisterFacultyRequest;
import com.performance.helper.RegisterRequest;
import com.performance.helper.RegisterStudentRequest;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private FacultyRepository facultyRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	
	
	
	// Register Student
    @PostMapping("/register-student")
    public ResponseEntity<?> registerStudent(@RequestBody RegisterStudentRequest request) {
        if (userRepository.findByUsername(request.username).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = new User();
        user.setUsername(request.username);
        user.setPassword(passwordEncoder.encode(request.password));
        user.setRole("ROLE_STUDENT");
        user.setEnabled(true);

        userRepository.save(user);

        Student student = new Student();
        student.setName(request.name);
        student.setRollNumber(request.rollNumber);
        student.setUser(user);

        studentRepository.save(student);

        return ResponseEntity.ok("Student registered successfully");
    }
    // Register Faculty
    @PostMapping("/register-faculty")
    public ResponseEntity<?> registerFaculty(@RequestBody RegisterFacultyRequest request) {
        if (userRepository.findByUsername(request.username).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = new User();
        user.setUsername(request.username);
        user.setPassword(passwordEncoder.encode(request.password));
        user.setRole("ROLE_FACULTY");
        user.setEnabled(true);

        userRepository.save(user);

        Faculty faculty = new Faculty();
        faculty.setName(request.name);
        faculty.setDepartment(request.department);
        faculty.setEmail(request.email);
        faculty.setUser(user);

        facultyRepository.save(faculty);

        return ResponseEntity.ok("Faculty registered successfully");
    }
    
    //Editing student data
    @PutMapping("/edit-student/{rollNumber}")
    public ResponseEntity<String> editStudent(@PathVariable String rollNumber,@RequestBody RegisterStudentRequest req)
    {
    	Optional<Student> ostudent = studentRepository.findByRollNumber(rollNumber);
    	if(!ostudent.isPresent())
    		return ResponseEntity.badRequest().body("Student not found with roll number :"+rollNumber);
    	
    	Student student =ostudent.get();
    	if (req.name!=null)
    	student.setName(req.name);
    	if (req.rollNumber!=null)
    	student.setRollNumber(req.rollNumber);
    	
    	User user=student.getUser();
    	if (req.username!=null)
    	user.setUsername(req.username);
    	
    	if(req.password !=null && !req.password.isBlank())
    		user.setPassword(passwordEncoder.encode(req.password));
    	
    	userRepository.save(user);
    	student.setUser(user);
    	studentRepository.save(student);
    	
    	return ResponseEntity.ok("Student updated successfully");
    	
    	
    }

	

}

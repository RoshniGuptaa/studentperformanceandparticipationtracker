package com.performance.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.performance.dao.AttendanceRepository;
import com.performance.dao.CourseRepository;
import com.performance.dao.FacultyRepository;
import com.performance.dao.PerformanceRepository;
import com.performance.dao.StudentRepository;
import com.performance.dao.SubjectRepository;
import com.performance.dao.UserRepository;
import com.performance.entities.Faculty;
import com.performance.entities.Subject;
import com.performance.entities.User;

@Controller
@RequestMapping("/faculty")
@PreAuthorize("hasRole('FACULTY')")
public class FacultyPageController {

	 @Autowired
	    private AttendanceRepository attendanceRepository;

	    @Autowired
	    private StudentRepository studentRepository;

	    @Autowired
	    private FacultyRepository facultyRepository;

	    @Autowired
	    private CourseRepository courseRepository;

	    @Autowired
	    private UserRepository userRepository;
	    @Autowired
	    private SubjectRepository subjectRepository;
	    
	    @Autowired
	    private PerformanceRepository performanceRepository;
	    
	    
	    @ModelAttribute
	    public void addCommonData(Model model, Principal principal) {
	        if (principal != null) {
	            model.addAttribute("username", principal.getName());
	        }
	    }
	    
	@GetMapping("/dashboard")
    public String dashboardPage(Principal principal,Model model) {
    	
    	String username = principal.getName();
    	model.addAttribute("username", username);
        return "faculty/dashboard"; // Thymeleaf view
    }
	
	@GetMapping("/mark-attendance")
	public String showSubjectSelectionPage(Model model, Principal principal) {
	    String facultyUsername = principal.getName();
	    User facultyUser = userRepository.findByUsername(facultyUsername).get();
	    Faculty faculty = facultyRepository.findByUserId(facultyUser.getId()).get();
	    
	    List<Subject> subjects = subjectRepository.findByFacultyId(faculty.getId());
	    model.addAttribute("subjects", subjects);
	    return "faculty/mark_attendance";
	}
	
	@GetMapping("/view-attendance")
	public String showViewAttendancePage(Model model, Principal principal) {
	    String facultyUsername = principal.getName();
	    User facultyUser = userRepository.findByUsername(facultyUsername).get();
	    Faculty faculty = facultyRepository.findByUserId(facultyUser.getId()).get();

	    List<Subject> subjects = subjectRepository.findByFacultyId(faculty.getId());
	    model.addAttribute("subjects", subjects);
	    return "faculty/view_attendance";
	}
	@GetMapping("/enter-marks")
	public String showEnterMarksPage(Model model, Principal principal) {
	    User user = userRepository.findByUsername(principal.getName()).get();
	    Faculty faculty = facultyRepository.findByUserId(user.getId()).get();
	    List<Subject> subjects = subjectRepository.findByFacultyId(faculty.getId());
	    model.addAttribute("subjects", subjects);
	    return "faculty/enter_marks";
	}

	@GetMapping("/view-marks")
	public String viewMarksPage(Model model, Principal principal) {
	    User user = userRepository.findByUsername(principal.getName()).get();
	    Faculty faculty = facultyRepository.findByUserId(user.getId()).get();
	    List<Subject> subjects = subjectRepository.findByFacultyId(faculty.getId());
	    model.addAttribute("subjects", subjects);
	    return "faculty/view_marks";
	}

}

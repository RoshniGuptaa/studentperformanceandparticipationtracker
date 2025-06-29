package com.performance.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.performance.dao.AttendanceRepository;
import com.performance.dao.ParticipationRepository;
import com.performance.dao.PerformanceRepository;
import com.performance.dao.StudentRepository;
import com.performance.dao.UserRepository;
import com.performance.entities.Attendance;
import com.performance.entities.Participation;
import com.performance.entities.Performance;
import com.performance.entities.Student;
import com.performance.entities.Subject;
import com.performance.entities.User;
import com.performance.helper.RegisterParticipationRequest;

@Controller
@RequestMapping("/student/api")
public class StudentController {

	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private AttendanceRepository attendanceRepository;
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PerformanceRepository performanceRepository;
	@Autowired
	private ParticipationRepository participationRepository;
	
	
	@ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
    }
	
	//-------------------------------------------------------------------------
	@GetMapping("my-attendance-details")
	public String getSubjectWiseAttendancePercentage(Principal principal,RedirectAttributes redirectAttributes,Model model)
	{
		String studentUsername=principal.getName();
		
		Optional<User> optionalUser = userRepository.findByUsername(studentUsername);
		
		if(optionalUser.isEmpty()) {
			redirectAttributes.addFlashAttribute("error","Invalid student credentials");
			//return ResponseEntity.badRequest().body("Invalid Student credentials");
			return "student/attendance";
		}
		Optional<Student> optStudent = studentRepository.findByUserId(optionalUser.get().getId());
		
		if(optStudent.isEmpty()) {
			
			redirectAttributes.addFlashAttribute("error","Student not found..");
			return "student/attendance";
			//return ResponseEntity.badRequest().body("Student not found ..");
		}
		Student student=optStudent.get();
		
		List<Subject> subjects=student.getSubjects();
		List<String> labels = new ArrayList<>();
        List<Double> percentages = new ArrayList<>();
		List<Map<String,Object>> result=new ArrayList<>();
		
		for(Subject subject:subjects)
		{
			List<Attendance> attendanceList = attendanceRepository.findByStudentIdAndSubjectId(student.getId(), subject.getId());
			long total=attendanceList.size();
			long present=attendanceList.stream().filter(at->at.getStatus().equals("Present")).count();
			
			double percentage=total==0?0.0:(present*100)/total;
			percentage=Math.round(percentage*100)/100;
			
			labels.add(subject.getSubjectCode());
            percentages.add(percentage);
			Map<String,Object> subjectAttendance=new HashMap<>();
			subjectAttendance.put("code", subject.getSubjectCode());
			subjectAttendance.put("name", subject.getSubjectName());
			subjectAttendance.put("totalClasses", total);
			subjectAttendance.put("attendedClasses", present);
			subjectAttendance.put("percentage", percentage);
			
			result.add(subjectAttendance);
		}
		
		 model.addAttribute("labels", labels);
	        model.addAttribute("percentages", percentages);
		model.addAttribute("attendanceData",result);
		model.addAttribute("studentname",student.getName());
		model.addAttribute("username",principal.getName());
		return "student/attendance";
		//return ResponseEntity.ok(result);
		
	}
	
	//------------------------------VIEW PROFILE----------------------
	@GetMapping("/profile")
	public String viewProfile(Model model, Principal principal) {
	    String username = principal.getName();
	    Optional<User> userOpt = userRepository.findByUsername(username);
	    if (userOpt.isEmpty()) {
	        model.addAttribute("error", "Invalid credentials");
	        return "error";
	    }

	    User user = userOpt.get();
	    Optional<Student> studentOpt = studentRepository.findByUserId(user.getId());
	    if (studentOpt.isEmpty()) {
	        model.addAttribute("error", "Student not found");
	        return "error";
	    }

	    Student student = studentOpt.get();

	    model.addAttribute("student", student);
	    model.addAttribute("user", user);
	    return "student/view_profile";
	}
	//-----------------------------------------------VIEW RESULT----------------------
	@GetMapping("/view-result")
	public String viewResult(Principal principal,RedirectAttributes redirectAttributes,Model model)
	{
		String username=principal.getName();
		User studentUser=userRepository.findByUsername(username).get();
		
		if(studentUser==null) {
			redirectAttributes.addFlashAttribute("error","Invalid student credentials");
			return "student/view_result";
	// return ResponseEntity.badRequest().body("Invalid student credentials");
		}
		Student student = studentRepository.findByUserId(studentUser.getId()).orElseGet(null);
		if(student==null) {
			redirectAttributes.addFlashAttribute("error","Student not found...");
			return "student/view_result";
			//return ResponseEntity.badRequest().body("Student not found...");
		}
		
		List<Performance> performances=performanceRepository.findByStudentId(student.getId());
		
		if(performances.isEmpty())
		{
			model.addAttribute("noData", "No marks available yet.");
            return "student/view-result";
 
			//ResponseEntity.ok("No marks available yet ....");
		}
		
		//Result generation
		List<Map<String,Object>> result=new ArrayList<>();
		
		double totalGradePoints =0.0;
		int subjectCount=performances.size();
		
		for(Performance performance:performances)
		{
			int marks=performance.getMarks();
			String grade=calaculateGrade(marks);
			double gradePoint=getGradePoint(grade);
			totalGradePoints+=gradePoint;
		
		
		Map<String ,Object> resData=new HashMap<>();
		
		resData.put("SubjectName", performance.getSubject().getSubjectName());
		resData.put("SubjectCode", performance.getSubject().getSubjectCode());
		resData.put("Marks", marks);
		resData.put("Grade", grade);
		resData.put("GradePoint", gradePoint);
		result.add(resData);
		}
		
		
		double cgpa=totalGradePoints/subjectCount;
		cgpa=Math.round(cgpa*100)/100;
		
		model.addAttribute("StudentName", student.getName());
		model.addAttribute("rollNumber", student.getRollNumber());
		model.addAttribute("cgpa", cgpa);
		model.addAttribute("results", result);
		
		
		return "student/view_result";
		//return ResponseEntity.ok(finalResult);
	}
	
	private String calaculateGrade(int marks)
	{
		if (marks >= 95) return "A+";
	    else if (marks >= 90) return "A";
	    else if (marks >= 80) return "B+";
	    else if (marks >= 70) return "B";
	    else if (marks >= 60) return "C+";
	    else if (marks >= 50)return "C";
	    else if (marks >= 40) return "D";
	    else return "F";
	}
	
	
	private double getGradePoint(String grade)
	{
		return switch (grade) {
        case "A+" -> 10.0;
        case "A" -> 9.0;
        case "B+" -> 8.0;
        case "B" -> 7.0;
        case "C+" -> 6.0;
        case "C"-> 5.0;
        case "D" -> 4.0;
        default -> 0.0; // Fail
		};
	}
	
	//-------------------------------PARTICIPATION ----------------------------
	
	//ADDING PARTICIPATION
	@PostMapping("/add-participation")
	public String addParticipation(@RequestBody RegisterParticipationRequest req,Principal principal,RedirectAttributes redirectAttributes)
	{
		String username=principal.getName();
		User user=userRepository.findByUsername(username).get();
		
		Student student=studentRepository.findByUserId(user.getId()).get();
		
		Participation participation=new Participation();
		
		participation.setStudent(student);
		participation.setActivityType(req.getActivityType());
		participation.setTitle(req.getTitle());
		participation.setDate(req.getDate());
		participation.setLevel(req.getLevel());
		participation.setRole(req.getRole());
		participation.setDescription(req.getDescription());
		
		participationRepository.save(participation);
		
		redirectAttributes.addFlashAttribute("success", "Participation recorded successfully!");
	    return "redirect:/student/view-participations";
	}
	
	@GetMapping("/view-participations")
    public String getParticipation(Principal principal,RedirectAttributes redirectAttributes,Model model) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).get();
        Student student = studentRepository.findByUserId(user.getId()).get();

        List<Participation> participations = participationRepository.findByStudent(student);
        if(participations.isEmpty()) {
        	redirectAttributes.addFlashAttribute("error","No participations on any event...");
        	return "student/view_participation";
        	//return ResponseEntity.ok("No participations on any event...");
        }
        
        List<RegisterParticipationRequest> participationRegister = participations.stream()
                .map(p -> new RegisterParticipationRequest(
                    p.getActivityType(),
                    p.getTitle(),
                    p.getDate(),
                    p.getLevel(),
                    p.getRole(),
                    p.getDescription()
                ))
                .collect(Collectors.toList());
        model.addAttribute("participations", participations);
        return "student/view_particapation";
            //return ResponseEntity.ok(participationRegister);
       
    }
}

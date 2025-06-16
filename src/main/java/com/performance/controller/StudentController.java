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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
@RequestMapping("/student")
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
	
	//-------------------------------------------------------------------------
	@GetMapping("my-attendance-details")
	public ResponseEntity<?> getSubjectWiseAttendancePercentage(Principal principal)
	{
		String studentUsername=principal.getName();
		
		Optional<User> optionalUser = userRepository.findByUsername(studentUsername);
		
		if(optionalUser.isEmpty())
			return ResponseEntity.badRequest().body("Invalid Student credentials");
		
		Optional<Student> optStudent = studentRepository.findByUserId(optionalUser.get().getId());
		
		if(optStudent.isEmpty())
			return ResponseEntity.badRequest().body("Student not found ..");
		
		Student student=optStudent.get();
		
		List<Subject> subjects=student.getSubjects();
		
		List<Map<String,Object>> result=new ArrayList<>();
		
		for(Subject subject:subjects)
		{
			List<Attendance> attendanceList = attendanceRepository.findByStudentIdAndSubjectId(student.getId(), subject.getId());
			long total=attendanceList.size();
			long present=attendanceList.stream().filter(at->at.getStatus().equals("Present")).count();
			
			double percentage=total==0?0.0:(present*100)/total;
			percentage=Math.round(percentage*100)/100;
			
			Map<String,Object> subjectAttendance=new HashMap<>();
			subjectAttendance.put("Subject Code", subject.getSubjectCode());
			subjectAttendance.put("Subject Name", subject.getSubjectName());
			subjectAttendance.put("Total Classes", total);
			subjectAttendance.put("Attended Classes", present);
			subjectAttendance.put("Attendance Percentage", percentage);
			
			result.add(subjectAttendance);
		}
		return ResponseEntity.ok(result);
		
	}
	
	
	@GetMapping("/view-result")
	public ResponseEntity<?> viewResult(Principal principal)
	{
		String username=principal.getName();
		User studentUser=userRepository.findByUsername(username).get();
		
		if(studentUser==null)
			return ResponseEntity.badRequest().body("Invalid student credentials");
		
		Student student = studentRepository.findByUserId(studentUser.getId()).orElseGet(null);
		if(student==null)
			return ResponseEntity.badRequest().body("Student not found...");
		
		List<Performance> performances=performanceRepository.findByStudentId(student.getId());
		
		if(performances.isEmpty())
			ResponseEntity.ok("No marks available yet ....");
		
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
		
		resData.put("Subject Name", performance.getSubject().getSubjectName());
		resData.put("Subject code", performance.getSubject().getSubjectCode());
		resData.put("Marks", marks);
		resData.put("Grade", grade);
		resData.put("Grade Point", gradePoint);
		result.add(resData);
		}
		Map<String,Object> finalResult=new HashMap<>();
		
		double cgpa=totalGradePoints/subjectCount;
		cgpa=Math.round(cgpa*100)/100;
		
		finalResult.put("Student", student.getName());
		finalResult.put("Roll Number", student.getRollNumber());
		finalResult.put("CGPA", cgpa);
		finalResult.put("Results", result);
		
		return ResponseEntity.ok(finalResult);
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
	public ResponseEntity<String> addParticipation(@RequestBody RegisterParticipationRequest req,Principal principal)
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
		
		return ResponseEntity.ok("Participation recorded successfully");
		
	}
	
	@GetMapping("/view-participations")
    public ResponseEntity<?> getParticipation(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).get();
        Student student = studentRepository.findByUserId(user.getId()).get();

        List<Participation> participations = participationRepository.findByStudent(student);
        if(participations.isEmpty())
        	return ResponseEntity.ok("No participations on any event...");
        
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

            return ResponseEntity.ok(participationRegister);
       
    }
}

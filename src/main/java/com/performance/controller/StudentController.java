package com.performance.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.performance.dao.AttendanceRepository;
import com.performance.dao.StudentRepository;
import com.performance.dao.UserRepository;
import com.performance.entities.Attendance;
import com.performance.entities.Student;
import com.performance.entities.Subject;
import com.performance.entities.User;

@RestController
@RequestMapping("/student")
public class StudentController {

	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private AttendanceRepository attendanceRepository;
	
	@Autowired
	private UserRepository userRepository;
	
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
}

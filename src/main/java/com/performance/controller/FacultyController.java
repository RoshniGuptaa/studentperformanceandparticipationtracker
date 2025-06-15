package com.performance.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.spi.LocaleServiceProvider;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.performance.dao.AttendanceRepository;
import com.performance.dao.CourseRepository;
import com.performance.dao.FacultyRepository;
import com.performance.dao.PerformanceRepository;
import com.performance.dao.StudentRepository;
import com.performance.dao.SubjectRepository;
import com.performance.dao.UserRepository;
import com.performance.entities.Attendance;
import com.performance.entities.Course;
import com.performance.entities.Faculty;
import com.performance.entities.Performance;
import com.performance.entities.Student;
import com.performance.entities.Subject;
import com.performance.entities.User;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

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
	    
	    @PostMapping("/mark-attendance")
	    public ResponseEntity<String> markAttendance(@RequestParam String rollNumber,
	    		                                      @RequestParam String subjectCode,
	    		                                      @RequestParam String status,
	    		                                      @RequestParam @DateTimeFormat(iso=ISO.DATE) LocalDate date,Principal principal)
	    {
	    	
	      Optional<Student> optionalStudent = studentRepository.findByRollNumber(rollNumber);
	      
	      if(optionalStudent.isEmpty())
	    	  return ResponseEntity.badRequest().body("Invalid roll number");
	      
	      String facultyusername=principal.getName();
	         User facultyUser=userRepository.findByUsername(facultyusername).get();
	      Faculty faculty=facultyRepository.findByUserId(facultyUser.getId()).get();
	      
	      if(faculty==null)
	    	  return ResponseEntity.badRequest().body("Unauthorized faculty");
	      
	      Optional<Subject> osubject=subjectRepository.findBySubjectCode(subjectCode);
	      if(osubject.isEmpty())
	    	   return ResponseEntity.badRequest().body("Subject not found");
	      
	      Subject subject=osubject.get();
	      if(!(subject.getFaculty().getId()==faculty.getId()))
	    	  return ResponseEntity.badRequest().body("Faculty do not handle this subject");
	      
	      Student student=optionalStudent.get();
	      
	      if(!student.getSubjects().contains(subject))
	    	  return ResponseEntity.badRequest().body("Student not enrolled in this subject");
	      
	      Course course=subject.getCourse();
	      
	      Attendance attendance=new Attendance();
	      if(attendanceRepository.findByStudentIdAndSubjectIdAndDate(student.getId(), subject.getId(), date).isPresent())
	    	   return ResponseEntity.badRequest().body("Attendence already marked for the student");
	      attendance.setStudent(student);
	      attendance.setFaculty(faculty);
	      attendance.setSubject(subject);
	      attendance.setCourse(course);
	      attendance.setDate(date);
	      attendance.setStatus(status);
	      attendanceRepository.save(attendance);
	      
	      return ResponseEntity.ok(optionalStudent.get().getRollNumber()+" attendance marked");
	      
	    }
	    
	    @GetMapping("/view-students-attendance/{subjectCode}")
	    public ResponseEntity<?> studentsAttendancePercentage(@PathVariable("subjectCode") String subjectCode,Principal principal)
	    {
	    	String facultyUsername=principal.getName();
	    	
	    	User facultyUser=userRepository.findByUsername(facultyUsername).get();
	    	 
		      Faculty faculty=facultyRepository.findByUserId(facultyUser.getId()).get();
		      
		      if(faculty==null)
		    	  return ResponseEntity.badRequest().body("Unauthorized faculty");
		      
		      Optional<Subject> osubject=subjectRepository.findBySubjectCode(subjectCode);
		      if(osubject.isEmpty())
		    	   return ResponseEntity.badRequest().body("Subject not found");
		      
		      Subject subject=osubject.get();
		      
	    	if(subject.getFaculty().getId()!=faculty.getId())
	    		return ResponseEntity.badRequest().body(faculty.getName()+" is not authorized for this course");
	
	    	List<Student> students=studentRepository.findAll().stream().filter(s->s.getSubjects().contains(subject)).collect(Collectors.toList());
	    	
	    	List<Map<String,Object>> result=new ArrayList<>();
	    	for(Student student:students) {
	    	List<Attendance> attendance=attendanceRepository.findByStudentIdAndSubjectId(student.getId(), subject.getId());
	    	
	    	long total=attendance.size();
	    	long present=attendance.stream().filter(f->f.getStatus().equals("Present")).count();
	    	
	    	double percentage= total==0?0.0:((double)present/total*100);
	    	
	    	percentage = Math.round(percentage * 100.0) / 100.0; 

	    	
	    	Map<String, Object> resultData=new HashMap<>();
	    	resultData.put("Roll Number",student.getRollNumber());
	    	resultData.put("Name ",student.getName());
	    	resultData.put("Attendance Percentage", percentage);
	    	result.add(resultData);
	    	}
	    	
	    	return ResponseEntity.ok(result);
	    	
	    	
	    }
	    
	 //---------------------------ENTER MARKS -----------------------------------------------
	 
	 @PostMapping("enter-marks")
	 public ResponseEntity<String> enterSubjectMarks(@RequestParam String rollNumber,
			                                          @RequestParam String subjectCode,
			                                          @RequestParam int marks,
			                                          @RequestParam(required = false) String comments,Principal principal)
	 {
		User  facultyUser =userRepository.findByUsername(principal.getName()).get() ;
		 
	     Optional<Faculty> optfaculty= facultyRepository.findByUserId(facultyUser.getId());
	     if(optfaculty.isEmpty())
	    	  return ResponseEntity.badRequest().body("You are not authorized as faculty");
	     
	     Faculty faculty=optfaculty.get();
	     
	    Optional<Student> optStudent = studentRepository.findByRollNumber(rollNumber);
	    if(optStudent.isEmpty())
	    	 return ResponseEntity.badRequest().body("No Student found whith roll number :"+rollNumber);
	    
	    Student student=optStudent.get();
	    
	    Optional<Subject> optSubject = subjectRepository.findBySubjectCode(subjectCode);
	    if(optSubject.isEmpty())
	    	 return ResponseEntity.badRequest().body("No Subject found whith code :"+subjectCode);
	    
	    Subject subject=optSubject.get();
	    if(!student.getSubjects().contains(subject))
	    	return ResponseEntity.badRequest().body("Student not enrolled in this subject.... ");
	    
	    if(!(subject.getFaculty()==faculty))
	    	return ResponseEntity.badRequest().body("Faculty not authorized for this subject.... ");
	    
	    
	    Performance performance=new Performance();
	    
	    performance.setStudent(student);
	    performance.setFaculty(faculty);
	    performance.setCourse(student.getCourse());
	    performance.setSubject(subject);
	    performance.setMarks(marks);
	    performance.setComments(comments);
	    
	    performanceRepository.save(performance);
	    
	    return ResponseEntity.ok("Marks added successfully for student :"+student.getName());
	     
	 }
	 
	    
	    
	    
	    

}


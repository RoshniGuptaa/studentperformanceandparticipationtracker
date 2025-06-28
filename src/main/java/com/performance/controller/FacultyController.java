package com.performance.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
import com.performance.helper.AttendanceRequest;
import com.performance.helper.RequestPerformance;

@RestController
@RequestMapping("/faculty/api")
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
	    
	//-----------------------------------------MARK ATTENDANCE-------------------------------------------------------------------    
	    @GetMapping("/students-by-subject")
	    @ResponseBody
	    public List<Map<String, Object>> getStudentsBySubject(@RequestParam String subjectCode, Principal principal) {
	        Optional<Subject> optionalSubject = subjectRepository.findBySubjectCode(subjectCode);
	        if (optionalSubject.isEmpty()) {
	            return Collections.emptyList();
	        }

	        Subject subject = optionalSubject.get();
	        List<Student> students = studentRepository.findAll().stream()
	            .filter(s -> s.getSubjects().contains(subject))
	            .collect(Collectors.toList());

	        return students.stream().map(s -> {
	            Map<String, Object> map = new HashMap<>();
	            map.put("id", s.getId());
	            map.put("name", s.getName());
	            map.put("rollNumber", s.getRollNumber());
	            return map;
	        }).collect(Collectors.toList());
	    }

//---------------------------------------MARK  ATTENDANCE -------------------------------------------------
	    @PostMapping("/mark-attendance")
	    public ResponseEntity<String> markAttendance(@RequestBody List<AttendanceRequest> attendanceRequests,Principal principal)
	    {
	    	
	    	String facultyusername=principal.getName();
	         User facultyUser=userRepository.findByUsername(facultyusername).get();
	      Faculty faculty=facultyRepository.findByUserId(facultyUser.getId()).get();
	      
	      if(faculty==null)
	    	  return ResponseEntity.badRequest().body("Unauthorized faculty");
	      
	      int attendancemarked=0;
	      for(AttendanceRequest req:attendanceRequests) {
	      Optional<Student> optionalStudent = studentRepository.findById(req.getStudentId());
	      
	      if(optionalStudent.isEmpty())
	    	  return ResponseEntity.badRequest().body("Invalid roll number");
	      
	      
	      Optional<Subject> osubject=subjectRepository.findBySubjectCode(req.getSubjectCode());
	      if(osubject.isEmpty())
	    	   return ResponseEntity.badRequest().body("Subject not found");
	      
	      Subject subject=osubject.get();
	      if(!(subject.getFaculty().getId()==faculty.getId()))
	    	  return ResponseEntity.badRequest().body("Faculty do not handle this subject");
	      
	      Student student=optionalStudent.get();
	      
	      if(!student.getSubjects().contains(subject))
	    	  return ResponseEntity.badRequest().body("Student not enrolled in this subject");
	      
	      Course course=subject.getCourse();
	      
	     
	      if(attendanceRepository.findByStudentIdAndSubjectIdAndDate(student.getId(), subject.getId(), req.getDate()).isPresent()) {
	    	  attendancemarked++;
	    	    continue;
	    	  // return ResponseEntity.badRequest().body("Attendence already marked for the student");
	      }
	      Attendance attendance=new Attendance();
	      attendance.setStudent(student);
	      attendance.setFaculty(faculty);
	      attendance.setSubject(subject);
	      attendance.setCourse(course);
	      attendance.setDate(req.getDate());
	      attendance.setStatus(req.getStatus());
	      attendanceRepository.save(attendance);
	      }
	      if(attendancemarked==attendanceRequests.size())
	    	   return ResponseEntity.badRequest().body("attendance already marked...");
	      return ResponseEntity.ok("Attendance submitted successfully ...");
	      
	    }
//-------------------------------VIEW ATTENDANCE---------------------------------------------------------	    
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
	 public ResponseEntity<?> enterSubjectMarks(@RequestBody List<RequestPerformance> reqList,Principal principal)
	 {
		User  facultyUser =userRepository.findByUsername(principal.getName()).get() ;
		 
	     Optional<Faculty> optfaculty= facultyRepository.findByUserId(facultyUser.getId());
	     if(optfaculty.isEmpty())
	    	  return ResponseEntity.badRequest().body("You are not authorized as faculty");
	     
	     Faculty faculty=optfaculty.get();
	     
	     int assigned = 0;
	     int already = 0;
	     int error = 0;
	     List<String> alreadyAssignedStudents = new ArrayList<>();
	     
	     for(RequestPerformance req:reqList) {
	    	 
	    Optional<Student> optStudent = studentRepository.findByRollNumber(req.getRollNumber());
	    if(optStudent.isEmpty())
	    	 return ResponseEntity.badRequest().body("No Student found whith roll number :"+req.getRollNumber());
	    
	    Student student=optStudent.get();
	    
	    Optional<Subject> optSubject = subjectRepository.findBySubjectCode(req.getSubjectCode());
	    if(optSubject.isEmpty())
	    	 return ResponseEntity.badRequest().body("No Subject found whith code :"+req.getSubjectCode());
	    
	    Subject subject=optSubject.get();
	    if(!student.getSubjects().contains(subject))
	    	return ResponseEntity.badRequest().body("Student not enrolled in this subject.... ");
	    
	    if(!(subject.getFaculty()==faculty))
	    	return ResponseEntity.badRequest().body("Faculty not authorized for this subject.... ");
	    
	    if( performanceRepository.findByStudentIdAndSubjectId(student.getId(),subject.getId())!=null) {
	    	
	    	already++;
	    	alreadyAssignedStudents.add(student.getName());
	    	continue;
	    	// return ResponseEntity.badRequest().body("Marks already assigned ...");
	    }
	    Performance performance=new Performance();
	    
	    performance.setStudent(student);
	    performance.setFaculty(faculty);
	    performance.setCourse(student.getCourse());
	    performance.setSubject(subject);
	    performance.setMarks(req.getMarks());
	    performance.setComments(req.getComments());
	    
	    performanceRepository.save(performance);
	    assigned++;
	     }
	     return ResponseEntity.ok(
	    	        Map.of(
	    	            "assigned", assigned,
	    	            "already", already,
	    	            "error", error,
	    	            "alreadyAssignedStudents", alreadyAssignedStudents
	    	        )
	    	    );
	    //return ResponseEntity.ok("Marks added successfully for student :"+student.getName());
	     
	 }
	 
	    
	    
	  //----------------------------------VIEW MARKS-----------------------------------------------------
	 
	 @GetMapping("/view-marks/{subjectCode}")
	 public ResponseEntity<?> viewStudentMarks(@PathVariable("subjectCode")String subjectCode,Principal principal)
	 {
		 String facultyusername=principal.getName();
		 
		 User user =userRepository.findByUsername(facultyusername).get();
		 if(user==null)
			 return ResponseEntity.badRequest().body("User not found ");
		
		 Optional<Faculty> ofaculty=facultyRepository.findByUserId(user.getId());
		 
		 if(ofaculty.isEmpty())
			 return ResponseEntity.badRequest().body("Faculty not found ");
			
		Optional<Subject> osubject=subjectRepository.findBySubjectCode(subjectCode);
		if(osubject.isEmpty())
			return ResponseEntity.badRequest().body("Subject not found ");
		
		Faculty faculty=ofaculty.get();
		Subject subject=osubject.get();
		
		if(!(subject.getFaculty().getId()==faculty.getId()))
		{
			return ResponseEntity.badRequest().body("Faculty not authorized for this subject.. ");
			
			
			
		}
		
		List<Performance> performances=performanceRepository.findBySubjectId(subject.getId());
		
		 List<Map<String, Object>> results = new ArrayList<>();
		    for (Performance perf : performances) {
		        Map<String, Object> map = new HashMap<>();
		        map.put("rollNumber", perf.getStudent().getRollNumber());
		        map.put("name", perf.getStudent().getName());
		        map.put("marks", perf.getMarks());
		        map.put("comments", perf.getComments());
		        results.add(map);
		    }

		    return ResponseEntity.ok(results);
		
	 }

}


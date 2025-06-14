package com.performance.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.performance.dao.CourseRepository;
import com.performance.dao.FacultyRepository;
import com.performance.dao.StudentRepository;
import com.performance.dao.SubjectRepository;
import com.performance.dao.UserRepository;
import com.performance.entities.Course;
import com.performance.entities.Faculty;
import com.performance.entities.Student;
import com.performance.entities.Subject;
import com.performance.entities.User;
import com.performance.helper.CourseRequest;
import com.performance.helper.RegisterFacultyRequest;
import com.performance.helper.RegisterRequest;
import com.performance.helper.RegisterStudentRequest;
import com.performance.helper.RegisterSubjectRequest;

@RestController
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
	@Autowired
	private CourseRepository courseRepository;
	
	 @Autowired
	    private SubjectRepository subjectRepository;

	
	//----------------------------------------------COURSE CRUD OPERATION BY ADMIN-------------------------------------
	//Add new Course
	@PostMapping("/add-course")
	public ResponseEntity<String> addCourse(@RequestBody CourseRequest req){
		
		Optional<Faculty> optionalFaculty = facultyRepository.findById(req.getFacultyId());
        if (optionalFaculty.isEmpty()) {
            return ResponseEntity.badRequest().body("Faculty not found with ID: " + req.getFacultyId());
        }

        Course course = new Course();
        course.setCourseName(req.getCourseName());
        course.setCourseCode(req.getCourseCode());
        course.setAcademicYear(req.getAcademicYear());
        course.setFaculty(optionalFaculty.get());

        courseRepository.save(course);

        return ResponseEntity.ok("Course added successfully");
	}
	//Edit course
	@PutMapping("/edit-course/{courseCode}")
	public ResponseEntity<String> editCourse(@PathVariable String courseCode, @RequestBody CourseRequest req) {

	    Optional<Course> optionalCourse = courseRepository.findByCourseCode(courseCode);

	    if (optionalCourse.isEmpty()) {
	        return ResponseEntity.badRequest().body("Course not found with code: " + courseCode);
	    }

	    Course course = optionalCourse.get();

	    if (req.getCourseName() != null) course.setCourseName(req.getCourseName());
	    if (req.getCourseCode() != null) course.setCourseCode(req.getCourseCode());
	    if (req.getAcademicYear() != null) course.setAcademicYear(req.getAcademicYear());

	    if (req.getFacultyId() != 0) {
	        Optional<Faculty> optionalFaculty = facultyRepository.findById(req.getFacultyId());
	        if (optionalFaculty.isPresent()) {
	            course.setFaculty(optionalFaculty.get());
	        } else {
	            return ResponseEntity.badRequest().body("Faculty with ID not found: " + req.getFacultyId());
	        }
	    }

	    courseRepository.save(course);
	    return ResponseEntity.ok("Course updated successfully");
	}

	@DeleteMapping("/delete-course/{courseCode}")
	public ResponseEntity<String> deleteCourse(@PathVariable String courseCode) {

	    Optional<Course> optionalCourse = courseRepository.findByCourseCode(courseCode);

	    if (optionalCourse.isEmpty()) {
	        return ResponseEntity.badRequest().body("Course not found with code: " + courseCode);
	    }

	    courseRepository.delete(optionalCourse.get());

	    return ResponseEntity.ok("Course deleted successfully");
	}

	
	//----------------------------------------------------------SUBJECT CRUD OPERATION -------------------------------
	
	//  Add a subject
    @PostMapping("/add-subject")
    public ResponseEntity<?> addSubject(@RequestBody RegisterSubjectRequest req) {
    	Subject subject=new Subject();
    	subject.setSubjectCode(req.subjectCode);
    	subject.setSubjectName(req.subjectName);
    	Optional<Course> optionalCourse = courseRepository.findById(req.getCourseId());
    	if (optionalCourse.isEmpty()) return ResponseEntity.badRequest().body("Course not found");
        else subject.setCourse(optionalCourse.get());
        
        Optional<Faculty> optionalFaculty = facultyRepository.findById(req.getFacultyId());
        if (optionalFaculty.isEmpty()) return ResponseEntity.badRequest().body("Faculty not found");
        else subject.setFaculty(optionalFaculty.get());
        
    	
        subjectRepository.save(subject);
        return ResponseEntity.ok("Subject added successfully");
    }

    
    //  Edit a subject by subject code
    @PutMapping("/edit-subject/{subjectCode}")
    public ResponseEntity<?> editSubject(@PathVariable String subjectCode, @RequestBody RegisterSubjectRequest req) {
        Optional<Subject> optionalSubject = subjectRepository.findBySubjectCode(subjectCode);
        if (optionalSubject.isEmpty()) return ResponseEntity.badRequest().body("Subject not found");

        
        Subject subject = optionalSubject.get();
        
        if(req.getSubjectCode()!=null) subject.setSubjectCode(req.getSubjectCode());
        if(req.getSubjectName()!=null) subject.setSubjectName(req.getSubjectName());
        
        if(req.getCourseId()!=0) {
        Optional<Course> optionalCourse = courseRepository.findById(req.getCourseId());
        if (optionalCourse.isEmpty()) return ResponseEntity.badRequest().body("Course not found");
        else subject.setCourse(optionalCourse.get());
        }
        if(req.getFacultyId()!=0) {
        Optional<Faculty> optionalFaculty = facultyRepository.findById(req.getFacultyId());
        if (optionalFaculty.isEmpty()) return ResponseEntity.badRequest().body("Faculty not found");
        else subject.setFaculty(optionalFaculty.get());
        }
   

        subjectRepository.save(subject);
        return ResponseEntity.ok("Subject updated successfully");
    }

 // Delete a subject by subject code
    @DeleteMapping("/delete-subject/{subjectCode}")
    public ResponseEntity<?> deleteSubject(@PathVariable String subjectCode) {
        Optional<Subject> optionalSubject = subjectRepository.findBySubjectCode(subjectCode);
        if (optionalSubject.isEmpty()) return ResponseEntity.badRequest().body("Subject not found");
        subjectRepository.delete(optionalSubject.get());
        return ResponseEntity.ok("Subject deleted successfully");
    }
	
	//------------------------------------------STUDENT CRUD OPERATION BY ADMIN ---------------------------
	
	// Register Student
    @PostMapping("/register-student")
    public ResponseEntity<?> registerStudent(@RequestBody RegisterStudentRequest request) {
        if (userRepository.findByUsername(request.username).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

     // Find course by courseCode
        Optional<Course> optionalCourse = courseRepository.findByCourseCode(request.courseCode);
        if (optionalCourse.isEmpty()) {
            return ResponseEntity.badRequest().body("Course not found with code: " + request.courseCode);
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
        student.setCourse(optionalCourse.get());
        student.setSemester(request.semester);
        student.setAcademicClass(request.academicClass);

        studentRepository.save(student);

        return ResponseEntity.ok("Student registered successfully");
    }
    
    
    //Editing student data
    @PutMapping("/edit-student/{rollNumber}")
    public ResponseEntity<String> editStudent(@PathVariable String rollNumber,@RequestBody RegisterStudentRequest req)
    {
    	Optional<Student> ostudent = studentRepository.findByRollNumber(rollNumber);
    	if(!ostudent.isPresent())
    		return ResponseEntity.badRequest().body("Student not found with roll number :"+rollNumber);
    	
    	Student student =ostudent.get();
    	if (req.name!=null) student.setName(req.name);
    	if (req.rollNumber!=null) student.setRollNumber(req.rollNumber);
    	 if (req.semester != null) student.setSemester(req.semester);
    	 if (req.academicClass != null) student.setAcademicClass(req.academicClass);
    	 
    	User user=student.getUser();
    	if (req.username!=null)
    	user.setUsername(req.username);
    	
    	if(req.password !=null && !req.password.isBlank())
    		user.setPassword(passwordEncoder.encode(req.password));
    	
    	userRepository.save(user);
        student.setUser(user);
        
     // Update course using courseCode
        if (req.courseCode != null) {
            Optional<Course> optionalCourse = courseRepository.findByCourseCode(req.courseCode);
            if (optionalCourse.isEmpty())
                return ResponseEntity.badRequest().body("Course not found with code: " + req.courseCode);
            student.setCourse(optionalCourse.get());
        }
    	studentRepository.save(student);
    	
    	return ResponseEntity.ok("Student updated successfully");
    }
    	//Deleting student data
    	@DeleteMapping("/delete-student/{rollNumber}")
    	public ResponseEntity<String> deleteStudent(@PathVariable String rollNumber){
    		
    		Optional<Student> optionalstudent = studentRepository.findByRollNumber(rollNumber);
    		
    		if(optionalstudent.isEmpty())
    			return ResponseEntity.badRequest().body("Student not found with roll number : "+rollNumber);
    		
    		Student student=optionalstudent.get();
    		
    		User user=student.getUser();
    		
    		studentRepository.delete(student);
    		if(user!=null)
    			userRepository.delete(user);
    		
    		
    		return ResponseEntity.ok("Student deleted successfully");
    	}
    	
    	//      UPDATING SEMESTER OF STUDENT
    	
    	 @PutMapping("/update-student-semester/{rollNumber}")
    	    public ResponseEntity<?> updateStudentSemester(@PathVariable String rollNumber,
    	                                                   @RequestParam String semester) {
    	        Optional<Student> studentOpt = studentRepository.findByRollNumber(rollNumber);
    	        if (studentOpt.isEmpty()) return ResponseEntity.badRequest().body("Student not found");

    	        Student student = studentOpt.get();
    	        student.setSemester(semester);
    	        studentRepository.save(student);
    	        return ResponseEntity.ok("Student semester updated successfully");
    	    }
    	 
    	//  Assign subjects to student
    	 
    	    @PutMapping("/assign-subjects-to-student/{rollNumber}")
    	    public ResponseEntity<?> assignSubjectsToStudent(@PathVariable String rollNumber,
    	                                                     @RequestBody List<Integer> subjectIds) {
    	        Optional<Student> studentOpt = studentRepository.findByRollNumber(rollNumber);
    	        if (studentOpt.isEmpty()) return ResponseEntity.badRequest().body("Student not found");

    	        Student student = studentOpt.get();
    	        List<Subject> subjects = subjectRepository.findAllById(subjectIds);
    	        List<Subject> oldSubjects= student.getSubjects();
    	        if(oldSubjects==null)
    	             student.setSubjects(subjects);
    	        else
    	        {
    	        	oldSubjects.addAll(subjects);
    	        }
    	        studentRepository.save(student);
    	        return ResponseEntity.ok("Subjects assigned to student successfully");
    	    }
    	 
    	
    //------------------------------------------FACULTY OPERATIONS--------------------------------------	
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
    	
    //Editing faculty data
    	@PutMapping("/edit-faculty/{username}")
    	public ResponseEntity<String> editFaculty(@PathVariable String username,@RequestBody RegisterFacultyRequest req)
    	{
    		Optional<User> optionalUser = userRepository.findByUsername(username);
    		if(optionalUser.isEmpty())
    			return ResponseEntity.badRequest().body("No faculty with username : "+username);
    		
    		User user=optionalUser.get();
    		
    		Faculty faculty=facultyRepository.findAll().stream().filter(f->f.getUser().getId()==user.getId()).findFirst().orElse(null);
    		
    		if(faculty==null)
    			return ResponseEntity.badRequest().body("No faculty with username : "+username);
    		
    		
    		if (req.username != null) user.setUsername(req.username);
    	    if (req.password != null && !req.password.isBlank()) {
    	        user.setPassword(passwordEncoder.encode(req.password));
    	    }
    	    userRepository.save(user);
    	    
    	    if (req.name != null) faculty.setName(req.name);
    	    if (req.department != null) faculty.setDepartment(req.department);
    	    if (req.email != null) faculty.setEmail(req.email);

    	    faculty.setUser(user);
    	    facultyRepository.save(faculty);

    	    
    	    return ResponseEntity.ok("Faculty updated successfully");
    		
    		
    	}
    	
    	//Delete faculty data
    	@DeleteMapping("/delete-faculty/{username}")
    	public ResponseEntity<String> deleteFaculty(@PathVariable String username) {

    	    Optional<User> userOptional = userRepository.findByUsername(username);
    	    if (userOptional.isEmpty()) {
    	        return ResponseEntity.badRequest().body("Faculty with username not found: " + username);
    	    }

    	    User user = userOptional.get();

    	    // Find faculty by user
    	    Faculty faculty = facultyRepository.findAll()
    	        .stream()
    	        .filter(f -> f.getUser().getId() == user.getId())
    	        .findFirst()
    	        .orElse(null);

    	    if (faculty == null) {
    	        return ResponseEntity.badRequest().body("No faculty entity linked to username: " + username);
    	    }

    	    // First delete faculty to avoid FK violation
    	    facultyRepository.delete(faculty);
    	    userRepository.delete(user);

    	    return ResponseEntity.ok("Faculty deleted successfully.");
    	}

    	//  Assign subjects to faculty
        @PutMapping("/assign-subjects-to-faculty/{facultyId}")
        public ResponseEntity<?> assignSubjectsToFaculty(@PathVariable int facultyId,
                                                         @RequestBody List<Integer> subjectIds) {
            Optional<Faculty> facultyOpt = facultyRepository.findById(facultyId);
            if (facultyOpt.isEmpty()) return ResponseEntity.badRequest().body("Faculty not found");

            Faculty faculty = facultyOpt.get();
            List<Subject> subjects = subjectRepository.findAllById(subjectIds);

            for (Subject subject : subjects) {
                subject.setFaculty(faculty);
            }
            subjectRepository.saveAll(subjects);

            return ResponseEntity.ok("Subjects assigned to faculty successfully");
        }
        

	

}

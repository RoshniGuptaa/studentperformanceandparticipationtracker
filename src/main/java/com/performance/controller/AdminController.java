package com.performance.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

import jakarta.validation.Valid;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/api")
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
	public String addCourse(@ModelAttribute("course") CourseRequest req,RedirectAttributes redirectAttributes,Model mode){
		
		Optional<Faculty> optionalFaculty = facultyRepository.findById(req.getFacultyId());
        if (optionalFaculty.isEmpty()) {
        	 redirectAttributes.addFlashAttribute("error", "Faculty not found with ID: " + req.getFacultyId());
             return "redirect:/admin/add-course";
            //return ResponseEntity.badRequest().body("Faculty not found with ID: " + req.getFacultyId());
        }

        Course course = new Course();
        course.setCourseName(req.getCourseName());
        course.setCourseCode(req.getCourseCode());
        course.setAcademicYear(req.getAcademicYear());
        course.setFaculty(optionalFaculty.get());

        courseRepository.save(course);

        redirectAttributes.addFlashAttribute("success", "Course added successfully!");
        return "redirect:/admin/add-course";
        //return ResponseEntity.ok("Course added successfully");
	}
	//Edit course
	@PostMapping("/edit-course/{courseCode}")
	public String editCourse(@PathVariable String courseCode, @ModelAttribute("course") CourseRequest req,RedirectAttributes redirectAttributes) {

	    Optional<Course> optionalCourse = courseRepository.findByCourseCode(courseCode);

	    if (optionalCourse.isEmpty()) {
	    	 redirectAttributes.addFlashAttribute("error", "Course not found");
	         return "redirect:/admin/manage-courses";
	        //return ResponseEntity.badRequest().body("Course not found with code: " + courseCode);
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
	        	redirectAttributes.addFlashAttribute("error", "Faculty is not found..");
	        	return "redirect:/admin/manage-courses";
	            //return ResponseEntity.badRequest().body("Faculty with ID not found: " + req.getFacultyId());
	        }
	    }

	    courseRepository.save(course);
	    redirectAttributes.addFlashAttribute("success", "Course updated successfully");
	    return "redirect:/admin/manage-courses";
	    //return ResponseEntity.ok("Course updated successfully");
	}

	@GetMapping("/delete-course/{courseCode}")
	public String deleteCourse(@PathVariable String courseCode,RedirectAttributes redirectAttributes) {

	    Optional<Course> optionalCourse = courseRepository.findByCourseCode(courseCode);

	    if (optionalCourse.isEmpty()) {
	        
	    	redirectAttributes.addFlashAttribute("error","Course not found with code: " + courseCode );
		    return "redirect:/admin/manage-courses";
	    	//return ResponseEntity.badRequest().body("Course not found with code: " + courseCode);
	    }

	    courseRepository.delete(optionalCourse.get());
	    redirectAttributes.addFlashAttribute("success", "Course deleted successfully");
	    return "redirect:/admin/manage-courses";
	    
	    //return ResponseEntity.ok("Course deleted successfully");
	}

	
	//----------------------------------------------------------SUBJECT CRUD OPERATION -------------------------------
	
	//  Add a subject
    @PostMapping("/add-subject")
    public String addSubject(@ModelAttribute RegisterSubjectRequest req,RedirectAttributes redirectAttributes) {
    	Subject subject=new Subject();
    	subject.setSubjectCode(req.subjectCode);
    	subject.setSubjectName(req.subjectName);
    	Optional<Course> optionalCourse = courseRepository.findById(req.getCourseId());
    	if (optionalCourse.isEmpty())
    		{ 
    		    redirectAttributes.addFlashAttribute("error","course not found");
    		    return "redirect:/admin/add-subject";
    		}
        else subject.setCourse(optionalCourse.get());
        
        Optional<Faculty> optionalFaculty = facultyRepository.findById(req.getFacultyId());
        if (optionalFaculty.isEmpty())
        	{
        	redirectAttributes.addFlashAttribute("error","Faculty not found");
		    return "redirect:/admin/add-subject";
        	//return ResponseEntity.badRequest().body("Faculty not found");
		    }
        else subject.setFaculty(optionalFaculty.get());
        
    	
        subjectRepository.save(subject);
        redirectAttributes.addFlashAttribute("success","Subject added successfully");
	    return "redirect:/admin/add-subject";
        //return ResponseEntity.ok("Subject added successfully");
    }

    
    //  Edit a subject by subject code
    @PostMapping("/edit-subject/{subjectCode}")
    public String editSubject(@PathVariable String subjectCode, @ModelAttribute RegisterSubjectRequest req,RedirectAttributes redirectAttributes) {
        Optional<Subject> optionalSubject = subjectRepository.findBySubjectCode(subjectCode);
        if (optionalSubject.isEmpty()) {
        	 redirectAttributes.addFlashAttribute("error", "Subject not found");
             return "redirect:/admin/manage-subjects";
        	//return ResponseEntity.badRequest().body("Subject not found");
        }
        
        Subject subject = optionalSubject.get();
        
        if(req.getSubjectCode()!=null) subject.setSubjectCode(req.getSubjectCode());
        if(req.getSubjectName()!=null) subject.setSubjectName(req.getSubjectName());
        
        if(req.getCourseId()!=0) {
        Optional<Course> optionalCourse = courseRepository.findById(req.getCourseId());
        if (optionalCourse.isEmpty()) 
        	{
        	 redirectAttributes.addFlashAttribute("error", "Course not found");
             return "redirect:/admin/manage-subjects";
        	//return ResponseEntity.badRequest().body("Course not found");
        	
        	}
        else subject.setCourse(optionalCourse.get());
        }
        if(req.getFacultyId()!=0) {
        Optional<Faculty> optionalFaculty = facultyRepository.findById(req.getFacultyId());
        if (optionalFaculty.isEmpty())
        	{//return ResponseEntity.badRequest().body("Faculty not found");
        	 redirectAttributes.addFlashAttribute("error", "Faculty not found");
             return "redirect:/admin/manage-subjects";
        	}
        else subject.setFaculty(optionalFaculty.get());
        }
   

        subjectRepository.save(subject);
        redirectAttributes.addFlashAttribute("success", "Subject updated successfully");
        return "redirect:/admin/manage-subjects";
        //return ResponseEntity.ok("Subject updated successfully");
    }

 // Delete a subject by subject code
    @GetMapping("/delete-subject/{subjectCode}")
    public String deleteSubject(@PathVariable String subjectCode,RedirectAttributes redirectAttributes) {
        Optional<Subject> optionalSubject = subjectRepository.findBySubjectCode(subjectCode);
        if (optionalSubject.isEmpty())
        {
        	redirectAttributes.addFlashAttribute("error", "Subject not found");
        	//return ResponseEntity.badRequest().body("Subject not found");
        }
        else {
        subjectRepository.delete(optionalSubject.get());
        redirectAttributes.addFlashAttribute("success", "Subject deleted successfully....");
        }
        return "redirect:/admin/manage-subjects";
        //return ResponseEntity.ok("Subject deleted successfully");
    }
	
	//------------------------------------------STUDENT CRUD OPERATION BY ADMIN ---------------------------
	
	// Register Student
    @PostMapping("/register-student")
    public String registerStudent(@ModelAttribute RegisterStudentRequest request,RedirectAttributes redirectAttributes) {
        if (userRepository.findByUsername(request.username).isPresent()) {
        	 redirectAttributes.addFlashAttribute("error", "Username already exists");
             return "redirect:/admin/register-student";
        	//return ResponseEntity.badRequest().body("Username already exists");
        }

     // Find course by courseCode
        Optional<Course> optionalCourse = courseRepository.findByCourseCode(request.courseCode);
        if (optionalCourse.isEmpty()) {
        	redirectAttributes.addFlashAttribute("error", "Course not found");
            return "redirect:/admin/register-student";
        	//return ResponseEntity.badRequest().body("Course not found with code: " + request.courseCode);
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
        redirectAttributes.addFlashAttribute("success", "Student registered successfully");
        return "redirect:/admin/register-student";
       // return ResponseEntity.ok("Student registered successfully");
    }
    
    
    //Editing student data
    @PostMapping("/edit-student/{rollNumber}")
    public String editStudent(@PathVariable String rollNumber,@ModelAttribute RegisterStudentRequest req,RedirectAttributes redirectAttributes)
    {
    	Optional<Student> ostudent = studentRepository.findByRollNumber(rollNumber);
    	if(!ostudent.isPresent())
    	{
    		 redirectAttributes.addFlashAttribute("error", "Student not found with roll number: " + rollNumber);
    	        return "redirect:/admin/manage-students";
    		//return ResponseEntity.badRequest().body("Student not found with roll number :"+rollNumber);
    	}
    	
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
            {
            	 redirectAttributes.addFlashAttribute("error", "Course not found with code: " + req.courseCode);
     	        return "redirect:/admin/manage-students";
                //return ResponseEntity.badRequest().body("Course not found with code: " + req.courseCode);
            }
            student.setCourse(optionalCourse.get());
        }
    	studentRepository.save(student);
    	
    	 redirectAttributes.addFlashAttribute("success", "Student updated successfully");
    	    return "redirect:/admin/manage-students";
    	//return ResponseEntity.ok("Student updated successfully");
    }
    	//Deleting student data
    	@GetMapping("/delete-student/{rollNumber}")
    	public String deleteStudent(@PathVariable String rollNumber,RedirectAttributes redirectAttributes){
    		
    		Optional<Student> optionalstudent = studentRepository.findByRollNumber(rollNumber);
    		
    		if(optionalstudent.isEmpty()) {
    			 redirectAttributes.addFlashAttribute("error", "Student not found with roll number: " + rollNumber);
    		        return "redirect:/admin/manage-students";
    			//return ResponseEntity.badRequest().body("Student not found with roll number : "+rollNumber);
    		}
    		Student student=optionalstudent.get();
    		
    		User user=student.getUser();
    		
    		studentRepository.delete(student);
    		if(user!=null)
    			userRepository.delete(user);
    		
    		redirectAttributes.addFlashAttribute("success", "Student deleted successfully");
    	    return "redirect:/admin/manage-students";
    		//return ResponseEntity.ok("Student deleted successfully");
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
    	 
    	    @PostMapping("/assign-subjects-to-student/{rollNumber}")
    	    public String assignSubjectsToStudent(@PathVariable String rollNumber,
    	                                                     @RequestParam List<Integer> subjectIds,RedirectAttributes redirectAttributes) {
    	        Optional<Student> studentOpt = studentRepository.findByRollNumber(rollNumber);
    	        if (studentOpt.isEmpty()) {
    	        	redirectAttributes.addFlashAttribute("error", "Student not found");
    	            return "redirect:/admin/manage-students";
    	        	//return ResponseEntity.badRequest().body("Student not found");
    	        }

    	        Student student = studentOpt.get();
    	        List<Subject> oldSubjects= student.getSubjects();
    	        subjectIds=subjectIds.stream().filter(id->!(oldSubjects.contains(subjectRepository.findById(id).get()))).collect(Collectors.toList());
    	        List<Subject> subjects = subjectRepository.findAllById(subjectIds);
    	        //List<Subject> oldSubjects= student.getSubjects();
    	        subjects.forEach(sub->sub.setCourse(student.getCourse()));
    	        if(oldSubjects==null)
    	             student.setSubjects(subjects);
    	        else
    	        {
    	        	oldSubjects.addAll(subjects);
    	        }
    	        studentRepository.save(student);
    	        redirectAttributes.addFlashAttribute("success", "Subjects assigned successfully");

    	        return "redirect:/admin/manage-students";
    	        //return ResponseEntity.ok("Subjects assigned to student successfully");
    	    }
    	    
    	    @PostMapping("/delete-subjects/{rollNumber}")
    	    public String deleteSubjectsFromStudent(@PathVariable String rollNumber,
    	                                            @RequestParam(name = "subjectIds", required = false) List<Integer> subjectIds,
    	                                            RedirectAttributes redirectAttributes) {
    	        Optional<Student> studentOpt = studentRepository.findByRollNumber(rollNumber);
    	        if (studentOpt.isEmpty()) {
    	            redirectAttributes.addFlashAttribute("error", "Student not found");
    	            return "redirect:/admin/manage-students";
    	        }

    	        Student student = studentOpt.get();
    	        List<Subject> subjects = student.getSubjects();

    	        if (subjectIds != null && !subjectIds.isEmpty()) {
    	            subjects.removeIf(subject -> subjectIds.contains(subject.getId()));
    	            student.setSubjects(subjects);
    	            studentRepository.save(student);
    	            redirectAttributes.addFlashAttribute("success", "Selected subjects deleted successfully.");
    	        } else {
    	            redirectAttributes.addFlashAttribute("warning", "No subjects selected for deletion.");
    	        }

    	        return "redirect:/admin/manage-students";
    	    }

    	 
    	
    //------------------------------------------FACULTY OPERATIONS--------------------------------------	
    	// Register Faculty
        @PostMapping("/register-faculty")
        public String registerFaculty(@Valid @ModelAttribute("faculty") RegisterFacultyRequest request,Model model,BindingResult result) {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                //return ResponseEntity.badRequest().body("Username already exists");
            	result.rejectValue("username", null,"Username already exists");
            }
            if (result.hasErrors()) {
                return "admin/add_faculty";
            }
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole("ROLE_FACULTY");
            user.setEnabled(true);

            userRepository.save(user);

            Faculty faculty = new Faculty();
            faculty.setName(request.getName());
            faculty.setDepartment(request.getDepartment());
            faculty.setEmail(request.getEmail());
            faculty.setUser(user);

            facultyRepository.save(faculty);

            //return ResponseEntity.ok("Faculty registered successfully");
            model.addAttribute("success", "Faculty registered successfully");
            model.addAttribute("faculty", new RegisterFacultyRequest()); // reset form
            return "admin/add_faculty";
        }
    	
    //Editing faculty data
    	@PostMapping("/edit-faculty/{username}")
    	public String editFaculty(@PathVariable String username,@ModelAttribute("faculty") RegisterFacultyRequest req,RedirectAttributes redirectAttributes)
    	{
    		Optional<User> optionalUser = userRepository.findByUsername(username);
    		if(optionalUser.isEmpty()) {
    			redirectAttributes.addFlashAttribute("error", "No faculty with username : "+username);
    			return "redirect:/admin/edit-faculty-form/{username}";
    			//return ResponseEntity.badRequest().body("No faculty with username : "+username);
    		}
    		
    		User user=optionalUser.get();
    		
    		Faculty faculty=facultyRepository.findAll().stream().filter(f->f.getUser().getId()==user.getId()).findFirst().orElse(null);
    		
    		if(faculty==null) {
    			redirectAttributes.addFlashAttribute("error", "No faculty with username : "+username);
    			return "redirect:/admin/edit-faculty-form/{username}";
    			//return ResponseEntity.badRequest().body("No faculty with username : "+username);
    		}
    		
    		
    		if (req.getUsername() != null) user.setUsername(req.getUsername());
    	    if (req.getPassword() != null && !req.getPassword().isBlank()) {
    	        user.setPassword(passwordEncoder.encode(req.getPassword()));
    	    }
    	    userRepository.save(user);
    	    
    	    if (req.getName() != null) faculty.setName(req.getName());
    	    if (req.getDepartment() != null) faculty.setDepartment(req.getDepartment());
    	    if (req.getEmail() != null) faculty.setEmail(req.getEmail());

    	    faculty.setUser(user);
    	    facultyRepository.save(faculty);

    	    redirectAttributes.addFlashAttribute("success", username+"is updated");
    	    return "redirect:/admin/manage-faculties";
    		
    		
    	}
    	
    	//Delete faculty data
    	@GetMapping("/delete-faculty/{username}")
    	public String deleteFaculty(@PathVariable String username,RedirectAttributes redirectAttributes) {

    	    Optional<User> userOptional = userRepository.findByUsername(username);
    	    if (userOptional.isEmpty()) {
    	    	redirectAttributes.addFlashAttribute("error","Faculty with username not found: " + username);
    	    	return "redirect:/admin/manage-faculties";
    	       // return ResponseEntity.badRequest().body("Faculty with username not found: " + username);
    	    }

    	    User user = userOptional.get();

    	    // Find faculty by user
    	    Faculty faculty = facultyRepository.findAll()
    	        .stream()
    	        .filter(f -> f.getUser().getId() == user.getId())
    	        .findFirst()
    	        .orElse(null);

    	    if (faculty == null) {
    	    	redirectAttributes.addFlashAttribute("error","Faculty with username not found: " + username);
    	    	return "redirect:/admin/manage-faculties";
    	        //return ResponseEntity.badRequest().body("No faculty entity linked to username: " + username);
    	    }

    	    // First delete faculty to avoid FK violation
    	    facultyRepository.delete(faculty);
    	    userRepository.delete(user);
    	    redirectAttributes.addFlashAttribute("success","Faculty deleted successfully... " + username);
	    	return "redirect:/admin/manage-faculties";
    	    //return ResponseEntity.ok("Faculty deleted successfully.");
    	}

    	//  Assign subjects to faculty
        @PostMapping("/assign-subjects-to-faculty/{facultyId}")
        public String assignSubjectsToFaculty(@PathVariable("facultyId") int facultyId,
                                                         @RequestParam("subjectIds") List<Integer> subjectIds,RedirectAttributes redirectAttributes) {
            Optional<Faculty> facultyOpt = facultyRepository.findById(facultyId);
            if (facultyOpt.isEmpty()) {
            	redirectAttributes.addFlashAttribute("error", "Faculty not found");
            return "redirect:/admin/manage-faculties";
            }
            //return ResponseEntity.badRequest().body("Faculty not found");

            Faculty faculty = facultyOpt.get();
            List<Subject> subjects = subjectRepository.findAllById(subjectIds);

            for (Subject subject : subjects) {
                subject.setFaculty(faculty);
            }
            subjectRepository.saveAll(subjects);
            redirectAttributes.addFlashAttribute("success", "Subjects assigned successfully");
            return "redirect:/admin/manage-faculties";
            //return ResponseEntity.ok("Subjects assigned to faculty successfully");
        }
        

	

}

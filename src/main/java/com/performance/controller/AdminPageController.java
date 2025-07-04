package com.performance.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.performance.helper.RegisterSubjectRequest;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPageController {

	@Autowired
	 FacultyRepository facultyRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	SubjectRepository subjectRepository;
	@Autowired
	private CourseRepository courseRepository;
	@Autowired
	private StudentRepository studentRepository;
	 
	
	
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
        return "admin/dashboard"; // Thymeleaf view
    }
    
    @GetMapping("/add-faculty")
    public String addFacultyForm(Model model,Principal principal) {
    	System.out.println("Logged in user: " + principal.getName()); // debug
        model.addAttribute("faculty", new RegisterFacultyRequest());
        return "/admin/add_faculty";
    }
    
    @GetMapping("/manage-faculties")
    public String manageFaculties(Model model,@RequestParam(defaultValue="0")int page) {
    	
		/*
		 * List<Faculty> faculties = facultyRepository.findAll();
		 * model.addAttribute("faculties", faculties);
		 */
        Page<Faculty> facultyPage = facultyRepository.findAll(PageRequest.of(page, 5)); // 5 per page
        model.addAttribute("facultyPage", facultyPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", facultyPage.getTotalPages());
        return "admin/manage_faculties";  // This refers to the Thymeleaf template
    }
    
    @GetMapping("/edit-faculty-form/{username}")
    public String showEditForm(@PathVariable String username, Model model) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) return "redirect:/admin/manage-faculties";

        Faculty faculty = facultyRepository.findAll()
                .stream()
                .filter(f -> f.getUser().getId() == user.get().getId())
                .findFirst()
                .orElse(null);
       RegisterFacultyRequest req=new RegisterFacultyRequest();
       req.setUsername(username);
       req.setName(faculty.getName());
       req.setEmail(faculty.getEmail());
       req.setDepartment(faculty.getDepartment());
      
       model.addAttribute("faculty", req);
        
        return "admin/edit_faculty";
    }
    @GetMapping("/assign-subject-form/{facultyId}")
    public String showAssignSubjectForm(@PathVariable int facultyId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Faculty> facultyOpt = facultyRepository.findById(facultyId);
        if (facultyOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Faculty not found");
            return "redirect:/admin/manage-faculties";
        }

        Faculty faculty = facultyOpt.get();
        List<Subject> allSubjects = subjectRepository.findAll();
        
     // Filter distinct subjects by name
        List<Subject> uniqueSubjects = allSubjects.stream()
            .collect(Collectors.collectingAndThen(
                Collectors.toMap(Subject::getSubjectName, s -> s, (s1, s2) -> s1),
                map -> new ArrayList<>(map.values())
            ));

        model.addAttribute("faculty", faculty);
        model.addAttribute("subjects", uniqueSubjects);

        return "admin/assign_subject";  // This HTML will handle JS-based submission
    }

    @GetMapping("/add-course")
    public String showAddCourseForm(Model model) {
        List<Faculty> faculties = facultyRepository.findAll();
        model.addAttribute("faculties", faculties);
        model.addAttribute("course", new CourseRequest());
        return "admin/add_course"; // this is your Thymeleaf HTML page
    }
    @GetMapping("/manage-courses")
    public String manageCourses(Model model) {
        List<Course> courses = courseRepository.findAll();
        model.addAttribute("courses", courses);
        return "admin/manage_courses";  // Refers to the Thymeleaf file you just created
    }

    @GetMapping("/edit-course-form/{courseCode}")
    public String showEditCourseForm(@PathVariable String courseCode, Model model, RedirectAttributes redirectAttributes) {
        Optional<Course> courseOpt = courseRepository.findByCourseCode(courseCode);
        if (courseOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Course not found with code: " + courseCode);
            return "redirect:/admin/manage-courses";
        }

        Course course = courseOpt.get();
        
        List<Faculty> faculties = facultyRepository.findAll();

        model.addAttribute("course", course);
        model.addAttribute("faculties", faculties);

        return "admin/edit_course"; // points to edit_course.html
    }
    
    
    @GetMapping("/add-subject")
    public String showAddSubjectForm(Model model) {
        model.addAttribute("subject", new RegisterSubjectRequest());
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("faculties", facultyRepository.findAll());
        return "admin/add_subject"; // This matches the HTML file provided
    }
    
    @GetMapping("/manage-subjects")
    public String showManageSubjects(Model model, 
                                     @RequestParam(defaultValue = "0") int page) {
        Page<Subject> subjectPage = subjectRepository.findAll(PageRequest.of(page, 5));
        model.addAttribute("subjectPage", subjectPage);
        
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", subjectPage.getTotalPages());
        return "admin/manage_subjects"; // Thymeleaf HTML file
    }
    
    @GetMapping("/edit-subject-form/{subjectCode}")
    public String showEditSubjectForm(@PathVariable String subjectCode, Model model, RedirectAttributes redirectAttributes) {
        Optional<Subject> optionalSubject = subjectRepository.findBySubjectCode(subjectCode);
        if (optionalSubject.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Subject not found");
            return "redirect:/admin/manage-subjects";
        }

        List<Course> courses = courseRepository.findAll();
        List<Faculty> faculties = facultyRepository.findAll();

        model.addAttribute("subject", optionalSubject.get());
        model.addAttribute("courses", courses);
        model.addAttribute("faculties", faculties);
        return "admin/edit_subject"; // Make sure this matches your template location
    }
//-------------------------------STUDENT PAGES CONTROLLER--------------------------------------
    
 // Display the Register Student form
    @GetMapping("/register-student")
    public String showRegisterStudentForm(Model model) {
        model.addAttribute("courses", courseRepository.findAll());  // Needed for <select>
        return "admin/register_student"; // This matches the Thymeleaf page
    }
    //Display manage student page
    @GetMapping("/manage-students")
    public String manageStudents(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 5); // 5 students per page
        Page<Student> students = studentRepository.findAll(pageable);
        model.addAttribute("students", students);
        return "admin/manage_students";
    }

    //Displaying edit student form
    @GetMapping("/edit-student-form/{rollNumber}")
    public String openEditStudentForm(@PathVariable String rollNumber, Model model) {
        Optional<Student> optionalStudent = studentRepository.findByRollNumber(rollNumber);
        if (optionalStudent.isEmpty()) {
            model.addAttribute("error", "Student not found");
            return "redirect:/admin/manage-students";
        }

        Student student = optionalStudent.get();
        List<Course> courses = courseRepository.findAll();

        model.addAttribute("student", student);
        model.addAttribute("courses", courses);
        return "admin/edit_student_form"; // make sure this is your HTML path
    }
    
    @GetMapping("/assign-subjects/{rollNumber}")
    public String openAssignSubjectsPage(@PathVariable String rollNumber, Model model) {
        Optional<Student> optionalStudent = studentRepository.findByRollNumber(rollNumber);
        if (optionalStudent.isEmpty()) {
            model.addAttribute("error", "Student not found with roll number: " + rollNumber);
            return "redirect:/admin/manage-students";
        }

        Student student = optionalStudent.get();
       
        List<Subject> allSubjects = subjectRepository.findAll();
        List<Integer> assignedSubjectIds = student.getSubjects().stream()
                                                  .map(Subject::getId)
                                                  .toList();

        model.addAttribute("student", student);
        model.addAttribute("subjects", allSubjects);
        model.addAttribute("assignedSubjectIds", assignedSubjectIds);

        return "admin/assign_subjects_form"; // create this Thymeleaf file
    }
    
    @GetMapping("/delete-subjects/{rollNumber}")
    public String showDeleteSubjectsForm(@PathVariable String rollNumber, Model model) {
        Optional<Student> studentOpt = studentRepository.findByRollNumber(rollNumber);
        if (studentOpt.isEmpty()) {
            model.addAttribute("error", "Student not found");
            return "redirect:/admin/manage-students";
        }

        Student student = studentOpt.get();
        model.addAttribute("student", student);
        model.addAttribute("subjects", student.getSubjects());
        return "admin/delete_subjects_form";
    }





}


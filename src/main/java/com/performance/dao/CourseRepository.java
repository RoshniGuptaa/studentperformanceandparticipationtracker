package com.performance.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.performance.entities.Course;
import com.performance.entities.Faculty;

public interface CourseRepository extends JpaRepository<Course,Integer >{

	public Optional<Course> findByCourseCode(String courseCode);
	
	//@Query("SELECT c.faculty.id FROM Course c WHERE c.courseCode = :courseCode")
	//Integer findFacultyIdByCourseCode(@Param("courseCode") String courseCode);

	public Integer findFacultyIdByCourseCode(String courseCode);
	
	
}

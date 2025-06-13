package com.performance.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.performance.entities.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer>{
	
    List<Attendance> findBySubjectId(int subjectId);

	List<Attendance> findByStudentIdAndSubjectId(int studentId, int subjectId);
}

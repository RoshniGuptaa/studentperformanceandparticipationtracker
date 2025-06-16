package com.performance.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.performance.entities.Performance;

public interface PerformanceRepository extends JpaRepository<Performance, Integer> {

	public List<Performance> findByStudentId(int studentId) ;

	public Performance findByStudentIdAndSubjectId(int studentId,int subjectId);
		

}

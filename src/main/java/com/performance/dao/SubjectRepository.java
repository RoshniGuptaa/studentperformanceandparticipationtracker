package com.performance.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.performance.entities.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Integer>{

	Optional<Subject> findBySubjectCode(String subjectCode);
	

}

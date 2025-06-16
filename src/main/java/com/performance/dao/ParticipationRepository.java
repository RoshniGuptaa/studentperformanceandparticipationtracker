package com.performance.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.performance.entities.Participation;
import com.performance.entities.Student;

public interface ParticipationRepository extends JpaRepository<Participation, Integer>{

	 List<Participation> findByStudent(Student student);
}



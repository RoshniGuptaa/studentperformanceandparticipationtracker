package com.performance.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.performance.entities.Faculty;


public interface FacultyRepository extends JpaRepository<Faculty, Integer>{
	Optional<Faculty>findByEmail(String email);
}

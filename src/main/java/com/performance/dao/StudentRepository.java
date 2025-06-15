package com.performance.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.performance.entities.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {

	Optional<Student>findByRollNumber(String rollNumber);

	Optional<Student> findByUserId(int userId);
}

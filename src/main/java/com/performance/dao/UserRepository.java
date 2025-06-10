package com.performance.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.performance.entities.User;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer>{
	

	 Optional<User> findByUsername(String username);
}

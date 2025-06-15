package com.performance.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.performance.entities.Performance;

public interface PerformanceRepository extends JpaRepository<Performance, Integer> {

}

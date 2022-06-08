package com.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.models.Log;

public interface LogRepository extends JpaRepository<Log, Long>{

}

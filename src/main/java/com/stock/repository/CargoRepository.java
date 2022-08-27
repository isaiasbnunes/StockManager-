package com.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.models.Cargo;

/**
 * 
 * @author isaias
 *
 */
public interface CargoRepository extends JpaRepository<Cargo, Long>{

	List<Cargo> findByActiveTrueOrderByNomeAsc();
}

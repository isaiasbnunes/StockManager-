package com.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.stock.models.Funcionario;

/**
 * 
 * @author isaias
 *
 */
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long>{

	List<Funcionario> findByOrderByNomeAsc();
	
	List<Funcionario> findByActiveTrueOrderByNomeAsc();
}

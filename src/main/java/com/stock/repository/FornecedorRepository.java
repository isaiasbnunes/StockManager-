package com.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.models.Fornecedor;

/**
 * 
 * @author isaias
 *
 */
public interface FornecedorRepository  extends JpaRepository<Fornecedor, Long>{
	
	List<Fornecedor> findByOrderByNomeFantasiaAsc();
	
}

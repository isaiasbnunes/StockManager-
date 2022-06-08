package com.stock.repository;


import org.springframework.data.jpa.repository.JpaRepository;


import com.stock.models.EntradaItens;

/**
 * 
 * @author isaias
 *
 */
public interface EntradaItensRepository extends JpaRepository<EntradaItens, Long>{

	
}
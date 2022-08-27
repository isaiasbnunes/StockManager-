package com.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.models.CategoriaProduto;

/**
 * 
 * @author isaias
 *
 */
public interface CategoriaProdutoRepository extends JpaRepository<CategoriaProduto, Long>{
	List<CategoriaProduto> findByActiveTrueOrderByNomeAsc();
}

package com.stock.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stock.models.Produto;

/**
 * 
 * @author isaias
 *
 */
public interface ProdutoRepository extends JpaRepository<Produto, Long>{

	
	@Query(value = "SELECT p FROM Produto p where p.nome = :nome ORDER BY p.nome ASC" )
	public List<Produto> findByName(@Param("nome") String nome);
	
	@Query(value = "SELECT p FROM Produto p where p.categoria.nome = :categoria and active = true ORDER BY p.nome ASC" )
	public List<Produto> findByCategory(@Param("categoria") String categoria);
	
	List<Produto> findByActiveTrueOrderByNomeAsc();
	
	List<Produto> findByOrderByNomeAsc();
}

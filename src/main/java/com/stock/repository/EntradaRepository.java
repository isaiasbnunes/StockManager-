package com.stock.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stock.models.Entrada;
import com.stock.models.Fornecedor;
import com.stock.models.Saida;


/**
 * 
 * @author isaias
 *
 */
public interface EntradaRepository extends JpaRepository<Entrada, Long>{


	@Query(value = "SELECT e FROM Entrada e where e.dataRecebimento BETWEEN :dataInicio AND :dataFim " )
	public List<Entrada> findByDateRecebimento(@Param("dataInicio") Date dataInicio, @Param("dataFim") Date dataFim);

	@Query(value = "SELECT e FROM Entrada e where e.fornecedor = :fornecedor and e.dataRecebimento BETWEEN :dataInicio AND :dataFim " )
	public List<Entrada> findByDateRecebimentoAndFornecedor(@Param("fornecedor") Fornecedor fornecedor , @Param("dataInicio") Date dataInicio, @Param("dataFim") Date dataFim);
	
}

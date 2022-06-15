package com.stock.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stock.models.Produto;
import com.stock.models.Saida;

public interface SaidaRepository extends JpaRepository<Saida, Long> {

	@Query(value = "SELECT s FROM Saida s where s.setor.nome = :setor and s.dataSaida BETWEEN :dataInicio AND :dataFim " )
	public List<Saida> findByDateAndSetor(@Param("setor") String setor, 
			      @Param("dataInicio") Date dataInicio, @Param("dataFim") Date dataFim);

	
	@Query(value = "SELECT s FROM Saida s where  s.dataSaida BETWEEN :dataInicio AND :dataFim " )
	public List<Saida> findByDate( 
			      @Param("dataInicio") Date dataInicio, @Param("dataFim") Date dataFim);
	
}

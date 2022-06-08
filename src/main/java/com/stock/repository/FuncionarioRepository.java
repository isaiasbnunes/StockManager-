package com.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.models.Funcionario;

/**
 * 
 * @author isaias
 *
 */
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long>{

}

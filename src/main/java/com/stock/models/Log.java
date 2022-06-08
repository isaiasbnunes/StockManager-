package com.stock.models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.stock.enums.Operacao;

@Entity
@Table(name = "log")
public class Log implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Lob
	private String dadosAntes;
	@Lob
	private String dadosDepois;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.TIMESTAMP) 
	private Date data = new java.sql.Date(System.currentTimeMillis());
	
	@Enumerated(EnumType.ORDINAL)
	private Operacao operacao;
	
	@ManyToOne
	private User user;
	
	public Log() {
		super();
	}

	/**
	 * 
	 * @param dadosAntes
	 * @param dadosDepois
	 * @param operacao
	 * @param user
	 */
	public Log(String dadosAntes, String dadosDepois, Operacao operacao, User user) {
		super();
		this.dadosAntes = dadosAntes;
		this.dadosDepois = dadosDepois;
		this.operacao = operacao;
		this.user = user;
	}
	
	/**
	 * 
	 * @param operacao
	 * @param user
	 */
	public Log(Operacao operacao, User user) {
		super();
		this.operacao = operacao;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDadosAntes() {
		return dadosAntes;
	}

	public void setDadosAntes(String dadosAntes) {
		this.dadosAntes = dadosAntes;
	}

	public String getDadosDepois() {
		return dadosDepois;
	}

	public void setDadosDepois(String dadosDepois) {
		this.dadosDepois = dadosDepois;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Operacao getOperacao() {
		return operacao;
	}

	public void setOperacao(Operacao operacao) {
		this.operacao = operacao;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
}

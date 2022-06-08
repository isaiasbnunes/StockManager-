package com.stock.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;


@Entity
@Table(name = "saida")
public class Saida implements Serializable{

	
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	private Date dataSaida;
	
	/**
	 *   Data que foi registrado no sistema 
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.TIMESTAMP) 
	private Date dataRegistro = new java.sql.Date(System.currentTimeMillis());
	
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="saida")
    private List<SaidaItens> saidaItens;
	
	/**
	 *  pra qual setor os insumos foram enviados
	 */
	@ManyToOne
	private Setor setor;
	
	@ManyToOne
	private User user;
	
	@ManyToOne
	private Funcionario funcionario;

	@ManyToOne
	private TipoSaida tipoSaida;
	
	private String observacao;
	
	@Column(columnDefinition = "boolean default false")
	private boolean atendida = false;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDataSaida() {
		return dataSaida;
	}

	public void setDataSaida(Date dataSaida) {
		this.dataSaida = dataSaida;
	}

	public Date getDataRegistro() {
		return dataRegistro;
	}

	public void setDataRegistro(Date dataRegistro) {
		this.dataRegistro = dataRegistro;
	}

	public Setor getSetor() {
		return setor;
	}

	public void setSetor(Setor setor) {
		this.setor = setor;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public TipoSaida getTipoSaida() {
		return tipoSaida;
	}

	public void setTipoSaida(TipoSaida tipoSaida) {
		this.tipoSaida = tipoSaida;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public List<SaidaItens> getSaidaItens() {
		return saidaItens;
	}

	public void setSaidaItens(List<SaidaItens> saidaItens) {
		this.saidaItens = saidaItens;
	}

	public Funcionario getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(Funcionario funcionario) {
		this.funcionario = funcionario;
	}

	public boolean isAtendida() {
		return atendida;
	}

	public void setAtendida(boolean atendida) {
		this.atendida = atendida;
	}

	@Override
	public String toString() {
		return "Saida [id=" + id + ", dataSaida=" + dataSaida + ", dataRegistro=" + dataRegistro + ", setor=" + setor
				+ ", user=" + user + ", funcionario=" + funcionario + ", tipoSaida=" + tipoSaida + "]";
	}

}

package com.stock.utils;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

public class Consumo {
	
	private String nomeCategoria;
	private Double valor;
	private Double quantidade;
	private String nomeProduto;
	private Double totalIten;
	private String UnidadeMedida;
	
	private String valorString;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date data;
	
	public String getNomeCategoria() {
		return nomeCategoria;
	}
	public void setNomeCategoria(String nomeCategoria) {
		this.nomeCategoria = nomeCategoria;
	}
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	public Double getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Double quantidade) {
		this.quantidade = quantidade;
	}
	
	public String getNomeProduto() {
		return nomeProduto;
	}
	public void setNomeProduto(String nomeProduto) {
		this.nomeProduto = nomeProduto;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	
	public String getValorString() {
		return valorString;
    }
	
	public String getUnidadeMedida() {
		return UnidadeMedida;
	}
	public void setUnidadeMedida(String unidadeMedida) {
		UnidadeMedida = unidadeMedida;
	}
	public Double getTotalIten() {
		return totalIten;
	}
	public void setTotalIten(Double totalIten) {
		this.totalIten = totalIten;
	}
	public void setValorString(double valor) {
		this.valorString = getNumberFormat(valor);
	}

	public String getNumberFormat(double valorReal) {
			
		Locale localeBR = new Locale("pt","BR");
		NumberFormat dinheiro = NumberFormat.getCurrencyInstance(localeBR);
		
		return dinheiro.format(valorReal);
	}

   
	
}

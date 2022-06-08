package com.stock.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.NumberFormat;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.data.domain.Sort;

import com.stock.enums.UnidadeMedida;
import com.stock.models.Produto;
import com.stock.repository.CategoriaProdutoRepository;
import com.stock.repository.ProdutoRepository;
import com.stock.utils.ReportGenerate;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * 
 * @author isaias
 *
 */
@Controller
public class ProdutoController {

	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private CategoriaProdutoRepository categoriaProdutoRepository; 
	
	ReportGenerate report = new ReportGenerate();
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/produto/listar")
	public ModelAndView listar() {
		ModelAndView mv = new ModelAndView("produtos/lista");
		mv.addObject("listaProduto", produtoRepository.findAll(Sort.by(Sort.Direction.ASC, "nome")));
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/produto/cadastrar")
	public ModelAndView cadastrar(Produto p) {
		ModelAndView mv = new ModelAndView("produtos/cadastro"); 
		mv.addObject("produto", p);
		mv.addObject("categorias",categoriaProdutoRepository.findAll());
		mv.addObject("listaUnidadeMedida", UnidadeMedida.values());
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER')")
	@PostMapping("/produtos/salvar")
	public ModelAndView salvar(@Valid Produto produto, BindingResult result) {
		if(result.hasErrors()) {
			return cadastrar(produto);
		}
		Produto p = produtoRepository.saveAndFlush(produto);
		
		if(p.getNome().equals(produto.getNome())) {
			ModelAndView mv = new ModelAndView("produtos/cadastro"); 
			mv.addObject("produto", new Produto());
			mv.addObject("msg", "success");
			mv.addObject("categorias",categoriaProdutoRepository.findAll());
			mv.addObject("listaUnidadeMedida", UnidadeMedida.values());
			return mv;
		}
		
		return cadastrar(new Produto());
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@GetMapping("/produtos/remover{id}")
	public ModelAndView remover(@PathVariable("id") Long id) {
		Optional<Produto> produto = produtoRepository.findById(id);
		produtoRepository.delete(produto.get());
		return listar();
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
	@GetMapping("/produtos/editar{id}")
	public ModelAndView editar(@PathVariable("id") Long id) {
		Optional<Produto> p = produtoRepository.findById(id);
		return cadastrar(p.get());
	}
	
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/produtos/posicao_estoque")
	public ModelAndView posicaoEstoque() {
		ModelAndView mv = new ModelAndView("relatorio/posicao-estoque");
		mv.addObject("listCategoria", categoriaProdutoRepository.findAll());
	   return mv;
	}
	
	
	@PostMapping(value = "/produtos/posicao_estoque_gerarPDF")
	public ResponseEntity<byte[]> generatePdf(String categoria, boolean todas) {
		List<Produto> produtoList;
		if(todas) {
			produtoList = produtoRepository.findAll(Sort.by(Sort.Direction.ASC, "nome"));	
		}else {
			produtoList = produtoRepository.findByCategory(categoria);	
		}
		JRBeanCollectionDataSource beanCollectionDataSource = 
				new JRBeanCollectionDataSource(produtoList, false);
		
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("total", calcTotalProduto(produtoList));

		return report.generatereportPDF(parameters, beanCollectionDataSource, "reports/produtos_estoque.jrxml");
	}
	
	private String calcTotalProduto(List<Produto> produtoList) {
		Produto produto = new Produto();
		
		double total = 0.;
		
		for(Produto p : produtoList) {
		    total += p.getEstoque() * p.getValor();
		}
		
		return produto.getNumberFormat(total);
	}
	
	
}







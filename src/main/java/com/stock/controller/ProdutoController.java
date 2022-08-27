package com.stock.controller;


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

import com.stock.enums.UnidadeMedida;
import com.stock.models.Produto;
import com.stock.repository.CategoriaProdutoRepository;
import com.stock.repository.ProdutoRepository;



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
	
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/produto/listar")
	public ModelAndView listar() {
		ModelAndView mv = new ModelAndView("produtos/lista");
		mv.addObject("listaProduto", produtoRepository.findByActiveTrueOrderByNomeAsc());
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/produto/cadastrar")
	public ModelAndView cadastrar(Produto p) {
		ModelAndView mv = new ModelAndView("produtos/cadastro"); 
		mv.addObject("produto", p);
		mv.addObject("categorias",categoriaProdutoRepository.findByActiveTrueOrderByNomeAsc());
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
		if(produto.isPresent()) {
			Produto p  = produto.get();
			p.setActive(false);
			produtoRepository.saveAndFlush(p);
		}
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
	
}







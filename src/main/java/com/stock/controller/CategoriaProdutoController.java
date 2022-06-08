package com.stock.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


import com.stock.models.CategoriaProduto;
import com.stock.repository.CategoriaProdutoRepository;

/**
 * 
 * @author isaias
 *
 */
@Controller
public class CategoriaProdutoController {


	@Autowired
	private CategoriaProdutoRepository categoriaProdutoRepository ;
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/categoriaProduto/listar")
	public ModelAndView listar() {
		ModelAndView mv = new ModelAndView("categoriaProduto/lista");
		mv.addObject("listaCategorias", categoriaProdutoRepository.findAll());
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/categoriaProduto/cadastrar")
	public ModelAndView cadastrar(CategoriaProduto c) {
		ModelAndView mv = new ModelAndView("categoriaProduto/cadastro");
		mv.addObject("categoria", c);
		
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER')")
	@RequestMapping(value = "/categoriaProduto/salvar",method=RequestMethod.POST)
	public ModelAndView salvar(@Valid CategoriaProduto categoria, BindingResult result) {
		
		if(result.hasErrors()) {
			return cadastrar(categoria);
		}
		
		CategoriaProduto c = categoriaProdutoRepository.saveAndFlush(categoria);
		if(c.getNome().equals(categoria.getNome())) {
			ModelAndView mv = new ModelAndView("categoriaProduto/cadastro");
			mv.addObject("categoria", new CategoriaProduto());
			mv.addObject("msg", "success");
			return mv;
		}
		
		return cadastrar(new CategoriaProduto());
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@GetMapping("/categoriaProduto/remover{id}")
	public ModelAndView remover(@PathVariable("id") Long id) {
		Optional<CategoriaProduto> c = categoriaProdutoRepository.findById(id);
		categoriaProdutoRepository.delete(c.get());
		return listar();
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
	@GetMapping("/categoriaProduto/editar{id}")
	public ModelAndView editar(@PathVariable("id") Long id) {
		Optional<CategoriaProduto> c = categoriaProdutoRepository.findById(id);
		return cadastrar(c.get());
	}
		
}

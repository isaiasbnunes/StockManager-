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


import com.stock.models.TipoEntrada;
import com.stock.repository.TipoEntradaRepository;


@Controller
public class TipoEntradaController {

	@Autowired
	private TipoEntradaRepository tipoEntradaRepository;
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/tipoEntrada/cadastrar")
	public ModelAndView cadastrar(TipoEntrada t) {
		ModelAndView mv = new ModelAndView("tipoEntrada/cadastro");
		mv.addObject("tipoEntrada", t);
		mv.addObject("listaTipoEntrada", tipoEntradaRepository.findAll());
		
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/tipoEntrada/listar")
	public ModelAndView listar() {
		ModelAndView mv = new ModelAndView("tipoEntrada/lista");
		mv.addObject("listaTipoEntrada", tipoEntradaRepository.findAll());
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER')")
	@PostMapping("/tipoEntrada/salvar")
	public ModelAndView salvar(@Valid TipoEntrada tipo, BindingResult result) {
		
		//System.out.println(result.getAllErrors());
		if(result.hasErrors()) {
			return cadastrar(tipo);
		}
		
		TipoEntrada t = tipoEntradaRepository.saveAndFlush(tipo);
		if(t.getNome().equals(tipo.getNome())) {
			ModelAndView mv = new ModelAndView("tipoEntrada/cadastro");
			mv.addObject("tipoEntrada", new TipoEntrada());
			mv.addObject("msg", "success");
			return mv;
		}
		
		return cadastrar(new TipoEntrada());
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@GetMapping("/tipoEntrada/remover{id}")
	public ModelAndView remover(@PathVariable("id") Long id) {
		Optional<TipoEntrada> t = tipoEntradaRepository.findById(id);
		tipoEntradaRepository.delete(t.get());
		return listar();
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
	@GetMapping("/tipoEntrada/editar{id}")
	public ModelAndView editar(@PathVariable("id") Long id) {
		Optional<TipoEntrada> c = tipoEntradaRepository.findById(id);
		return cadastrar(c.get());
	}
	
}

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

import com.stock.models.TipoSaida;
import com.stock.repository.TipoSaidaRepository;


@Controller
public class TipoSaidaController {


	@Autowired
	private TipoSaidaRepository tipoSaidaRepository;
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/tipoSaida/cadastrar")
	public ModelAndView cadastrar(TipoSaida t) {
		ModelAndView mv = new ModelAndView("tipoSaida/cadastro");
		mv.addObject("tipoSaida", t);
		mv.addObject("listaTipoSaida", tipoSaidaRepository.findAll());
		
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/tipoSaida/listar")
	public ModelAndView listar() {
		ModelAndView mv = new ModelAndView("tipoSaida/lista");
		mv.addObject("listaTipoSaida", tipoSaidaRepository.findAll());
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
	@PostMapping("/tipoSaida/salvar")
	public ModelAndView salvar(@Valid TipoSaida tipo, BindingResult result) {
		
		//System.out.println(result.getAllErrors());
		if(result.hasErrors()) {
			return cadastrar(tipo);
		}
		
		TipoSaida t = tipoSaidaRepository.saveAndFlush(tipo);
		if(t.getNome().equals(tipo.getNome())) {
			ModelAndView mv = new ModelAndView("tipoSaida/cadastro");
			mv.addObject("tipoSaida", new TipoSaida());
			mv.addObject("msg", "success");
			return mv;
		}
		
		return cadastrar(new TipoSaida());
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@GetMapping("/tipoSaida/remover{id}")
	public ModelAndView remover(@PathVariable("id") Long id) {
		Optional<TipoSaida> t = tipoSaidaRepository.findById(id);
		tipoSaidaRepository.delete(t.get());
		return listar();
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
	@GetMapping("/tipoSaida/editar{id}")
	public ModelAndView editar(@PathVariable("id") Long id) {
		Optional<TipoSaida> c = tipoSaidaRepository.findById(id);
		return cadastrar(c.get());
	}
	
}

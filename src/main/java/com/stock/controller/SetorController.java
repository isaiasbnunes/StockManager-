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

import com.stock.models.Setor;
import com.stock.repository.SetorRepository;

/**
 * 
 * @author isaias
 *
 */
@Controller
public class SetorController {

	@Autowired
	private SetorRepository setorRepository;
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/setor/listar")
	public ModelAndView listar() {
		ModelAndView mv = new ModelAndView("setor/lista");
		mv.addObject("listaSetor", setorRepository.findAll());
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/setor/cadastrar")
	public ModelAndView cadastrar(Setor s) {
		ModelAndView mv = new ModelAndView("setor/cadastro"); 
		mv.addObject("setor", s);
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER')")
	@PostMapping("/setor/salvar")
	public ModelAndView salvar(@Valid Setor s, BindingResult result) {
		if(result.hasErrors()) {
			return cadastrar(s);
		}
		Setor setor = setorRepository.saveAndFlush(s);
		
		if(s.getNome().equals(setor.getNome())) {
			ModelAndView mv = new ModelAndView("setor/cadastro"); 
			mv.addObject("setor", new Setor());
			mv.addObject("msg", "success");
			return mv;
		}
		
		return cadastrar(new Setor());
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@GetMapping("/setor/remover{id}")
	public ModelAndView remover(@PathVariable("id") Long id) {
		Optional<Setor> s = setorRepository.findById(id);
		setorRepository.delete(s.get());
		return listar();
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
	@GetMapping("/setor/editar{id}")
	public ModelAndView editar(@PathVariable("id") Long id) {
		Optional<Setor> s = setorRepository.findById(id);
		return cadastrar(s.get());
	}
	
}

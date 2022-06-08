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

import com.stock.models.Cargo;
import com.stock.repository.CargoRepository;

/**
 * 
 * @author isaias
 *
 */
@Controller
public class cargoController {

	@Autowired
	private CargoRepository cargoRepository;

	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/cargo/listar")
	public ModelAndView listar() {
		ModelAndView mv = new ModelAndView("cargo/lista");
		mv.addObject("listaCargo", cargoRepository.findAll());
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/cargo/cadastrar")
	public ModelAndView cadastrar(Cargo cargo) {
		ModelAndView mv = new ModelAndView("cargo/cadastro"); 
		mv.addObject("cargo", cargo);
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER')")
	@PostMapping("/cargo/salvar")
	public ModelAndView salvar(@Valid Cargo cargo, BindingResult result) {
		if(result.hasErrors()) {
			return cadastrar(cargo);
		}
		Cargo c = cargoRepository.saveAndFlush(cargo);
		
		if(c.getNome().equals(cargo.getNome())) {
			ModelAndView mv = new ModelAndView("cargo/cadastro"); 
			mv.addObject("cargo", new Cargo());
			mv.addObject("msg", "success");
			return mv;
		}
		
		return cadastrar(new Cargo());
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@GetMapping("/cargo/remover{id}")
	public ModelAndView remover(@PathVariable("id") Long id) {
		Optional<Cargo> cargo = cargoRepository.findById(id);
		cargoRepository.delete(cargo.get());
		return listar();
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
	@GetMapping("/cargo/editar{id}")
	public ModelAndView editar(@PathVariable("id") Long id) {
		Optional<Cargo> cargo = cargoRepository.findById(id);
		return cadastrar(cargo.get());
	}
	
}

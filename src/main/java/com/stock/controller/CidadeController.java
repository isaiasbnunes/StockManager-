package com.stock.controller;

import java.util.Optional;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.stock.enums.Operacao;
import com.stock.models.Cidade;
import com.stock.repository.CidadeRepository;
import com.stock.repository.EstadoRepository;
import com.stock.utils.LogService;

/**
 * 
 * @author isaias
 *
 */
@Controller
public class CidadeController {
	
	@Autowired
	private CidadeRepository cidadeRepository;
	
	@Autowired
	private EstadoRepository estadoRepository;
	
	@Autowired
	private LogService logService;
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/cidade/cadastrar")
	public ModelAndView cadastrar(Cidade c) {
		ModelAndView mv = new ModelAndView("cidade/cadastro");
		mv.addObject("cidade", c);
		mv.addObject("listaEstados", estadoRepository.findAll());
		
		return mv;
	}
	
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/cidade/listar")
	public ModelAndView listar() {
		ModelAndView mv = new ModelAndView("cidade/lista");
		mv.addObject("listaCidades", cidadeRepository.findAll());
		return mv;
	}
	
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER')")
	@PostMapping("/cidade/salvar")
	public ModelAndView salvar(Cidade cidade) {
		
		if(cidade.getId() != null) {
			Cidade c = cidadeRepository.getById(cidade.getId());
			logService.save(c.toString(),cidade.toString(), Operacao.EDIT);
		}else {
			logService.save("", cidade.toString(), Operacao.SAVE);
		}
		
		Cidade c = cidadeRepository.saveAndFlush(cidade);
		if(cidade.getNome().equals(c.getNome())) {
			ModelAndView mv = new ModelAndView("cidade/cadastro");
			mv.addObject("cidade", new Cidade());
			mv.addObject("listaEstados", estadoRepository.findAll());
			mv.addObject("msg", "success");
			return mv;
		}
		
		return cadastrar(new Cidade());
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@GetMapping("/cidade/remover{id}")
	public ModelAndView remover(@PathVariable("id") Long id) {
		Optional<Cidade> c = cidadeRepository.findById(id);
		if(c.isPresent()) {
			cidadeRepository.delete(c.get());
			logService.save(c.get().toString(), "", Operacao.DELETE);
		}
		return listar();
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
	@GetMapping("/cidade/editar{id}")
	public ModelAndView editar(@PathVariable("id") Long id) {
		
		Optional<Cidade> c = cidadeRepository.findById(id);
		return cadastrar(c.get());
	}
}



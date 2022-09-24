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

import com.stock.enums.Operacao;
import com.stock.models.Fornecedor;
import com.stock.repository.CidadeRepository;
import com.stock.repository.FornecedorRepository;
import com.stock.utils.LogService;

/**
 * 
 * @author isaias
 *
 */
@Controller
public class FornecedorController {

	@Autowired
	private FornecedorRepository fornecedorRepository;
	
	@Autowired
	private CidadeRepository cidadeRepository;
	
	@Autowired
	private LogService logService;
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/fornecedor/cadastrar")
	public ModelAndView cadastrar(Fornecedor fornecedor) {
		ModelAndView mv = new ModelAndView("fornecedor/cadastro");
		mv.addObject("fornecedor", fornecedor);
		mv.addObject("listaCidades", cidadeRepository.findAll());
		
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/fornecedor/listar")
	public ModelAndView listar() {
		ModelAndView mv = new ModelAndView("fornecedor/lista");	
		mv.addObject("listaFornecedores", fornecedorRepository.findByActiveTrueOrderByNomeFantasiaAsc());
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER')")
	@PostMapping("/fornecedor/salvar")
	public ModelAndView salvar(@Valid Fornecedor fornecedor, BindingResult result) {
		
		if(result.hasErrors()) {
			return cadastrar(fornecedor);
		}
		
		if(fornecedor.getId() != null) {
			Optional<Fornecedor> f = fornecedorRepository.findById(fornecedor.getId());
			logService.save(f.get().toString(), fornecedor.toString(), Operacao.EDIT);
		}else {
			logService.save("", fornecedor.toString(), Operacao.SAVE);
		}
		
		Fornecedor f = fornecedorRepository.saveAndFlush(fornecedor);
		if(f.getNomeFantasia().equals(fornecedor.getNomeFantasia())) {
			ModelAndView mv = new ModelAndView("fornecedor/cadastro");
			mv.addObject("fornecedor", new Fornecedor());
			mv.addObject("listaCidades", cidadeRepository.findAll());
			mv.addObject("msg", "success");
			return mv;
		}
		
		return cadastrar(new Fornecedor());
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
	@GetMapping("fornecedor/editar/{id}")
	public ModelAndView editar(@PathVariable("id") Long id ) {
		Optional<Fornecedor> f = fornecedorRepository.findById(id);
		return cadastrar(f.get());
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@GetMapping("fornecedor/remover/{id}")
	public ModelAndView remover(@PathVariable("id") Long id) {
		Optional<Fornecedor> f = fornecedorRepository.findById(id);
		if(f.isPresent()) {
			Fornecedor forne = f.get();
			forne.setActive(false);
			fornecedorRepository.save(forne);
			logService.save(forne.toString(), "", Operacao.DELETE);
		}
		return listar();
	}
	
	
}

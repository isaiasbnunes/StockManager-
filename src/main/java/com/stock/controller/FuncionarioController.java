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

import com.stock.models.Cidade;
import com.stock.models.Funcionario;
import com.stock.repository.CargoRepository;
import com.stock.repository.CidadeRepository;
import com.stock.repository.EstadoRepository;
import com.stock.repository.FuncionarioRepository;
import com.stock.repository.SetorRepository;

/**
 * 
 * @author isaias
 *
 */
@Controller
public class FuncionarioController {
	
	@Autowired
	private CidadeRepository cidadeRepository;
	
	@Autowired
	private CargoRepository cargoRepository;
	
	@Autowired
	private FuncionarioRepository funcionarioRepository;
	
	@Autowired
	private EstadoRepository estadoRepository;
	
	@Autowired
	private SetorRepository setorRepository;
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/funcionarios/cadastrar")
	public ModelAndView cadastrar(Funcionario funcionario) {
		ModelAndView mv =  initModelAndView(funcionario);
		return mv;
	}

	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER')")
	@PostMapping("/funcionarios/cidade")
	private ModelAndView salvarCidade(@Valid Cidade cidade) {
		cidadeRepository.saveAndFlush(cidade);
	    return	initModelAndView(new Funcionario());
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER')")
	@PostMapping("/funcionarios/salvar")
	public ModelAndView salvar(@Valid Funcionario funcionario) {
		
			Funcionario f = funcionarioRepository.saveAndFlush(funcionario);
			if(f.getNome().equals(funcionario.getNome())) {
				
				ModelAndView mv = initModelAndView(new Funcionario());
				mv.addObject("msg", "success");
				return mv;
			}
		
		return cadastrar(new Funcionario());
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/funcionarios/listar")
	public ModelAndView listar() {
		ModelAndView mv=new ModelAndView("funcionarios/lista");
		mv.addObject("listaFuncionarios", funcionarioRepository.findAll());
		return mv;
	}
	
	private ModelAndView initModelAndView(Funcionario funcionario) {
		ModelAndView mv =  new ModelAndView("funcionarios/cadastro");
		mv.addObject("funcionario", funcionario);
		mv.addObject("cidade", new Cidade());
		mv.addObject("listaCidades", cidadeRepository.findAll());
		mv.addObject("listaCargos", cargoRepository.findAll());
		mv.addObject("listaEstados", estadoRepository.findAll());
		mv.addObject("listaSetor", setorRepository.findAll());
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@GetMapping("/funcionarios/remover/{id}")
	public ModelAndView remover(@PathVariable("id") Long id) {
		Optional<Funcionario> funcionario = funcionarioRepository.findById(id);
		funcionarioRepository.delete(funcionario.get());
		return listar();
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
	@GetMapping("/funcionarios/editar/{id}")
	public ModelAndView editar(@PathVariable("id") Long id ) {
		Optional<Funcionario> funcionario = funcionarioRepository.findById(id);
		return cadastrar(funcionario.get());
	}
}

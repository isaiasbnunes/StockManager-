package com.stock.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.stock.MyUserDetails;
import com.stock.models.Entrada;
import com.stock.models.EntradaItens;
import com.stock.models.Produto;
import com.stock.models.User;
import com.stock.repository.EntradaRepository;
import com.stock.repository.FornecedorRepository;
import com.stock.repository.FuncionarioRepository;
import com.stock.repository.ProdutoRepository;
import com.stock.repository.TipoEntradaRepository;
import com.stock.repository.UserRepository;

/**
 * 
 * @author isaias
 *
 */
@Controller
public class EntradaController {

	private List<EntradaItens> listaEntrada = new ArrayList<EntradaItens>();
	
	private Double total = 0.;
	
	
	@Autowired
	private FornecedorRepository fornecedorRepository;
	
	@Autowired
	private FuncionarioRepository funcionarioRepository;
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private EntradaRepository entradaRepository;
	
	@Autowired
	private TipoEntradaRepository tipoEntradaRepository;
	
	@Autowired
	private UserRepository userRepository;	
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/entrada/cadastrar")
	public ModelAndView cadastrar(Entrada entrada, EntradaItens entradaItens) {
		System.out.println(">>> page entrada...");
		System.out.println(">>> Total..."+ total);
		ModelAndView mv = new ModelAndView("entrada/cadastro");
		mv.addObject("entrada", entrada);
		mv.addObject("listaEntradaItens", this.listaEntrada);
		mv.addObject("entradaItens", entradaItens);
		mv.addObject("total", total);
		mv.addObject("listaFuncionarios", funcionarioRepository.findAll());
		mv.addObject("listaProduto", produtoRepository.findAll());
		mv.addObject("listaFornecedor", fornecedorRepository.findAll());
		mv.addObject("listTipoEntrada", tipoEntradaRepository.findAll());
		
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER')")
	@PostMapping("/entrada/salvar")
	public ModelAndView salvar(String acao, Entrada entrada, EntradaItens entradaItens)  {

		if(acao.equals("itens")) {
			System.out.println( ">>>>>>> Produto" + entradaItens.getProduto());
			this.listaEntrada.add(entradaItens);
			total = total + entradaItens.getValor();
		
		}else if(acao.equals("salvar")) {
			
			entrada.setUser(getUserLogin());
			entrada.setEntradaItens(listaEntrada);
			entradaRepository.saveAndFlush(entrada);
			for(EntradaItens it : listaEntrada) {
				it.setEntrada(entrada);
				//entradaItensRepository.saveAndFlush(it);
				Optional<Produto> prod = produtoRepository.findById(it.getProduto().getId());
				Produto produto = prod.get();
				produto.setValor(it.getValor() / it.getQuantidade() );
				produto.setEstoque(produto.getEstoque() + it.getQuantidade());
				produtoRepository.saveAndFlush(produto);
				total = 0.;
				this.listaEntrada = new ArrayList<>();
			}
			return cadastrar(new Entrada(), new EntradaItens());
			
		}else if( !acao.equals("itens") &&  !acao.equals("salvar")) {	
		    //remover itens da lista
			for(EntradaItens i : listaEntrada) {
				if(i.getProduto().getNome().equals(acao)) {	
					 total = total - i.getValor();
					 listaEntrada.remove(i);
					 return cadastrar(entrada, new EntradaItens());
				}
			}
	   }
		return cadastrar(entrada, new EntradaItens());
    }
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/entrada/listar")
	public ModelAndView listar() {
		ModelAndView mv = new ModelAndView("entrada/lista");
		mv.addObject("listaEntradas", entradaRepository.findAll(Sort.by(Sort.Direction.DESC, "dataRecebimento")));
		return mv;
	}
	
	@GetMapping("/entrada/cancelar")
	public ModelAndView cancelar() {
		total = 0.;
		listaEntrada = new ArrayList<EntradaItens>();
		return cadastrar(new Entrada(), new EntradaItens());
	}
	
	private Double totalItens(List<EntradaItens> list) {
		Double total = 0.;
		for(EntradaItens e : list) {
			total = total + e.getValor();
		}
		
		return total;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/entrada/visualizar{id}")
	public ModelAndView editar(@PathVariable("id") Long id) {
		ModelAndView mv = new ModelAndView("entrada/visualizar");
		
		Optional<Entrada> e = entradaRepository.findById(id);
		
		mv.addObject("observacao", e.get().getObservacao()); 
		mv.addObject("numeroNota", e.get().getNumeroNota()); 
		mv.addObject("id", e.get().getId()); 
		mv.addObject("tipoEntrada", e.get().getTipoEntrada()); 
		mv.addObject("dataRecebimento", e.get().getDataRecebimento()); 
		mv.addObject("dataRegistro", e.get().getDataRegistro()); 
		mv.addObject("funcionario", e.get().getFuncionario());  
		mv.addObject("login", e.get().getUser());  
		mv.addObject("fornecedor", e.get().getFornecedor());  
		mv.addObject("listaEntradaItens", e.get().getEntradaItens());
		mv.addObject("total", totalItens(e.get().getEntradaItens()));
		return mv;
	}
	
	private User getUserLogin() {
		
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String user;    

		if (principal instanceof MyUserDetails) {
		    user = (( MyUserDetails)principal).getUsername();
		} else {
		    user = principal.toString();
		}
		
		User userByBd = userRepository.getByUsername(user);
		
		if(userByBd != null) {
			return userByBd;
		}
		
		return null;
	}

	

}









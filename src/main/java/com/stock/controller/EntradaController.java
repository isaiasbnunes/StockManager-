package com.stock.controller;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.stock.models.Fornecedor;
import com.stock.models.Produto;
import com.stock.models.User;
import com.stock.repository.EntradaRepository;
import com.stock.repository.FornecedorRepository;
import com.stock.repository.FuncionarioRepository;
import com.stock.repository.ProdutoRepository;
import com.stock.repository.TipoEntradaRepository;
import com.stock.repository.UserRepository;
import com.stock.utils.UserLogin;

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
	private UserLogin userLogin;
	
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
		
		ModelAndView mv = new ModelAndView("entrada/cadastro");
		mv.addObject("entrada", entrada);
		mv.addObject("listaEntradaItens", this.listaEntrada);
		mv.addObject("entradaItens", entradaItens);
		mv.addObject("total", total);
		mv.addObject("listaFuncionarios", funcionarioRepository.findByActiveTrueOrderByNomeAsc());
		mv.addObject("listaProduto", produtoRepository.findByActiveTrueOrderByNomeAsc());
		mv.addObject("listaFornecedor", fornecedorRepository.findByActiveTrueOrderByNomeFantasiaAsc());
		mv.addObject("listTipoEntrada", tipoEntradaRepository.findAll());
		
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER')")
	@PostMapping("/entrada/salvar")
	public ModelAndView salvar(String acao, Entrada entrada, EntradaItens entradaItens)  {

		if(acao.equals("itens") && entradaItens.getProduto() != null ) {
			this.listaEntrada.add(entradaItens);
			total = total + entradaItens.getValor();
		}else if(acao.equals("salvar")) {
			
			entrada.setUser(userLogin.getUserLogin());
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
	public ModelAndView listar(String fornecedor, String dataInicio, String dataFim, boolean todas) throws ParseException {
		
		if(fornecedor == null || dataInicio == null || dataFim == null ) {
			ModelAndView mv = new ModelAndView("entrada/pesquisar");
			mv.addObject("listaFornecedor", fornecedorRepository.findByActiveTrueOrderByNomeFantasiaAsc());
			return mv;
		}
		
		Date inicio = new SimpleDateFormat("yyyy-MM-dd").parse(dataInicio);
		Date fim = new SimpleDateFormat("yyyy-MM-dd").parse(dataFim);
		
		if(todas) {
			ModelAndView mv = new ModelAndView("entrada/lista");
			mv.addObject("listaEntradas",  entradaRepository.findByDateRecebimento(inicio, fim));
			return mv;
		}
		
		Fornecedor f = fornecedorRepository.findById(Long.parseLong(fornecedor.trim())).get();
		
		ModelAndView mv = new ModelAndView("entrada/lista");
		mv.addObject("listaEntradas", entradaRepository.findByDateRecebimentoAndFornecedor(f, 
				inicio,  fim));
		return mv;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/entrada/pesquisar")
	public ModelAndView pesquisar() {
		ModelAndView mv = new ModelAndView("entrada/pesquisar");
		mv.addObject("listaFornecedor", fornecedorRepository.findByActiveTrueOrderByNomeFantasiaAsc());
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

}









package com.stock.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;


import com.stock.enums.Operacao;
import com.stock.models.Log;
import com.stock.models.Produto;
import com.stock.models.Saida;
import com.stock.models.SaidaItens;
import com.stock.repository.CategoriaProdutoRepository;
import com.stock.repository.FuncionarioRepository;
import com.stock.repository.LogRepository;
import com.stock.repository.ProdutoRepository;
import com.stock.repository.SaidaItensRepository;
import com.stock.repository.SaidaRepository;
import com.stock.repository.SetorRepository;
import com.stock.repository.TipoSaidaRepository;
import com.stock.utils.UserLogin;




/**
 * 
 * @author isaias
 *
 */
@Controller
public class SaidaController {

    private List<SaidaItens> listaSaidas = new ArrayList<SaidaItens>();
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private TipoSaidaRepository tipoSaidaRepository;
	
	@Autowired
	private FuncionarioRepository funcionarioRepository;
	
	@Autowired
	private SaidaRepository saidaRepository;
	
	@Autowired
	private SetorRepository setorRepository;
	
	@Autowired
	private LogRepository logRepository;
	
	@Autowired
	private SaidaItensRepository saidaItensRepository;
	
	private Log log;
	
	@Autowired
	private UserLogin userLogin;
	
	
	Produto produto = new Produto();
	
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/saida/cadastrar")
	public ModelAndView cadastrar(Saida saida, SaidaItens saidaItens, String msg) {
		
		return inicializarModel(saida, saidaItens, msg);
	}
	
	private ModelAndView inicializarModel(Saida saida, SaidaItens saidaItens, String msg) {
		ModelAndView mv = new ModelAndView("saida/cadastro");
		mv.addObject("msg", msg);
		mv.addObject("saida", saida);
		mv.addObject("listaSaidaItens", this.listaSaidas);
		mv.addObject("saidaItens", saidaItens);
		mv.addObject("listTipoEntrada", tipoSaidaRepository.findAll());
		mv.addObject("listaProdutos", produtoRepository.findByActiveTrueOrderByNomeAsc());
		mv.addObject("listaFuncionarios", funcionarioRepository.findByActiveTrueOrderByNomeAsc());
		mv.addObject("listaSetor", setorRepository.findAll());
		
		return mv;
	}
	
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/saida/listar")
	public ModelAndView listar() {
		ModelAndView mv = new ModelAndView("saida/lista");
		
		mv.addObject("listaSaidas", saidaRepository.findAll(Sort.by(Sort.Direction.DESC, "dataSaida")));
		return mv;
	}
	
	//Verifica se o produto existe na lista
	private boolean containsItensListsaida(SaidaItens saidaItens) {
		for(SaidaItens s : listaSaidas) {
			if(s.getProduto().getNome().equals(saidaItens.getProduto().getNome())) {
				return true;
			}
		}
		return false;
	}
	
	//Verificar se item não está zerado no estoque
	private boolean verificarItemEstoque(SaidaItens saidaItens) {
		Optional<Produto> p = produtoRepository.findById(saidaItens.getProduto().getId());
		double quant = p.get().getEstoque();
		
		if(quant <= 0 || 0 > (quant - saidaItens.getQuantidade())) {
			return true;
		}
		
		return false;
	}
	
	/*
	 *  Verificar estoque zerado antes de salvar
	 *  Verifica cada item da lista antes de salvar
	 *  Isso se faz necessario por poder ter varias saídas ainda não aprovadas
	 */
	private boolean checkStockBeforeSave() {
		
		for(SaidaItens s : listaSaidas ) {  
			   return verificarItemEstoque(s);
		}
		
		return false;
	}
	
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER')")
	@PostMapping("/saida/salvar")
	public ModelAndView salvar(String acao, Saida saida, SaidaItens saidaItens)  {
        
		if(acao.equals("itens")) {
			
			 if(verificarItemEstoque(saidaItens)) {
				 return inicializarModel(saida, saidaItens, "Item com estoque zerado ou insuficiente!");
			 }else {
				
				 if(containsItensListsaida(saidaItens)) {
					 return inicializarModel(saida, saidaItens, "Já existe esse item na lista!");
				 }
				 
				 this.listaSaidas.add(saidaItens);
			 }
		}else if(acao.equals("salvar")) {
			
			if(listaSaidas.isEmpty()) {
				return inicializarModel(saida, saidaItens, "Adicione ao menos um item antes de salvar!");
			}
			
				saida.setUser(userLogin.getUserLogin());
				saidaRepository.saveAndFlush(saida);
			
				 for(SaidaItens it : listaSaidas) { 
					 it.setSaida(saida);
				 	 saidaItensRepository.saveAndFlush(it); 
				 }
				 
				this.listaSaidas = new ArrayList<>();
				
				logSave("", saida.toString(), Operacao.SAVE);
				return cadastrar(new Saida(), new SaidaItens(),"");
		
		}else if( !acao.equals("itens") &&  !acao.equals("salvar")) {	
		
			for(SaidaItens i : listaSaidas) {
				if(i.getProduto().getNome().equals(acao)) {	
					 listaSaidas.remove(i);
					 return cadastrar(saida, new SaidaItens(),"");
				}
			}
	   }
		return cadastrar(saida, new SaidaItens(),"");
    }
	
	
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER')")
	@PostMapping("/saida/editSaida")
	public ModelAndView editSave(String acao, Saida saida, SaidaItens saidaItens)  {
        
		if(acao.equals("itens")) {
			Optional<Produto> p = produtoRepository.findById(saidaItens.getProduto().getId());
			 double quant = p.get().getEstoque();
			 if(quant <= 0 || 0 > (quant - saidaItens.getQuantidade())) {
				
				 return inicializarModel(saida, saidaItens, "estoq");
			 }else {
				 this.listaSaidas.add(saidaItens);
			 }
		}else if(acao.equals("salvar")) {
			
			saida.setUser(userLogin.getUserLogin());
			saidaRepository.saveAndFlush(saida);
		
			 for(SaidaItens it : listaSaidas) { 
				 it.setSaida(saida);
			 	 saidaItensRepository.saveAndFlush(it); 
			 }
			 
			this.listaSaidas = new ArrayList<>();
			
			logSave("", saida.toString(), Operacao.EDIT);
			
			return cadastrar(new Saida(), new SaidaItens(),"");
			
		}else if( !acao.equals("itens") &&  !acao.equals("salvar")) {	
		
			for(SaidaItens i : listaSaidas) {
				if(i.getProduto().getNome().equals(acao)) {	
					 listaSaidas.remove(i);
					 saidaItensRepository.deleteById(i.getId());
					 return cadastrar(saida, new SaidaItens(),"");
				}
			}
	   }
		return cadastrar(saida, new SaidaItens(),"");
    }
	
	
	
	//Atender saida salvar itens e baixa no estoque
	@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
	@PostMapping("/saida/atendsaida")
	public ModelAndView atenderSaidaSave(String acao, Saida saida, SaidaItens saidaItens)  {
       
		if(!saida.isAtendida()) {
			if(acao.equals("itens")) {
				
				 if(verificarItemEstoque(saidaItens)) {
					 return inicializarModel(saida, saidaItens, "estoq");
				 }else {
					
					 if(containsItensListsaida(saidaItens)) {
						 return inicializarModel(saida, saidaItens, "Existem itens repetidos na lista!");
					 }
					 
					 this.listaSaidas.add(saidaItens);
				 }
				
			}else if(acao.equals("salvar")) {
				
				if(!checkStockBeforeSave()) {
				
					saida.setUser(userLogin.getUserLogin());
					saida.setAtendida(true);
					saida.setSaidaItens(listaSaidas);
					saidaRepository.saveAndFlush(saida);
					for(SaidaItens it : listaSaidas) {
						Optional<Produto> prod = produtoRepository.findById(it.getProduto().getId());
						Produto produto = prod.get();
						produto.setEstoque(produto.getEstoque() - it.getQuantidade());
						produtoRepository.saveAndFlush(produto);
					}
					this.listaSaidas = new ArrayList<>();
					
					logSave("", saida.toString(), Operacao.ATENDEU_REQ);
					
					return cadastrar(new Saida(), new SaidaItens(),"");
				}else {
					return cadastrar(saida, saidaItens,"Existem itens com valores zerados ou insuficientes!");
				}
				
			}else if( !acao.equals("itens") &&  !acao.equals("salvar")) {	
			
				for(SaidaItens i : listaSaidas) {
					if(i.getProduto().getNome().equals(acao)) {	
						 listaSaidas.remove(i);
						 return cadastrar(saida, new SaidaItens(),"");
					}
				}
		   }
        }
		
		return cadastrar(saida, new SaidaItens(),"");
    }
	
	
	
	//Atender Requisição
	@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
	@GetMapping("/saida/atenderSaida{id}")
	public ModelAndView atender(@PathVariable("id") Long id) {
		ModelAndView mv; 
		
		Optional<Saida> s = saidaRepository.findById(id);
		if(s.get().isAtendida()) {
			mv = new ModelAndView("saida/lista");  
			mv.addObject("msg","isAtendida");
			mv.addObject("listaSaidas", saidaRepository.findAll());
		}else {
			mv = new ModelAndView("saida/atender");  
			this.listaSaidas = s.get().getSaidaItens();
			mv.addObject("saidaItens", new SaidaItens());
			mv.addObject("saida", s.get()); 
			mv.addObject("listaSaidaItens", this.listaSaidas);
			mv.addObject("listaSaidaItens", s.get().getSaidaItens());
			mv.addObject("listaSetor", setorRepository.findAll());
			mv.addObject("listTipoEntrada", tipoSaidaRepository.findAll());
			mv.addObject("listaProdutos", produtoRepository.findAll());
			mv.addObject("listaFuncionarios", funcionarioRepository.findAll());
		}
		
		return mv;
	}
	
	
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER')")
	@GetMapping("/saida/edit{id}")
	public ModelAndView edit(@PathVariable("id") Long id) {
		ModelAndView mv = new ModelAndView("saida/editar");
			
		Optional<Saida> s = saidaRepository.findById(id);
			
		if(s.get().isAtendida()) {
			mv = new ModelAndView("saida/lista");  
			mv.addObject("msg","isAtendida");
			mv.addObject("listaSaidas", saidaRepository.findAll());
		}else {
			
			this.listaSaidas = s.get().getSaidaItens();
			mv.addObject("saidaItens", new SaidaItens());
			mv.addObject("saida", s.get()); 
			mv.addObject("listaSaidaItens", this.listaSaidas);
			mv.addObject("listaSaidaItens", s.get().getSaidaItens());
			mv.addObject("listaSetor", setorRepository.findAll());
			mv.addObject("listTipoEntrada", tipoSaidaRepository.findAll());
			mv.addObject("listaProdutos", produtoRepository.findAll());
			mv.addObject("listaFuncionarios", funcionarioRepository.findAll());
		}
			return mv;
	}
		
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/saida/visualizar{id}")
	public ModelAndView visualizar(@PathVariable("id") Long id) {
		ModelAndView mv = new ModelAndView("saida/visualizar");
		
		Optional<Saida> s = saidaRepository.findById(id);
		
		mv.addObject("id", s.get().getId()); 
		mv.addObject("observacao", s.get().getObservacao()); 
		mv.addObject("dataRegistro", s.get().getDataRegistro()); 
		mv.addObject("dataSaida", s.get().getDataSaida()); 
		mv.addObject("funcionario", s.get().getFuncionario()); 
		mv.addObject("user", s.get().getUser()); 
		mv.addObject("setor", s.get().getSetor()); 	
		mv.addObject("tipoSaida", s.get().getTipoSaida());  
		mv.addObject("listaSaidaItens", s.get().getSaidaItens());
		  
		return mv;
	}
	

	@GetMapping("/saida/cancelar")
	public ModelAndView cancelar() {
		listaSaidas = new ArrayList<SaidaItens>();
		return cadastrar(new Saida(), new SaidaItens(),"");
	}
	
	private void logSave(String dadosAntes, String dadosDepois, Operacao o) {
		Log log = new Log(dadosAntes, dadosDepois, Operacao.SAVE, userLogin.getUserLogin());
		logRepository.save(log);
	}
}







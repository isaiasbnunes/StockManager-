package com.stock.controller;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.stock.models.CategoriaProduto;
import com.stock.models.Produto;
import com.stock.models.Saida;
import com.stock.models.SaidaItens;
import com.stock.repository.CategoriaProdutoRepository;
import com.stock.repository.ProdutoRepository;
import com.stock.repository.SaidaRepository;
import com.stock.repository.SetorRepository;
import com.stock.utils.Consumo;
import com.stock.utils.ReportGenerate;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;


/**
 * 
 * @author isaias
 *
 */
@Controller
public class RelatorioSaidaController {

	
	private double valorTotalProdutos = 0.;
	private double totalItens = 0.;
	Produto produto = new Produto();
	
	ReportGenerate report = new ReportGenerate();
	
	@Autowired
	private SaidaRepository saidaRepository;
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private CategoriaProdutoRepository categoriaRepository;
	
	@Autowired
	private SetorRepository setorRepository;
	
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/relatorio/consumo_categoria")
	public ModelAndView consumo() {
		ModelAndView mv = new ModelAndView("relatorio/consumo-categoria");
		
	   return mv;
	}
	
	
	//Gerar relatorio  - Tela
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/relatorio/consumo_produto")
	public ModelAndView consumoProduto() {
		ModelAndView mv = new ModelAndView("relatorio/consumo-por-produto");
		mv.addObject("listCategoria", categoriaRepository.findAll());
		mv.addObject("listSetor", setorRepository.findAll());
		return mv;
	}
	
	
	//Consumo por categoria
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@PostMapping("/relatorio/consumo_pesquisar")
	public ModelAndView pesquisarConsumoCategoria(String dataInicio, String dataFim) throws ParseException {
		ModelAndView mv = new ModelAndView("relatorio/consumo-categoria");
		List<Consumo> list = consumoCategoria(new SimpleDateFormat("yyyy-MM-dd").parse(dataInicio),
		new SimpleDateFormat("yyyy-MM-dd").parse(dataFim));	
		mv.addObject("listConsumo", list);
		return mv;
	}
		

	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@PostMapping("/relatorio/consumo_produto_gerarPDF")
	public ResponseEntity<byte[]> pesquisarConsumoPorProduto(String dataInicio, String dataFim, String categoria,
			String setor, boolean todas, boolean todosSetores, boolean zero)
			throws ParseException {
			
		    List<Consumo> list =	consumoProduto(new SimpleDateFormat("yyyy-MM-dd").parse(dataInicio),
				new SimpleDateFormat("yyyy-MM-dd").parse(dataFim), categoria, setor, todas, todosSetores, zero);
	
	   return generatePdf(list, dataInicio, dataFim);
	}
	

	public ResponseEntity<byte[]> generatePdf(List<Consumo> list, String dataInicio, String dataFim) throws ParseException {
		
		JRBeanCollectionDataSource beanCollectionDataSource = 
				new JRBeanCollectionDataSource(list, false);
		
		Map<String, Object> parameters = new HashMap<>();

		LocalDate dtInicio = LocalDate.parse(dataInicio);
		LocalDate dtFim = LocalDate.parse(dataFim);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");	
		
		parameters.put("dataInicio", dtInicio.format(formatter));
		parameters.put("dataFim", dtFim.format(formatter));
		parameters.put("total", produto.getNumberFormat( valorTotalProdutos));
	    DecimalFormat df = new DecimalFormat("#.###");
		parameters.put("totalItens", df.format(totalItens));
		
		
		return report.generatereportPDF(parameters, beanCollectionDataSource, "reports/consumo_por_produto.jrxml");
	}
	
	
	private List<Consumo> consumoProduto(Date dataInicio, Date dataFim, String categoria,
			String setor, boolean todas, boolean todosSetores, boolean zero) {
		
		List<Consumo> listConsumo =  new ArrayList<>(); 
		//List<Saida> saidas = saidaRepository.findByDate(dataInicio, dataFim);
		//List<Saida> saidas = saidaRepository.findByDateAndSetor(setor, dataInicio, dataFim);
		
		List<Saida> saidas = findSaidaBySetorAndDate(dataInicio, dataFim, setor, todosSetores);
		List<Produto> listProduto;
		
		if(todas) {
			 listProduto = produtoRepository.findAll();	
		}else {
			 listProduto = produtoRepository.findByCategory(categoria);
		}
		
		Consumo consumo;
		valorTotalProdutos = 0.;
		totalItens = 0.;
	    double quantidade = 0.;
	    double valor = 0.;
	   
	    for(Produto p : listProduto) {
	    	valor = 0.;
		    quantidade = 0.;
		    for (Saida s : saidas) {
		    	for(SaidaItens iten : s.getSaidaItens()) {
		    		if(p.getNome().equals(iten.getProduto().getNome())) {
		    			quantidade = quantidade + iten.getQuantidade(); 
		    			valor += iten.getQuantidade() * iten.getProduto().getValor();
		    		}
		    	}
		    }
		    consumo = new Consumo();
		    //permite valores zerados
		    if(zero) {
		    	if(quantidade >= 0) {
				    totalItens += quantidade;
				    valorTotalProdutos += valor;
				    listConsumo.add(addConsumoInList(consumo, quantidade, p));
		    	}
		    }else {
		    	if(quantidade > 0) {
		    	    totalItens += quantidade;
				    valorTotalProdutos += valor;
				    listConsumo.add(addConsumoInList(consumo, quantidade, p));
		    	}
		    }
	    }
	    
		return listConsumo;
	}
	
	
	private List<Consumo> consumoCategoria(Date dataInicio, Date dataFim) {
		
	    List<Saida> saidas = saidaRepository.findByDate(dataInicio, dataFim);
	    
	    List<CategoriaProduto> categorias = categoriaRepository.findAll();
		List<Consumo> listConsumo =  new ArrayList<>(); 
	    Consumo consumo;
		
	    double valor = 0.;
	    double quantidade = 0.;
	    
			for(CategoriaProduto c : categorias) { 
				 valor =0.;
		         quantidade = 0.0;
				for (Saida s : saidas) {
			        for(SaidaItens itens : s.getSaidaItens()) {
			        	 
						 if(c.getNome().equals(itens.getProduto().getCategoria().getNome())) {
							  valor = valor + (itens.getProduto().getValor() * itens.getQuantidade()); 
							  quantidade = quantidade + itens.getQuantidade();
					     } 
					}
		        
			    }
				consumo = new Consumo();
				consumo.setNomeCategoria(c.getNome()); 
				consumo.setQuantidade(quantidade);
				consumo.setValor(valor);  
				listConsumo.add(consumo); 
		}
	    
		return listConsumo;
	}
	
	
	private List<Saida> findSaidaBySetorAndDate(Date dataInicio, Date dataFim, String setor, boolean todosSetores){
		
		if(!todosSetores) {
			return saidaRepository.findByDateAndSetor(setor, dataInicio, dataFim);
		}
		
		return saidaRepository.findByDate(dataInicio, dataFim);
	}
	
	
	private Consumo addConsumoInList(Consumo c, double quantidade, Produto p) {
		 c.setValorString(p.getValor());
		 c.setQuantidade(quantidade);
		 c.setNomeProduto(p.getNome());
		 c.setUnidadeMedida(p.getUnidadeMedida().toString());
		 return c;
	}
	
	
}

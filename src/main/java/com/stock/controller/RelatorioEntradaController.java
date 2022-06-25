package com.stock.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.stock.models.Entrada;
import com.stock.models.EntradaItens;
import com.stock.models.Produto;
import com.stock.repository.EntradaRepository;
import com.stock.repository.ProdutoRepository;
import com.stock.utils.Consumo;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;


/**
 * 
 * @author isaias
 *
 */
@Controller
public class RelatorioEntradaController {

	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private EntradaRepository entradaRepository;
	
	
	@PreAuthorize("hasAnyAuthority('ADMIN','USER','MANAGER','VIEW')")
	@GetMapping("/relatorio/entrada_por_produto")
	public ModelAndView entradaPorProduto() {
		ModelAndView mv = new ModelAndView("relatorio/entrada-por-produto");
		mv.addObject("listaProdutos", produtoRepository.findByOrderByNomeAsc());
	   return mv;
	}
	
	

	@PostMapping(value = "/relatorio/pesquisa_entrada_produto", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generatePdf(String produto, String dataInicio, String dataFim) throws ParseException {
		byte data[] = null;
		
		List<Consumo> list =	pesquisaEntradaProduto(new SimpleDateFormat("yyyy-MM-dd").parse(dataInicio),
				new SimpleDateFormat("yyyy-MM-dd").parse(dataFim), produto);
		
		JRBeanCollectionDataSource beanCollectionDataSource = 
				new JRBeanCollectionDataSource(list, false);

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("total", sumTotalQuantidade(list) );

		JasperReport compileReport;
		try {
			
			compileReport = JasperCompileManager
					.compileReport(new FileInputStream("reports/entrada-por-produto-nome.jrxml"));
			JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, parameters, beanCollectionDataSource);
			 data = JasperExportManager.exportReportToPdf(jasperPrint);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=citiesreport.pdf");

		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

	}
	
	
	private Double sumTotalQuantidade(List<Consumo> list) {
		Double total = 0.;
		for(Consumo c : list) {
			total += c.getQuantidade();
		}
		
		return total;
	}
	
	
	private List<Consumo> pesquisaEntradaProduto(Date dataInicio, Date dataFim, String produto) {
		List<Consumo> listConsumo =  new ArrayList<>(); 
		
		List<Entrada> entradas = entradaRepository.findByDateRecebimento(dataInicio, dataFim);

		
		Consumo consumo;
	    double quantidade = 0.;
	    
	    if(!produto.isEmpty()) {
	    	List<Produto> listProduto = produtoRepository.findByName(produto);
		    for(Produto p : listProduto) {
		    	
			    for (Entrada s : entradas) {
			    	for(EntradaItens iten : s.getEntradaItens()) {
			    		if(p.getNome().equals(iten.getProduto().getNome())) {
			    			
			    			consumo = new Consumo();
					    	consumo.setData(s.getDataRecebimento());
						    consumo.setQuantidade(iten.getQuantidade());
						    consumo.setNomeProduto(p.getNome());
						    consumo.setValor(iten.getValor());
						    listConsumo.add(consumo); 
			    		}
			    	}
			    	
			    }
			    
			    
		    }
	    }else {
	    	
	    	List<Produto> listProduto = produtoRepository.findByOrderByNomeAsc();
	    	 for(Produto p : listProduto) {
			    	
				    quantidade = 0.;
				    for (Entrada s : entradas) {
				    	for(EntradaItens iten : s.getEntradaItens()) {
				    		if(p.getNome().equals(iten.getProduto().getNome())) {
				    			quantidade = quantidade + iten.getQuantidade(); 
				    		}
				    	}
				    }
				    
				    consumo = new Consumo();
				    consumo.setQuantidade(quantidade);
				    consumo.setNomeProduto(p.getNome());
				    listConsumo.add(consumo); 
			    }
	    }
		
		return listConsumo;
	}
	
	

	
}

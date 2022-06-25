package com.stock.controller;


import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.stock.models.Produto;

import org.springframework.beans.factory.annotation.Autowired;
import com.stock.repository.ProdutoRepository;
import org.springframework.data.domain.Sort;
import com.stock.utils.ReportGenerate;



/**
 * 
 * @author isaias
 *
 */
@Controller
public class RelatorioEstoqueController {
    

    @Autowired
	private ProdutoRepository produtoRepository;

    ReportGenerate report = new ReportGenerate();


    @PostMapping(value = "/produtos/posicao_estoque_gerarPDF")
	public ResponseEntity<byte[]> generatePdf(String categoria, boolean todas) {
		List<Produto> produtoList;
		if(todas) {
			produtoList = produtoRepository.findAll(Sort.by(Sort.Direction.ASC, "nome"));	
		}else {
			produtoList = produtoRepository.findByCategory(categoria);	
		}
		JRBeanCollectionDataSource beanCollectionDataSource = 
				new JRBeanCollectionDataSource(produtoList, false);
		
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("total", calcTotalProduto(produtoList));

		return report.generatereportPDF(parameters, beanCollectionDataSource, "reports/produtos_estoque.jrxml");
	}

    private String calcTotalProduto(List<Produto> produtoList) {
		Produto produto = new Produto();
		
		double total = 0.;
		
		for(Produto p : produtoList) {
		    total += p.getEstoque() * p.getValor();
		}
		
		return produto.getNumberFormat(total);
	}

}

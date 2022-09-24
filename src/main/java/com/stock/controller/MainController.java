package com.stock.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.stock.models.Saida;
import com.stock.models.SaidaItens;
import com.stock.repository.SaidaRepository;

/**
 * 
 * @author isaias
 *
 */
@Controller
public class MainController {

	@Autowired
	private SaidaRepository saidaRepositori;
	
	@GetMapping("/")
	public ModelAndView acessarPrincipal() {
		
		long saidasTotal =  saidaRepositori.count();
		long atender =  saidaRepositori.countSaidaAtender();
		long total =  saidasTotal - atender;
		
		
		
		ModelAndView mv = new ModelAndView("home");
		mv.addObject("totalSaidaAtender", atender);
		mv.addObject("totalAtendidas",  total);
		mv.addObject("valorTotal", totalSaidas());
		return mv;
	}
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/denied")
	public String denied() {
		return "403";
	}
	
	
	private Double totalSaidas() {
		
		Date now = new Date();
		int month = now.getMonth();
		int day = now.getDay();
		int year = now.getYear();
		
		Date init = new Date(year, month, 1);
		
		List<Saida> saidas = saidaRepositori.findByDate(init, now);
		Double total = 0.0;
		
		for(Saida s : saidas) {
			for(SaidaItens i : s.getSaidaItens()) {
				total += i.getQuantidade() * i.getProduto().getValor();
			}
		}
		
		System.out.println(	"data hoje  "+now);
		System.out.println(	"inicio mes  "+init);
	    System.out.println(	"total saidas  "+saidas.size());
		return total;
	}
	
}





package com.stock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

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
	
	
}

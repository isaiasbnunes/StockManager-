package com.stock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 
 * @author isaias
 *
 */
@Controller
public class MainController {

	@GetMapping("/")
	public String acessarPrincipal() {
		return "home";
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

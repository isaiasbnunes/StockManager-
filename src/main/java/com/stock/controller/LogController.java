package com.stock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.stock.repository.LogRepository;


@Controller
public class LogController {

	@Autowired
	private LogRepository logRepository;
	
	
	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@GetMapping("/admin/log/listar")
	public ModelAndView listar() {
		ModelAndView mv = new ModelAndView("admin/log/lista");
		mv.addObject("listLog", logRepository.findAll(Sort.by(Sort.Direction.DESC, "data")));
		return mv;
	}
	
}

package com.stock.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExceptionHandlerController {
	
	 @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
	    @ExceptionHandler(Exception.class)
	    public ModelAndView tratadorDeException() {
	        ModelAndView modelAndView = new ModelAndView();
	        //
	        modelAndView.setViewName("403");
	        return modelAndView;
	    }
	 
}

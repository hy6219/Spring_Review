package com.zero.mvc.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class CommonExceptionAdvice {
	
	
	@ExceptionHandler(Exception.class)
	public ModelAndView common(Exception e) {
		ModelAndView mv=new ModelAndView();
		
		mv.setViewName("/error_basic");
		mv.addObject("exception",e);
		
		return mv;
	}
}

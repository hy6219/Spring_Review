package com.zero.mvc.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zero.mvc.domain.model.BoardVO;
import com.zero.mvc.domain.model.PageMaker;
import com.zero.mvc.domain.model.SearchCriteria;
import com.zero.mvc.service.BoardService;

@Controller
@RequestMapping("/sboard/*")
public class SearchBoardController {
	
	private static final Logger logger=
			LoggerFactory.getLogger(SearchBoardController.class);
	
	@Autowired
	private BoardService service;
	
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public String listPage(@ModelAttribute("cri") SearchCriteria cri, Model model) throws Exception {
		
		logger.info("검색조건으로 페이징 처리:{}",cri);
		
		List<BoardVO> list=service.listSearch(cri);
		int cnt=service.listSearchCount(cri);
		
		
		PageMaker pageMaker=new PageMaker();
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(cnt);
		
		
		model.addAttribute("list", list);
		model.addAttribute("pageMaker", pageMaker);
		
		return "/sboard/list";
	}
	
	@RequestMapping(value="/read",method=RequestMethod.GET)
	public String read(@RequestParam("bno") int bno,
			@ModelAttribute("cri") SearchCriteria cri, Model model) throws Exception{
		BoardVO board=service.read(bno);
		model.addAttribute("target", board);
		return "/sboard/readPage";
	}
	
}

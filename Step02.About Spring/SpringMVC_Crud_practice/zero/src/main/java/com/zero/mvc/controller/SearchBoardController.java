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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	
	//수정 페이지 보여주기
	@RequestMapping(value="/modifyPage", method=RequestMethod.GET)
	public String modifyPageGET(int bno, @ModelAttribute("cri") SearchCriteria cri, Model model) throws Exception{
		BoardVO board=service.read(bno);
		model.addAttribute("target", board);
		return "/sboard/modifyPage";
	}
	
	//수정할 내용 반영하기
	@RequestMapping(value="/modifyPage",method=RequestMethod.POST)
	public String modifyPagePOST(BoardVO board, SearchCriteria cri,RedirectAttributes rttr) throws Exception{
		
		int modRes=service.modify(board);
		String msg=modRes>0?"success":"failed";
		
		rttr.addFlashAttribute("page", cri.getPage());
		rttr.addFlashAttribute("pageNum", cri.getPageNum()); 
		rttr.addFlashAttribute("searchType", cri.getSearchType());
		rttr.addFlashAttribute("keyword", cri.getKeyword());
		rttr.addFlashAttribute("msg", msg);
		
		return "redirect:/sboard/list";
	}
	
	//게시글 삭제
	@RequestMapping(value="/removePage",method=RequestMethod.POST)
	public String remove(@RequestParam("bno") int bno, SearchCriteria cri, RedirectAttributes rttr) throws Exception{
		int removeRes=0;
		String msg="";
		
		removeRes=service.remove(bno);
		msg=removeRes>0?"success":"failed";
		
		rttr.addFlashAttribute("page", cri.getPage());
		rttr.addFlashAttribute("pageNum", cri.getPageNum());
		rttr.addFlashAttribute("searchType", cri.getSearchType());
		rttr.addFlashAttribute("keyword", cri.getKeyword());
		rttr.addFlashAttribute("msg", msg);
		
		
		return "redirect:/sboard/list";
	}
	
	//게시글 등록 양식 페이지 반환
	@RequestMapping(value="/register",method=RequestMethod.GET)
	public String registerGET() throws Exception{
		return "/sboard/register";
	}
	
	//게시글 등록
	@RequestMapping(value="/register",method=RequestMethod.POST)
	public String registerPOST(BoardVO board, RedirectAttributes rttr) throws Exception{
		int regRes=0;
		String msg="";
		
		regRes=service.register(board);
		
		msg=regRes>0?"success":"failed";
		
		rttr.addFlashAttribute("msg", msg);
		return "redirect: /sboard/list";
	}
}

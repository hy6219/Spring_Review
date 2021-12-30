package com.zero.mvc.controller;

import java.util.ArrayList;
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
import com.zero.mvc.domain.model.Criteria;
import com.zero.mvc.domain.model.PageMaker;
import com.zero.mvc.service.BoardService;

@Controller
@RequestMapping("/board/*")
public class BoardController {

	private static Logger logger=
			LoggerFactory.getLogger(BoardController.class);
	
	@Autowired
	private BoardService service;
	
	//등록 포맷 페이지 리턴
		@RequestMapping(value="/register", method=RequestMethod.GET)
		public String registerFormatPage() {
			return "/board/register";
		}
		//게시물 등록
		@RequestMapping(value="/register", method=RequestMethod.POST)
		public String registerArticle(RedirectAttributes rttr,BoardVO board) {
			int insertRes=0;
			
			try {
				insertRes=service.register(board);
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			// 리다이렉트시 정보 전달
			if(insertRes>0) {
				//삽입 성공
				rttr.addFlashAttribute("msg", "success");
			}else {
				//삽입실패
				rttr.addFlashAttribute("msg", "failed");
			}
			
			
			return "redirect: /board/listAll";
		}
	
	//전체조회
		@RequestMapping(value="/listAll",method=RequestMethod.GET)
		public String listAll(Model model) {
			List<BoardVO> articles=new ArrayList<>();
			
			try {
				articles=service.readAll();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			model.addAttribute("articles", articles);
			
			return "/board/listAll";
		}

	
		//특정글 상세보기
		@RequestMapping(value="/read", method=RequestMethod.GET)
		public String read(@RequestParam("bno") int bno,Model model) throws Exception {
			
			BoardVO target=new BoardVO();
			target=service.read(bno);
			
			if(target==null) {
				//존재하지 않는 경우
				throw new Exception("예외발생,처리");
			}
			
			//존재하는 경우
			model.addAttribute("article",target);
			
			return "/board/read";
		}
		
		//페이징처리 이후  정보 유지하기
		@RequestMapping(value="/readPage",method=RequestMethod.GET)
		public String read(@RequestParam("bno") int bno,
						  @ModelAttribute("cri") Criteria cri,
						  Model model) {
			
			BoardVO target=new BoardVO();
			
			try {
				target=service.read(bno);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//존재하는 경우
			model.addAttribute("article",target);
			
			return "/board/readPage";
		}
		
		//게시글 삭제
		@RequestMapping(value="/remove",method=RequestMethod.POST)
		public String remove(@RequestParam("bno") int bno,RedirectAttributes rttr) {
			int removeRes=0;
			
			try {
				removeRes=service.remove(bno);
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			if(removeRes>0) {
				//삭제 성공
				rttr.addFlashAttribute("msg", "success");
			}else {
				//삭제 실패
				rttr.addFlashAttribute("msg", "failed");
			}
			
			return "redirect: /board/listAll";
		}
		//수정할 내용 원본 보여주기
		@RequestMapping(value="/modify",method=RequestMethod.GET)
		public String modifyOriginal(@RequestParam("bno") int bno,Model model) {
			BoardVO target=new BoardVO();
			
			try {
				target=service.read(bno);
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			logger.info("수정할 내용 원본:{}",target);
			
			model.addAttribute("article",target);
			
			return "/board/modify";
		}
	
	//글 수정
	@RequestMapping(value="/modify",method=RequestMethod.POST)
	public String modifyBoard(BoardVO board,Model model) {
		
		int modRes=0;
		
		try {
			modRes=service.modify(board);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		logger.info("수정할 내용: {}",board);
		
		if(modRes>0) {
			//수정 성공
			model.addAttribute("msg", "success");
		}else {
			//수정 실패
			model.addAttribute("msg", "failed");
		}
		
		return "redirect:/board/listAll";
	}
	
	
	//페이징 처리
	@RequestMapping(value="/listCri", method=RequestMethod.GET)
	public String listPage(@ModelAttribute("cri") Criteria cri,Model model) throws Exception {
		logger.info("cri: "+cri.toString());
		
		
		PageMaker pageMaker=new PageMaker();
		pageMaker.setCri(cri);
		//전체 게시물 수
		int totalCount=service.countArticles();
		
		pageMaker.setTotalCount(totalCount);
		
		List<BoardVO> list=service.listCriteria(cri);
		model.addAttribute("list",list);
		model.addAttribute("pageMaker",pageMaker);
		
		//return "/board/listPage";
		return "/board/listCri";
	}
	
	//예외처리
	//exceptionHandler,ResponseStatus
//	@ExceptionHandler(Exception.class)
//	@ResponseStatus(value=HttpStatus.NOT_FOUND,reason="404")
//	public void handleException(Exception e) {
//		logger.info("error:{}",e.getMessage());
//	}
//	

}	

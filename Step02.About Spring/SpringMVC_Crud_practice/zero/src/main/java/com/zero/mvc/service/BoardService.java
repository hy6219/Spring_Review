package com.zero.mvc.service;

import java.util.List;

import com.zero.mvc.domain.model.BoardVO;
import com.zero.mvc.domain.model.Criteria;

public interface BoardService {
	public int register(BoardVO board) throws Exception;
	public BoardVO read(Integer bNo) throws Exception;
	public List<BoardVO> readAll() throws Exception;
	public int modify(BoardVO board) throws Exception;
	public int remove(Integer bNo) throws Exception;
	public List<BoardVO> listCriteria(Criteria cri) throws Exception;
	public int countArticles();
}

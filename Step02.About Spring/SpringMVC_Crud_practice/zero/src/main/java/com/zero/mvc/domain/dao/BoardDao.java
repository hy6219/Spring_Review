package com.zero.mvc.domain.dao;

import java.util.List;

import com.zero.mvc.domain.model.BoardVO;
import com.zero.mvc.domain.model.Criteria;

public interface BoardDao {
	
	static final String NAMESPACE="board.";
	
	public int insert(BoardVO board) throws Exception;
	public BoardVO selectOne(Integer bNo) throws Exception;
	public List<BoardVO> selectAll() throws Exception;
	public int update(BoardVO board) throws Exception;
	public int delete(Integer bNo) throws Exception;
	public List<BoardVO> listPage(int page) throws Exception;
	public List<BoardVO> listCriteria(Criteria cri) throws Exception;
	public int countArticles();
}

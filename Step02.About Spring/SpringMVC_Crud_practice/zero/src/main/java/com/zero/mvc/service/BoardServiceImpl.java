package com.zero.mvc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zero.mvc.domain.dao.BoardDao;
import com.zero.mvc.domain.model.BoardVO;
import com.zero.mvc.domain.model.Criteria;
import com.zero.mvc.domain.model.SearchCriteria;

@Service
public class BoardServiceImpl implements BoardService{

	@Autowired
	private BoardDao dao;
	
	@Override
	public int register(BoardVO board) throws Exception {
		// TODO Auto-generated method stub
		return dao.insert(board);
	}

	@Override
	public BoardVO read(Integer bNo) throws Exception {
		// TODO Auto-generated method stub
		return dao.selectOne(bNo);
	}

	@Override
	public List<BoardVO> readAll() throws Exception {
		// TODO Auto-generated method stub
		return dao.selectAll();
	}

	@Override
	public int modify(BoardVO board) throws Exception {
		// TODO Auto-generated method stub
		return dao.update(board);
	}

	@Override
	public int remove(Integer bNo) throws Exception {
		// TODO Auto-generated method stub
		return dao.delete(bNo);
	}

	@Override
	public List<BoardVO> listCriteria(Criteria cri) throws Exception {
		// TODO Auto-generated method stub
		return dao.listCriteria(cri);
	}

	@Override
	public int countArticles() {
		// TODO Auto-generated method stub
		return dao.countArticles();
	}

	@Override
	public List<BoardVO> listSearch(SearchCriteria cri) throws Exception {
		// TODO Auto-generated method stub
		return dao.listSearch(cri);
	}

	@Override
	public int listSearchCount(SearchCriteria cri) throws Exception {
		// TODO Auto-generated method stub
		return dao.listSearchCount(cri);
	}

}

package com.zero.mvc.domain.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zero.mvc.domain.model.BoardVO;
import com.zero.mvc.domain.model.Criteria;
import com.zero.mvc.domain.model.SearchCriteria;


@Repository
public class BoardDaoImpl implements BoardDao{

	@Autowired
	private SqlSessionTemplate session;
	
	@Override
	public int insert(BoardVO board)  throws Exception{
		// TODO Auto-generated method stub
		return session.insert(NAMESPACE+"insert",board);
	}

	@Override
	public BoardVO selectOne(Integer bNo)  throws Exception{
		// TODO Auto-generated method stub
		return session.selectOne(NAMESPACE+"selectOne",bNo);
	}

	@Override
	public List<BoardVO> selectAll() throws Exception {
		// TODO Auto-generated method stub
		return session.selectList(NAMESPACE+"selectAll");
	}

	@Override
	public int update(BoardVO board) throws Exception {
		// TODO Auto-generated method stub
		return session.update(NAMESPACE+"update",board);
	}

	@Override
	public int delete(Integer bNo) throws Exception {
		// TODO Auto-generated method stub
		return session.delete(NAMESPACE+"delete",bNo);
	}

	@Override
	public List<BoardVO> listPage(int page) throws Exception {
		// TODO Auto-generated method stub
		//기본값 1(0이하의 경우에 대한 요청시)
		if(page<=0) page=1;
		
		int idx=0;
		//(페이지-1)*10==>각 페이지의 요청건에 대한 시작인덱스가 될 것
		idx=(page-1)*10;
		return session.selectList(NAMESPACE+"listPage",idx);
	}

	@Override
	public List<BoardVO> listCriteria(Criteria cri) throws Exception {
		// TODO Auto-generated method stub
		return session.selectList(NAMESPACE+"listCriteria",cri);
	}

	@Override
	public int countArticles() {
		// TODO Auto-generated method stub
		return session.selectOne(NAMESPACE+"countArticles");
	}

	@Override
	public List<BoardVO> listSearch(SearchCriteria cri) throws Exception {
		// TODO Auto-generated method stub
		return session.selectList(NAMESPACE+"listSearch",cri);
	}

	@Override
	public int listSearchCount(SearchCriteria cri) throws Exception {
		// TODO Auto-generated method stub
		return session.selectOne(NAMESPACE+"listSearchCount",cri);
	}

}

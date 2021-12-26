package com.zero.mvc.domain.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zero.mvc.domain.model.BoardVO;


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

}

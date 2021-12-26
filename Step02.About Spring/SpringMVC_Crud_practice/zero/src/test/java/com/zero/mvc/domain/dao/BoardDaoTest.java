package com.zero.mvc.domain.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zero.mvc.domain.model.BoardVO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/**/*.xml"})
public class BoardDaoTest {

	@Autowired
	private BoardDao dao;
	
	private static Logger logger=
			LoggerFactory.getLogger(BoardDaoTest.class);
	
	@Test
	public void insertTest() throws Exception{
		BoardVO board=new BoardVO();
		board.setTitle("제목3");
		board.setContent("내용3");
		board.setWriter("작가3");
		System.out.println("board: "+board);
		dao.insert(board);
	}
	
	@Test
	public void selectOneTest() throws Exception {
		logger.info("selectOne: "+dao.selectOne(1));
	}
	
	@Test
	public void selectListTest() throws Exception {
		logger.info("selectList: {}", dao.selectAll());
	}
	

	
	@Test
	public void testUpdate() throws Exception{
		BoardVO board=new BoardVO();
		board.setBno(1);
		board.setTitle("수정된 제목");
		board.setContent("수정된 내용");
		dao.update(board);
	}
	
	@Test
	public void testDelete() throws Exception{
		dao.delete(2);
	}
}

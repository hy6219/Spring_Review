package com.zero.mvc.domain.dao;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zero.mvc.domain.model.BoardVO;
import com.zero.mvc.domain.model.Criteria;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/**/*.xml"})
public class BoardDaoTest {

	@Autowired
	private BoardDao dao;
	
	private static Logger logger=
			LoggerFactory.getLogger(BoardDaoTest.class);
	
//	@Test
//	public void insertTest() throws Exception{
//		BoardVO board=new BoardVO();
//		board.setTitle("제목3");
//		board.setContent("내용3");
//		board.setWriter("유저3");
//		System.out.println("board: "+board);
//		dao.insert(board);
//	}
	
	@Test
	public void selectOneTest() throws Exception {
		logger.info("selectOne: "+dao.selectOne(1));
	}
	
	@Test
	public void selectListTest() throws Exception {
		logger.info("selectList: {}", dao.selectAll());
	}
	

	
//	@Test
//	public void testUpdate() throws Exception{
//		BoardVO board=new BoardVO();
//		board.setBno(1);
//		board.setTitle("수정수정");
//		board.setContent("내용수정수정~");
//		dao.update(board);
//	}
//	
//	@Test
//	public void testDelete() throws Exception{
//		dao.delete(2);
//	}
	
	@Test
	public void testGetPagingContents() throws Exception{
		List<BoardVO> list=dao.listPage(3);
		logger.info("=======testGetPagingContents 테스트=======");
		for(BoardVO board:list) {
			logger.info("컨텐츠: {}",board.toString());
		}
		logger.info("=======");
	}
	
	@Test
	public void testListCriteria() throws Exception{
		
		Criteria cri=new Criteria();
		cri.setPage(2);
		cri.setPageNum(20);
		List<BoardVO> list=dao.listCriteria(cri);
		logger.info("=======listCriteria 테스트=======");
		
		for(BoardVO board:list) {
			logger.info("컨텐츠: {}",board.toString());
		}
		logger.info("=======");
	}
}

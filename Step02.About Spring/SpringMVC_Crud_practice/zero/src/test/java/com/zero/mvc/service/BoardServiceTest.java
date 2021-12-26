package com.zero.mvc.service;

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
public class BoardServiceTest {
	
	@Autowired
	private BoardService service;
	
	private final Logger logger=
			LoggerFactory.getLogger(BoardServiceTest.class);
	
	@Test
	public void readTest() throws Exception{
		logger.info("read: "+service.read(1));
	}
	
	@Test
	public void readAllTest() throws Exception{
		logger.info("readAll: "+service.readAll());
	}
	
	@Test
	public void modifyTest() throws Exception{
		BoardVO board=service.read(1);
		board.setTitle("수정수정제목1");
		
		service.modify(board);
		
		logger.info("after modified: {}",service.readAll());
	}
	
	@Test
	public void removeTest() throws Exception{
		service.remove(3);
		logger.info("after remove: {}",service.readAll());
	}
}

# 스프링 MVC- 페이징 처리

`페이징 처리`

사용자에게 필요한 최소한의 데이터를 전송하기 위해서 *전체 데이터 중 일부분만을 보여주는 방식*

## 01. Overview

1. URI 문자열을 조절 ▶ 원하는 페이지의 데이터가 출력될 수 있도록 하는 단계
- /board/listCri?page=3
😊 방법 (1) a 태그의 href 속성을 활용해서 URI 지정

 장점: 검색엔진 노출이 쉬워짐 ◀ 모든 연결 정보를 한번에 파악 가능

😊 방법 (2) form 태그를 이용해서 링크 클릭시 여러 정보를 전달
장점: 최소한의 정보를 이용해서 빠르게 개발 가능

2. 목록 페이지 하단에 페이지 번호를 보여주고, 클릭하면 페이지가 이동하는 단계

- 원하는 페이지의 데이터를 구하는 작업
- 화면 하단에 페이지를 출력해주는 작업(이전, 다음, 시작과 끝 페이지 번호에 대한 계산이 필요)

3. 목록 페이지에서 조회나 수정 작업 후 다시 원래 목록 페이지로 이동할 수 있도록 처리하는 단계

## 02. 페이징 처리 원칙

1. 반드시 GET 방식 이용
2. 페이징 처리가 되면 조회 화면에서 반드시 `목록 가기`가 필요
3. 반드시 필요한 페이지 번호만을 제공

## 03. MySQL을 활용한 페이징 쿼리 with mapper, dao, Criteria.java

오라클에서는 ROWNUM을 활용했다면, MySQL에서는 limit!!을 사용한다!! >3<

```sql
select ~
from ~
where ~
order by ~
limit 시작데이터,데이터갯수
```

EX) bno를 기준으로 생각했을 때, 랭킹으로 생각/접근한다면 [0,9]=>MySQL에서 시작 offset은 0부터라고 한다!
따라서 0,10: 1페이지/10개씩 보여줄 것
```sql
SELECT *
FROM  TBL_BOARD
WHERE BNO>0
ORDER BY BNO DESC
LIMIT 0,10
```

### 03-1. 충분한 더미데이터 넣기

```sql
INSERT INTO BOOK_EX.TBL_BOARD(TITLE,CONTENT,WRITER)
VALUES('테스트 제목','내용 테스트','user00');

INSERT INTO BOOK_EX.TBL_BOARD(TITLE,CONTENT,WRITER)
(SELECT TITLE,CONTENT,WRITER FROM BOOK_EX.TBL_BOARD);
```

넉넉하게 위의 쿼리로 효율적으로 더미데이터를 넣어주자

### 03-2. boardMapper.xml에 `x개씩 시작인덱스(자바 개념에서의 인덱스)부터 가져오는 페이징 쿼리` 추가하기

```xml
  	<!-- 페이징 : 10개씩 잘라오기-->
  	<select id="listPage" resultType="boardDto">
  		<![CDATA[
  			SELECT BNO,TITLE,CONTENT,WRITER,REGDATE,VIEWCNT
  			FROM TBL_BOARD
  			WHERE BNO>0
  			ORDER BY BNO DESC, REGDATE DESC
  			LIMIT #{idx},10
  		]]>
  	</select>
```


### 03-3.  mapper 파일을 활용해서 dao 작성하기

- Dao 인터페이스
```java
public List<BoardVO> listPage(int page) throws Exception;
```

- Dao 클래스
(1) 요청페이지가 1 이하라면 기본값으로 1을 설정해주기
(2) 페이지 파라미터의 의미는 [0,9]=`1페이지` 라는 의미로 사용할 것이므로, (페이지-1)*10을 해주도록 한다!
1페이지: 0~
2페이지: 10~
▶ 시작인덱스: 10*(페이지-1)
```java
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
```

👍 간단하게 잘 작동하는지 Dao Test를 해볼까~?
```java
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
		
		for(BoardVO board:list) {
			logger.info("컨텐츠: {}",board.toString());
		}
	}
}

```

일부러 db에 영향을 줄 수 있는 dml에 대한 테스트 부분을 주석처리하였다

3페이지는 랭킹 20부터 시작하는데, bno 에 대해서 내림차순 정렬하여 가져오는 모습을 볼 수 있다

```
NFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=132, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=131, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=130, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=129, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=128, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=127, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=126, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=125, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=124, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=123, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
```
### 03-4. 느낌팍!! 변동되는 한 페이지당 컨텐츠 수를 포함해서 페이징에 따른 컨텐츠를 가져오고 싶다면~?

😍 만약 쇼핑몰에서처럼 10개/20개/..씩 보여주고 싶다면 보여지는 아이템(컨텐츠) 수를 의미하는 limit a,b 중 `b` 값을 변동해주면 되겠다!!>3<

```xml
  	<!-- 페이징 : pageNum개씩 잘라오기-->
  	<select id="궁시렁궁시렁" resultType="boardDto">
  		<![CDATA[
  			SELECT BNO,TITLE,CONTENT,WRITER,REGDATE,VIEWCNT
  			FROM TBL_BOARD
  			WHERE BNO>0
  			ORDER BY BNO DESC, REGDATE DESC
  			LIMIT #{pageStart},#{pageNum}
  		]]>
  	</select>
```


그리고, 아래처럼 map 구조를 활용해도 되지만
```java
	@Override
	public MemberVO readWithPW(String userId, String userPw) {
		// TODO Auto-generated method stub
		Map<String,Object> param=
				new HashMap<>();
		
		param.put("userId", userId);
		param.put("userPw", userPw);
		return session.selectOne(NAMESPACE+"readWithPW",param);
	}
```

```java
```xml
  	<select id="readWithPW" resultType="memberVo">
  	 SELECT * FROM TBL_MEMBER
  	 WHERE USERID=#{userId} AND USERPW=#{userPw}
  	</select>
```

파라미터가 여러개로 될 수록 객체로 관리하는 것이 보다 효율적이다!

우리는 이를 Criteria 라는 클래스로 관리해보자

필드:
- 페이지값
- 한 페이지당 아이템수(기본값을 10으로 해보자)
---
👍 한 페이지당 보여지는 아이템수가 [0,100] 사이로 오지 않는 경우, 10개로 지정될 수 있도록 setter 메서드를 활용하자
👍 비슷한 흐름으로, 시작인덱스도 (페이지-1)*10을 getter 메서드를 활용해서 가져올 수 있도록 지원해줄 수 있다!
 
```java
package com.zero.mvc.domain.model;

public class Criteria {
	/**
	 * @author gs813
	 * page: 페이지값
	 * pageNum:한 페이지에 보여질 아이템수
	 * */
	private int page;
	private int pageNum;
	
	public Criteria() {
		super();
		// TODO Auto-generated constructor stub
		this.page=1;//초기값 1로 고정~
		this.pageNum=10;//초기값 10으로 고정~
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		if(page<=0) {
			this.page=1;
			return;
		}
		
		this.page = page;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		if(pageNum <=0 || pageNum>100) {
			this.pageNum=10;//한 페이지당 아이템은 [0,100]을 벗어나지 못하도록
			return;
		}
		
		this.pageNum = pageNum;
	}
	
	//시작 인덱스를 반환해주기
	public int getPageStart() {
		return (this.page-1)*this.pageNum;
	}

	@Override
	public String toString() {
		return "Criteria [page=" + page + ", pageNum=" + pageNum + "]";
	}
	
	
	
}

```

### 03-5. (실전)!! x개씩 보여줄 수 있도록 mapper, dao 작성하기!(유동적으로!)
(0) config.xml에 criteria 클래스 별칭 지정해주기
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
  
  <configuration>
  	<!-- 별칭 -->
   	<typeAliases>
   		<typeAlias type="com.zero.mvc.domain.model.BoardVO" alias="boardDto"/>
   		<typeAlias type="com.zero.mvc.domain.model.Criteria" alias="criteria"/>   		
  	</typeAliases>
  		<typeHandlers>
  <!-- LocalDateTime등 for jdk8 in mybatis -->
  		<typeHandler handler="org.apache.ibatis.type.InstantTypeHandler" />
  		<typeHandler handler="org.apache.ibatis.type.LocalDateTimeTypeHandler" />
  		<typeHandler handler="org.apache.ibatis.type.LocalDateTypeHandler" />
  		<typeHandler handler="org.apache.ibatis.type.LocalTimeTypeHandler" />
  		<typeHandler handler="org.apache.ibatis.type.OffsetDateTimeTypeHandler" />
  		<typeHandler handler="org.apache.ibatis.type.OffsetTimeTypeHandler" />
  		<typeHandler handler="org.apache.ibatis.type.ZonedDateTimeTypeHandler" />
  		<typeHandler handler="org.apache.ibatis.type.YearTypeHandler" />
  		<typeHandler handler="org.apache.ibatis.type.MonthTypeHandler" />
  		<typeHandler handler="org.apache.ibatis.type.YearMonthTypeHandler" />
  		<typeHandler handler="org.apache.ibatis.type.JapaneseDateTypeHandler" />
	</typeHandlers>
  	<!-- 매퍼위치들 -->
   	<mappers>
   		<mapper resource="mybatis/boardMapper.xml"/>
  	</mappers> 
  
  </configuration>
```

(1) mapper파일(boardMapper.xml 에 내용 추가)

✅ getter 메서드 명에 따라 작동하므로 이점을 유의!!
```xml
  	<!-- 유동적으로 x개씩 잘라올수 있도록 하기 -->
  	<select id="listCriteria" parameterType="criteria" resultType="boardDto">
  		<![CDATA[
  			SELECT BNO,TITLE,CONTENT,WRITER,REGDATE,VIEWCNT
  			FROM TBL_BOARD
  			WHERE BNO>0
  			ORDER BY BNO DESC, REGDATE DESC
  			LIMIT #{pageStart},#{pageNum}
  		]]>
  	</select>
```

(2) boardDao 인터페이스 내용 추가
```java
public List<BoardVO> listCriteria(Criteria cri) throws Exception;
```

(3) boardDaomImpl 클래스 내용 추가

```java
	@Override
	public List<BoardVO> listCriteria(Criteria cri) throws Exception {
		// TODO Auto-generated method stub
		return session.selectList(NAMESPACE+"listCriteria",cri);
	}
```

(4) 다시 한번 dao 테스트~
```java
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
```

```
INFO : com.zero.mvc.domain.dao.BoardDaoTest - =======listCriteria 테스트=======
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=132, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=131, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=130, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=129, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=128, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=127, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=126, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=125, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=124, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=123, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=122, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=121, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=120, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=119, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=118, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=117, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=116, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=115, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=114, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - 컨텐츠: BoardVO(bno=113, title=테스트 제목, content=내용 테스트, writer=user00, regDate=2021-12-28T22:20:05, viewCnt=0)
INFO : com.zero.mvc.domain.dao.BoardDaoTest - =======
```
그러면 이제는 10개가 아닌 20개이든, 30개이든지 끊어올 수 있는 것을 확인해볼 수 있다

### 03-6. 서비스 수정하기

- 서비스 인터페이스
```java
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
}

```
- 서비스 클래스
```java
	@Override
	public List<BoardVO> listCriteria(Criteria cri) throws Exception {
		// TODO Auto-generated method stub
		return dao.listCriteria(cri);
	}
```

### 03-7. 컨트롤러 및 뷰
✅ 시작 페이지 번호(여러 개 페이지가 존재한다면)
✅ 끝 페이지 번호
✅ 전체 데이터 갯수
✅ 이전, 이후 페이지 링크

#### 03-7-1. 끝 페이지 번호 endPage 구하기

`endPage=(int)(Math.ceil(cri.getPage()/(double)displayPageNum)*displayPageNum)`

- cri.getPage() : 현재 페이지 번호
- displayPageNum : 하단에 보여질 페이지번호 갯수(10개로 생각해보자 지금은!)

(예시)
(1) 현재페이지=1 ▶ 끝: 10(리스트에서 보여질)
▶ ceil(1/10)*10=10

(2) 현재페이지=3 ▶ 끝: 10(동적으로 보여준다면 12가 될 수도 있고, 11이 될 수도 있고~ 이건 사이트 마음~)

▶ ceil(3/10)*10=10

#### 03-7.2. 시작 페이지 번호 startPage 구하기

`startPage=(endPage-displayPageNum)+1`


(예시)
(1) 현재페이지=1 ▶ 끝: 10, 시작:1
▶ 시작페이지=(10-10)+1=1

(2) 현재페이지=3 ▶ 끝: 10, 시작:1
▶ 시작페이지=(10-10)+1=1

#### 03-7-3. 전체 게시물 수 totalCount, endPage 재계산

- endPage 갯수는 dml 작업으로 인한 갯수 변동이 있을 수 있기 때문에 재계산이 필요!

```java
int tempEndPage=
 (int)(Math.ceil(totalCount/(double)cri.getPageNum()));

if(endPage>tempEndPage){
   //endPage갱신
   endPage=tempEndPage;
}
```
#### 03-7-4. 이전 페이지, 이후 페이지 링크 계산

(1) 이전

- startPage==1 이면 이전값을 가질 수 없음

`startPage==1? false:true`

(2) 이후

- pageNum*endPage < totalCount 확인( 데이터가 더 남아 있는지)

`next= endPage*cri.getPageNum() >= totalCount? false:true`

#### 03-7-5. 위의 내용을 바탕으로 페이징 처리를 위한, Criteria를 품는 클래스인 PageMaker 클래스 만들기
- Criteria cri ◀ page, pageNum
- DB에서 계산되는 데이터 : totalCount (by sql)
- 계산을 통해 만들어지는 데이터: startPage, endPage, prev, next

```java
package com.zero.mvc.domain.model;

public class PageMaker {
	private Criteria cri;
	private int totalCount;
	private int startPage;
	private int endPage;
	private boolean prev;
	private boolean next;
	private int displayPageNum=10;
	
	
	public PageMaker() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Criteria getCri() {
		return cri;
	}


	public void setCri(Criteria cri) {
		this.cri = cri;
	}


	public int getTotalCount() {
		return totalCount;
	}


	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		calcData();//endPage 갱신
	}


	public int getStartPage() {
		return startPage;
	}


	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}


	public int getEndPage() {
		return endPage;
	}


	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}


	public boolean isPrev() {
		return prev;
	}


	public void setPrev(boolean prev) {
		this.prev=prev;
	}


	public boolean isNext() {
		return next;
	}


	public void setNext(boolean next) {
		this.next=next;
	}


	public int getDisplayPageNum() {
		return displayPageNum;
	}


	public void setDisplayPageNum(int displayPageNum) {
		this.displayPageNum = displayPageNum;
	}
	
	//페이징 처리작업을 위한 prev,next,totalCount,endPage,startPage 등 계산
	private void calcData() {
		//페이지번호 끝 계산
		this.endPage=(int)(Math.ceil(cri.getPage()/(double)this.displayPageNum)*this.displayPageNum);
		//페이지번호 시작 계산
		this.startPage=(this.endPage-this.displayPageNum)+1;
		//전체 데이터 갯수 갱신될때, endPage도 갱신될 수 있도록 setTotalCount 내부에 calcData 메서드 호출해주기!!
		//다시 계산!!(endPage)
		int tempEndPage=(int)(Math.ceil(this.totalCount/(double)cri.getPageNum()));
		
		if(this.endPage>tempEndPage) this.endPage=tempEndPage;
		
		//이전 페이지링크
		this.prev = this.startPage==1?false:true;
		//이후페이지링크: 누적 컨텐츠수 < 총 컨텐츠 수인지 파악
		this.next = cri.getPageNum() * this.endPage >= this.totalCount? false:true;
	}


	@Override
	public String toString() {
		return "PageMaker [cri=" + cri + ", totalCount=" + totalCount + ", startPage=" + startPage + ", endPage="
				+ endPage + ", prev=" + prev + ", next=" + next + ", displayPageNum=" + displayPageNum + "]";
	}
	
	
}

```
#### 03-7-6. 컨트롤러 
(0) config.xml 에 PageMaker 별칭 지정
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
  
  <configuration>
  	<!-- 별칭 -->
   	<typeAliases>
   		<typeAlias type="com.zero.mvc.domain.model.BoardVO" alias="boardDto"/>
   		<typeAlias type="com.zero.mvc.domain.model.Criteria" alias="criteria"/>   		
   		<typeAlias type="com.zero.mvc.domain.model.PageMaker" alias="pageMaker"/>   		
  	</typeAliases>
  		<typeHandlers>
  <!-- LocalDateTime등 for jdk8 in mybatis -->
  		<typeHandler handler="org.apache.ibatis.type.InstantTypeHandler" />
  		<typeHandler handler="org.apache.ibatis.type.LocalDateTimeTypeHandler" />
  		<typeHandler handler="org.apache.ibatis.type.LocalDateTypeHandler" />
  		<typeHandler handler="org.apache.ibatis.type.LocalTimeTypeHandler" />
  		<typeHandler handler="org.apache.ibatis.type.OffsetDateTimeTypeHandler" />
  		<typeHandler handler="org.apache.ibatis.type.OffsetTimeTypeHandler" />
  		<typeHandler handler="org.apache.ibatis.type.ZonedDateTimeTypeHandler" />
  		<typeHandler handler="org.apache.ibatis.type.YearTypeHandler" />
  		<typeHandler handler="org.apache.ibatis.type.MonthTypeHandler" />
  		<typeHandler handler="org.apache.ibatis.type.YearMonthTypeHandler" />
  		<typeHandler handler="org.apache.ibatis.type.JapaneseDateTypeHandler" />
	</typeHandlers>
  	<!-- 매퍼위치들 -->
   	<mappers>
   		<mapper resource="mybatis/boardMapper.xml"/>
  	</mappers> 
  
  </configuration>
```
(1) boardMapper.xml에 전체 게시물수를 카운트하는 쿼리 추가
```xml
  	 <!-- 전체 게시물 수 세기 -->
  	 <select id="countArticles">
  	  SELECT COUNT(BNO)
  	  FROM TBL_BOARD
  	 </select>
```

(2) dao에 (1) 관련 추가

```java
	@Override
	public int countArticles() {
		// TODO Auto-generated method stub
		return session.selectOne(NAMESPACE+"countArticles");
	}
```

서비스단에서도 적절히 추가해주기

------


```java
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
		
		return "/board/listPage";
	}
```
#### 03-7-7. listAll.jsp를 복사해서 listPage.jsp 이어서 작성해주기

```html
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="../include/header.jsp" %>  
<%request.setCharacterEncoding("utf-8"); %>
<%response.setContentType("text/html;charset=utf-8"); %> 
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<table class="table table-bordered">
		<thead>
			<tr>
				<th style="width:10px">BNO</th>
				<th>TITLE</th>
				<th>WRITER</th>
				<th>REGDATE</th>
				<th style="width:40px">VIEWCNT</th>
			</tr>
		</thead>
		<tbody>
			<c:choose>
				<c:when test="${empty list }">
					<tr>
						<td colspan="5">
						---등록된 게시글이 없습니다---
						</td>
					</tr>
				</c:when>
				<c:otherwise>
					<c:forEach items="${list}" var="item">
						<tr>
							<fmt:parseDate value="${item.regDate}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDateTime" type="both"/>
							<td>${item.bno }</td>
							<td><a href="<%=request.getContextPath()%>/board/read?bno=${item.bno}" title="${item.bno} 게시글 보기" target="_blank">${item.title }</a></td>
							<td>${item.writer }</td>
							<td><fmt:formatDate pattern="yyyy-MM-dd hh:mm:ss aa" value="${parsedDateTime }"/></td>
							<td>${item.viewCnt}</td>
						</tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</tbody>	
	</table>
	
	<!-- 페이지 버튼 -->
	<div class="text-center">
		<ul class="pagination">
			<c:if test="${pageMaker.prev }">
			<!-- 이전페이지 -->
				<li><a href="listCri?page=${pageMaker.startPage-1}">이전</a></li>
			</c:if>
			<!-- 페이지 번호들 -->
			<c:forEach begin="${pageMaker.startPage}" end="${pageMaker.endPage }" var="idx">
			<!-- 버튼 활성 -->
				<li <c:out value="${pageMaker.cri.page == idx?'class =active':''}"/>>
					<a href="listCri?page=${idx}">${idx }</a>
				</li>
			</c:forEach>
			<c:if test="${pageMaker.next }">
			<!-- 이후페이지 -->
				<li><a href="listCri?page=${pageMaker.endPage+1}">이후</a></li>
			</c:if>
		</ul>
	</div>
	<%@ include file="../include/footer.jsp" %>
</body>
</html>
```

직접 `http://localhost:9100/mvc/board/listCri?page=1` 로 테스트해보면 아래와 같이 잘 표시되는 것을 알 수 있다!
![페이징 처리!](https://github.com/hy6219/Spring_Review/blob/main/Step02.About%20Spring/SpringMVC/Pagination/%ED%8E%98%EC%9D%B4%EC%A7%95%EC%B2%98%EB%A6%AC_MySQL.PNG?raw=true)

완성본은 [여기](https://github.com/hy6219/Spring_Review/tree/main/Step02.About%20Spring/SpringMVC_Crud_practice/zero)! 2021-12-29일자로 정리된 버전을 확인하변 된다

그리고 `http://localhost:9100/mvc/board/listCri?page=5&pageNum=20` 처럼 페이지 번호뿐 아니라, 여러개의 데이터를 보여주기 위해서 쿼리스트링에 전달해줄 수 있다
![](https://github.com/hy6219/Spring_Review/blob/main/Step02.About%20Spring/SpringMVC/Pagination/pageNum%EC%9D%84%20%ED%99%9C%EC%9A%A9%ED%95%9C%20%ED%8E%98%EC%9D%B4%EC%A7%95%EC%B2%98%EB%A6%AC.png?raw=true)



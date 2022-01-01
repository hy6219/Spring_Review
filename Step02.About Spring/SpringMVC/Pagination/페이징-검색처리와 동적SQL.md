# 페이징-검색처리와 동적SQL

이번에는 `검색조건에 따른 페이징 처리`를 연습해보자!

How to?

1) MyBatis Dynamic SQL 이용

2) `@SelectProvider` 어노테이션 활용


## 01. 검색조건 객체 만들기

✅ `검색조건`을 객체로 만들고

✅ `검색조건 객체`가  `페이징객체 Criteria를 상속받도록`  진행

▶ 유지되어야 할 정보

- 현재 페이지 번호
- 페이지당 보여지는 데이터 수
- 검색 종류(t: title, c: content, w: writer)
- 검색 키워드

(1) 검색조건 객체
SearchCriteria.java
```java
package com.zero.mvc.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria extends Criteria{
	//검색종류
	private String searchType;
	//검색 키워드
	private String keyword;	
}

```

## 02. MyBatis mapper 작성

### 02-1. 👍MyBatis 동적 SQL 기능👍

(1) `<if></if>` : if 조건문처럼 사용
```
<if test="title!=null">AND title like #{title}</if>
``` 

(2) `choose, when, otherwise` : `switch-case` 처럼 사용

```
<choose>
  <when test="title!=null">
	  AND title like #{title}
  </when>
  <when test="author !=null and author.name!=null">
      AND author_name like #{author.name}
  </when>
  <otherwise>
	  AND featured=1
  </otherwise>
</choose>
```
(3) `trim,where,set`

- 단독으로 사용하지 않고, `<if>,<choose>`와 같은 태그들을 내포하여 SQL들을 연결해줌

`<where>`

내부 조건 성립에 따라 where 조건절 붙이기
```
SELECT * FROM BOARD
	<where>
		<if test="bno!=null">
			bno=#{bno}
		</if>
	</where>
```

👍 의미

1)  bno==null 인 경우: 
```
SELECT * FROM BOARD;
```

2) bno!=null 인 경우:

```
SELECT * FROM BOARD
WHERE BNO=2;
```

`<set>`

내부 if / choose 조건절이 성립될 때에만 `SET 구문이 붙음`

```
UPDATE BOARD
<set>
	<if test="title!=null and title!=''">
		title=#{title}
	</if>
</set>
WHERE BNO=#{bno}
```

▶ title값이 존재하고, title값이 ''이 아니라면 아래와 같이 성립됨 (아래처럼)

```sql
UPDATE BOARD
SET TITLE=#{title}
WHERE BNO=2;
```


`<trim>`

- `<where>/<set>+<if>`을 보완해주는 역할 by 접두사, 접미사
- prefix 속성: trim 태그 내부 실행될 쿼리문 앞에 설정해둔 속성값 삽입

```
UPDATE BOARD
<trim prefix="set">
	TITLE=#{title},CONTENT=#{content},WRITER=#{writer}
</trim>
WHERE BNO=#{bno};
```

↔ 
```sql
UPDATE BOARD
SET TITLE=#{title}, CONTENT=#{content},WRITER=#{writer}
WHERE BNO=#{bno};
```

- suffix 속성: trim 태그 내부 실행될 쿼리문 뒤에 설정된 속성값을 삽입

```
INSERT INTO BOARD(TITLE,CONTENT,WRITER)
<trim prefix="VALUES(" suffix=")">
#{title},#{content},#{writer}
</trim>
```

↔
```sql
INSERT INTO BOARD(TITLE,CONTENT,WRITER)
VALUES("제목1","내용1","USER01");
```

- prefixOverrides 속성: prefixOverrides 속성값과 trim 태그 내부 가장 앞 문자가 동일할 경우, 그 가장 앞 문자를 지우기

```
SELECT *
FROM  BOARD
WHERE
<trim prefixOverrides="OR">
	OR TITLE="1" AND CONTENT="1"
</trim>
```

↔
```sql
SELECT *
FROM BOARD
WHERE TITLE="1" AND CONTENT="1";
```

- suffixOverrides 속성: suffixOverrides 속성값과 trim 태그 내부 가장 뒤 문자가 동일한 경우, trim 태그 내부 가장 뒤 문자를 지우기

```
SELECT *
FROM  BOARD
WHERE
<trim suffixOverrides="AND">
TITLE="1" AND CONTENT="1" AND
</trim>
```

↔ 
```sql
SELECT *
FROM BOARD
WHERE TITLE="1" AND CONTENT="1";
```

`<foreach>`

- 반복적인 SQL 구문 작성에 용이
- collection 속성: 전달받은 인자(list, array 형태)
- item: 전달받은 인자값에 대한 별칭
- open : 해당 구문이 시작될때 삽입할 문자열  
- close : 해당 구문이 종료될때 삽입할 문자열  
- separator: 반복되는 사이에 출력할 문자열
- index: 반복되는 구문 번호(0부터 순차적으로 증가)

```
<foreach collection="boardList" item="list" open="(" close=")" separator="or">
</foreach>
```

`<sql>`

- 다른 구문에서 재사용 가능한 SQL 구문 정의시 사용

```
<sql id="id값">
사용할 쿼리
</sql>
```

`<include>`

- 같은 파일 내 정의해둔 `<sql>` 태그 내의 쿼리들을 불러올 수 있도록 해줌

```
<sql id="t1">
...
</sql>
<select id="t2" resultType="String">
	SELECT TITLE FROM BOARD
	<include refid="t1"></include>
</select>
```
### 02-2. MyBatis 동적쿼리 활용 way1

```sql
  	 <select id="listSearch" resultType="boardDto">
  	 	<![CDATA[
  	 		SELECT *
  	 		FROM TBL_BOARD
  	 		WHERE BNO>0
  	 	]]>
  	 	
  	 	<if test="searchType !=null">
  	 		<if test="searchType=='t'.toString()">
  	 			AND TITLE LIKE CONCAT('%',#{keyword},'%')
  	 		</if>
  	 		<if test="searchType=='c'.toString()">
  	 			AND CONTENT LIKE CONCAT('%',#{keyword},'%')
  	 		</if>
  	 		<if test="searchType=='w'.toString()">
  	 			AND WRITER LIKE CONCAT('%',#{keyword},'%')
  	 		</if>
  	 		<if test="searchType=='tc'.toString()">
  	 			AND (TITLE LIKE CONCAT('%',#{keyword},'%') OR CONTENT LIKE CONCAT('%',#{keyword},'%'))
  	 		</if>
  	 		<if test="searchType=='cw'.toString()">
  	 			AND (CONTENT LIKE CONCAT('%',#{keyword},'%') OR WRITER LIKE CONCAT('%',#{keyword},'%'))
  	 		</if>
  	 		<if test="searchType=='tcw'.toString()">
  	 			AND (TITLE LIKE CONCAT('%',#{keyword},'%') OR CONTENT LIKE CONCAT('%',#{keyword},'%') OR WRITER LIKE CONCAT('%',#{keyword},'%'))
  	 		</if>
  	 	</if>
  	 	<![CDATA[
  	 		ORDER BY BNO DESC
  	 		LIMIT #{pageStart},#{pageNum}
  	 	]]>
  	 </select>
```

### 02-3. MyBatis 동적쿼리 활용 way2

```sql
  	 <sql id="search">
  		 <if test="searchType !=null">
  	 		<if test="searchType=='t'.toString()">
  	 			AND TITLE LIKE CONCAT('%',#{keyword},'%')
  	 		</if>
  	 		<if test="searchType=='c'.toString()">
  	 			AND CONTENT LIKE CONCAT('%',#{keyword},'%')
  	 		</if>
  	 		<if test="searchType=='w'.toString()">
  	 			AND WRITER LIKE CONCAT('%',#{keyword},'%')
  	 		</if>
  	 		<if test="searchType=='tc'.toString()">
  	 			AND (TITLE LIKE CONCAT('%',#{keyword},'%') OR CONTENT LIKE CONCAT('%',#{keyword},'%'))
  	 		</if>
  	 		<if test="searchType=='cw'.toString()">
  	 			AND (CONTENT LIKE CONCAT('%',#{keyword},'%') OR WRITER LIKE CONCAT('%',#{keyword},'%'))
  	 		</if>
  	 		<if test="searchType=='tcw'.toString()">
  	 			AND (TITLE LIKE CONCAT('%',#{keyword},'%') OR CONTENT LIKE CONCAT('%',#{keyword},'%') OR WRITER LIKE CONCAT('%',#{keyword},'%'))
  	 		</if>
  	 	</if>
  	 </sql>
  	 <select id="listSearch" resultType="boardDto">
  	 	<![CDATA[
  	 		SELECT *
  	 		FROM TBL_BOARD
  	 		WHERE BNO>0
  	 	]]>
  	 	<include refid="search"></include>
  	 	<![CDATA[
  	 		ORDER BY BNO DESC
  	 		LIMIT #{pageStart},#{pageNum}
  	 	]]>
  	 </select>
```
그리고 이렇게 sql 태그와 include 태그를 활용하면 검색조건에 따른 쿼리부분은 아래와 같이 간단하게 정리될 수 있다
```sql
<select id="listSearchCount" resultType="int">
  		<![CDATA[
  			 SELECT COUNT(BNO)
  	 		FROM TBL_BOARD
  	 		WHERE BNO>0
  		]]>
  		<include refid="search"></include>
  	 </select>
```
## 03. Dao 부분 작성하기

BoardDao.java
```java
	public List<BoardVO> listSearch(SearchCriteria cri) throws Exception;
	public int listSearchCount(SearchCriteria cri) throws Exception;
```

BoardDaoImpl.java

```java
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
```

## 04. Service 부분 작성하기

BoardService.java

```java
	public List<BoardVO> listSearch(SearchCriteria cri) throws Exception;
	public int listSearchCount(SearchCriteria cri) throws Exception;
```

BoardServiceImpl.java

```java
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
```

## 05. 검색조건에 따른 페이징 처리를 다루는 컨트롤러 만들기

SearchBoardController.java

```java
package com.zero.mvc.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zero.mvc.domain.model.BoardVO;
import com.zero.mvc.domain.model.PageMaker;
import com.zero.mvc.domain.model.SearchCriteria;
import com.zero.mvc.service.BoardService;

@Controller
@RequestMapping("/sboard/*")
public class SearchBoardController {
	
	private static final Logger logger=
			LoggerFactory.getLogger(SearchBoardController.class);
	
	@Autowired
	private BoardService service;
	
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public String listPage(@ModelAttribute("cri") SearchCriteria cri, Model model) throws Exception {
		
		logger.info("검색조건으로 페이징 처리:{}",cri);
		
		List<BoardVO> list=service.listSearch(cri);
		int cnt=service.listSearchCount(cri);
		
		
		PageMaker pageMaker=new PageMaker();
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(cnt);
		
		
		model.addAttribute("list", list);
		model.addAttribute("pageMaker", pageMaker);
		
		return "/sboard/list";
	}
	
}

```

## 06. 검색조건에 따른 페이징 결과를 보여줄 뷰페이지 `/sboard/list` 만들기

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
<script>
	$(document).ready(function(){
		$("#searchBtn").on("click",function(event){
			self.location="list"+"${pageMaker.makeQuery(1)}"
			+"&searchType="+$("select option:selected").val()+
			"&keyword="+$("#keywordInput").val();
		});
		
		$("#newBtn").on("click",function(event){
			self.location="register";
		});
		
	});
</script>
<body>
	<div class="box-body">
    	  <select id="searchType" name="searchType">
        	 <option value="n" <c:out value="${cri.searchType==null?'selected':'' }"/>>--</option>
         	<option value="t" <c:out value="${cri.searchType eq 't'?'selected':'' }"/>>Title</option>
         	<option value="c" <c:out value="${cri.searchType eq 'c'?'selected':'' }"/>>Content</option>
         	<option value="w" <c:out value="${cri.searchType eq 'w'?'selected':'' }"/>>Writer</option>
         	<option value="tc" <c:out value="${cri.searchType eq 'tc'?'selected':'' }"/>>Title or Content</option>
         	<option value="cw" <c:out value="${cri.searchType eq 'cw'?'selected':'' }"/>>Content or Writer</option>
         	<option value="tcw" <c:out value="${cri.searchType eq 'tcw'?'selected':'' }"/>>Title or Content or Writer</option>
      	</select>
      	<input type="text" name="keyword" id="keywordInput" value="${cri.keyword }"/>
	      <button id="searchBtn">검색</button>
	      <button id="newBtn">register</button>
   </div>
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
							<td><a href="<%=request.getContextPath()%>/sboard/read?bno=${item.bno}" title="${item.bno} 게시글 보기" target="_blank">${item.title }</a></td>
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
				<li><a href="list${pageMaker.makeQuery(pageMaker.startPage-1)}">이전</a></li>
			</c:if>
			<!-- 페이지 번호들 -->
			<c:forEach begin="${pageMaker.startPage}" end="${pageMaker.endPage }" var="idx">
			<!-- 버튼 활성 -->
				<li <c:out value="${pageMaker.cri.page == idx?'class =active':''}"/>>
					<a href="list${pageMaker.makeQuery(idx)}">${idx }</a>
				</li>
			</c:forEach>
			<c:if test="${pageMaker.next }">
			<!-- 이후페이지 -->
				<li><a href="list${pageMaker.makeQuery(pageMaker.endPage+1)}">이후</a></li>
			</c:if>
		</ul>
	</div>
	<%@ include file="../include/footer.jsp" %>
</body>
</html>
```

## 07. 검색조건에 따른 URI 생성 makeQuery 메서드 수정

PageMaker.java

```java
package com.zero.mvc.domain.model;

import java.net.URI;

import org.springframework.web.util.UriComponentsBuilder;

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


	public PageMaker(Criteria cri, int totalCount, int startPage, int endPage, boolean prev, boolean next,
			int displayPageNum) {
		super();
		this.cri = cri;
		this.totalCount = totalCount;
		this.startPage = startPage;
		this.endPage = endPage;
		this.prev = prev;
		this.next = next;
		this.displayPageNum = displayPageNum;
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
	
	
	//URI 생성
	public String makeQuery(int page) {
		URI uri=UriComponentsBuilder.newInstance()
				.queryParam("page",page)
				.queryParam("pageNum", cri.getPageNum())
				.queryParam("searchType",((SearchCriteria)cri).getSearchType())
				.queryParam("keyword",((SearchCriteria)cri).getKeyword())
				.build()
				.toUri();
		
		return uri.toString();
	}
	
}

```

이제 `http://localhost:9100/mvc/sboard/list?page=3&pageNum=5&searchType=t&keyword=%ED%85%8C%EC%8A%A4%ED%8A%B8%20%EC%A0%9C%EB%AA%A9` ◀ `http://coderstoolbox.net/string/#!encoding=url&action=encode&charset=utf_8` 사이트를 이용해서 인코딩해주었다!

로 접속해주면 "테스트 제목"이 제목인 컨텐츠들이 표시된다!

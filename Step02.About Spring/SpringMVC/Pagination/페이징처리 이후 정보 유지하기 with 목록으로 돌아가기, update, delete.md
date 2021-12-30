# 페이징처리 이후  정보 유지하기

## 01. /board/read 부분에서 `목록보기`  추가

페이징처리가 추가되면서 

- bno 게시글 번호
- page 페이지 번호
- pageNum 한 페이지당 보여지는 게시물 갯수

를 uri에 담고있어야 한다!

이를 고려해서 다음과 같이 컨트롤러 내에서 read에 대한 메서드 부분을 오버로딩해서 다음과 같이 작성가능하다!

### 01-1. 컨트롤러
```java
		//페이징처리 이후  정보 유지하기
		@RequestMapping(value="/readPage",method=RequestMethod.GET)
		public String read(@RequestParam("bno") int bno,
						  @ModelAttribute("cri") Criteria cri,
						  Model model) {
			
			BoardVO target=new BoardVO();
			
			try {
				target=service.read(bno);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//존재하는 경우
			model.addAttribute("article",target);
			
			return "/board/readPage";
		}
```
위와 같이 컨트롤러에 
- bno 게시글 번호
- page 페이지 번호
- pageNum 한 페이지당 보여지는 게시물 갯수

를 파라미터로 가질 수 있는 메서드를 추가해주자

### 01-2.  /board/read.jsp를 복사해서 /board/readPage.jsp를 수정해주자

(1) form태그 내부에 page,pageNum 정보를 hidden으로 처리하기
```html
		<input type="hidden" name="page" value="${cri.page }"/>
		<input type="hidden" name="pageNum" value="${cri.pageNum }"/>
```

(2)  js 부분에서 primary버튼(목록버튼)을 form 태그 내부 요소를 활용해서 제출하도록 변경해주기
```javascript
		$(".btn-primary").on("click",function(){
			formObj.attr("method","get");
			formObj.attr("action",`<%=request.getContextPath()%>/board/listCri`);
			formObj.submit();
		});
```

```html
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
		var formObj=$("form[role='form']");
		
		console.log("form요소: ",formObj);
		
		$(".btn-warning").on("click",function(){
			formObj.attr("action",`<%=request.getContextPath()%>/board/modify`);
			formObj.attr("method","get");
			formObj.submit();
		});
		
		$(".btn-danger").on("click",function(){
			formObj.attr("action",`<%=request.getContextPath()%>/board/remove`);
			formObj.submit();
		});
		
		$(".btn-primary").on("click",function(){
			formObj.attr("method","get");
			formObj.attr("action",`<%=request.getContextPath()%>/board/listCri`);
			formObj.submit();
		});
	});
</script>
<body>
	<form role="form" action="modify" method="POST">
		<input type="hidden" name="bno" value="${article.bno}"/>
		<input type="hidden" name="page" value="${cri.page }"/>
		<input type="hidden" name="pageNum" value="${cri.pageNum }"/>
	<div class="box-body">
		<div class="form-group">
			<label for="exampleInputEmail1">Title</label>
			<input type="text" name="title" class="form-control" value="${article.title }" readonly/>
		</div>
		<div class="form-group">
			<label for="exampleInputPassword1">Content</label>
			<textarea class="form-control" name="content" rows="3"
				readonly>${article.content }</textarea>
		</div>
		<div class="form-group">
			<label for="exampleInputEmail1">Title</label>
			<input type="text" name="writer" class="form-control" value="${article.writer }" readonly/>
		</div>
	</div>
	</form>
	<div class="box-footer">
		<button type="submit" class="btn btn-warning">수정</button>
		<button type="submit" class="btn btn-danger">삭제</button>
		<button type="submit" class="btn btn-primary">목록</button>
	</div>
	<%@ include file="../include/footer.jsp" %>	
</body>
</html>
```

### 01-3. listCri.jsp에서 readPage.jsp로 이동할수 있도록 href 속성값을 read▶ readPage로 변경해주자

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
	$(function(){
		$(".pagination li a").on("click",function(event){
			
			event.preventDefault();
			
			var targetPage=$(this).attr("href");//href속성 가져오기
			
			
			//form 요소
			var jobForm=$("#jobForm");
			
			//하위 요소 중 name=page인 input값을 targetPage의 값으로 해주기
			jobForm.find("[name='page']").val(targetPage);
			//form의 action,method 값 변경해주기
			jobForm.attr("action",`<%=request.getContextPath()%>/board/listCri`).attr("method","get");
			jobForm.submit();
			
		});
	});
</script>
<body>
	<form id="jobForm">
		<input type="hidden" name="page" value="${pageMaker.cri.pageNum }"/>
		<input type="hidden" name="pageNum" value="${pageMaker.cri.pageNum }"/>
	</form>
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
							<td><a href="<%=request.getContextPath()%>/board/readPage?bno=${item.bno}" title="${item.bno} 게시글 보기" target="_blank">${item.title }</a></td>
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
				<li><a href="${pageMaker.startPage-1}">이전</a></li>
			</c:if>
			<!-- 페이지 번호들 -->
			<c:forEach begin="${pageMaker.startPage}" end="${pageMaker.endPage }" var="idx">
			<!-- 버튼 활성 -->
				<li <c:out value="${pageMaker.cri.page == idx?'class =active':''}"/>>
					<a href="${idx}">${idx }</a>
				</li>
			</c:forEach>
			<c:if test="${pageMaker.next }">
			<!-- 이후페이지 -->
				<li><a href="${pageMaker.endPage+1}">이후</a></li>
			</c:if>
		</ul>
	</div>
	<%@ include file="../include/footer.jsp" %>
</body>
</html>
```

## 02. 수정 페이지와 삭제 페이지의 처리

### 02-1. 삭제 처리

(1) 조회 페이지에서 `삭제` 요청 

(2) 컨트롤러를 거쳐서

(3)  목록페이지로 이동

😍 remove 컨트롤러 내 메서드 오버로딩

```java
		//게시글 삭제 (페이징 정보 유지)
				//게시글 삭제 (페이징 정보 유지)
		@RequestMapping(value="/removePage",method=RequestMethod.POST)
		public String remove(@RequestParam("bno") int bno,
				Criteria cri,
				RedirectAttributes rttr,
				HttpServletRequest req) {
			
			int removeRes=0;
			
			try {
				removeRes=service.remove(bno);
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			rttr.addFlashAttribute("page", cri.getPage());
			rttr.addFlashAttribute("pageNum", cri.getPageNum());
			
			if(removeRes>0) {
				//삭제 성공
				rttr.addFlashAttribute("msg", "success");
			}else {
				//삭제 실패
				rttr.addFlashAttribute("msg", "failed");
			}
			
			logger.info("게시물 삭제 cri:{}",cri);
			
			return "redirect:/board/listCri";
		}
```

😍 /board/readPage.jsp 수정

```javascript
		$(".btn-danger").on("click",function(){
			formObj.attr("method","post");			formObj.attr("action",`<%=request.getContextPath()%>/board/removePage`);
			formObj.submit();
		});
```

이렇게 삭제하면 목록페이지로 성공적으로 도착한다>3<


### 02-2. 수정 처리
(1) 조회 페이지에서 `수정` 요청 

(2) 컨트롤러를 거쳐서

(3) 수정 포맷 페이지로 이동

(4) 수정하기

(5)  목록페이지로 이동

😍 컨트롤러에서 페이징 정보를 담은채, `수정 포맷 페이지`를 반환할 메서드를 추가

```java
		//수정할 내용 원본 보여주기 with paging information
		@RequestMapping(value="/modifyPage",method=RequestMethod.GET)
		public String modifyOriginal(@RequestParam("bno") int bno,
				@ModelAttribute("cri") Criteria cri,
				Model model) {
			BoardVO target=new BoardVO();
			
			try {
				target=service.read(bno);
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			logger.info("수정할 내용 원본:{}",target);
			
			model.addAttribute("article",target);
			
			return "/board/modifyPage";
		}
```
😍 페이징 정보가 유실되지 않도록 적용된 readPage.jsp 페이지를 수정
```javascript
		$(".btn-warning").on("click",function(){
			formObj.attr("action",`<%=request.getContextPath()%>/board/modifyPage`);
			formObj.attr("method","get");
			formObj.submit();
		});
```

😍 modify.jsp를 복사해서 modifyPage.jsp 수정해주기

uri 매핑 부분 변경해주기!

```javascript
	$(".btn-primary").on("click",function(){
		formObj.attr("action",`<%=request.getContextPath()%>/board/modifyPage`);
		formObj.attr("method","post");
		formObj.submit();
	});
	
	$(".btn-warning").on("click",function(){
		formObj.attr("action",`<%=request.getContextPath()%>/board/listCri?page=${cri.page}&pageNum=${cri.pageNum}`);
		formObj.attr("method","get");
		formObj.submit();
	});
```

hidden 값으로 page, pageNum 전달해주기
```html
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
	var formObj=$("form[role='form']");
	
	console.log("form요소: ",formObj);
	
	$(".btn-primary").on("click",function(){
		formObj.attr("action",`<%=request.getContextPath()%>/board/modifyPage`);
		formObj.attr("method","post");
		formObj.submit();
	});
	
	$(".btn-warning").on("click",function(){
		formObj.attr("action",`<%=request.getContextPath()%>/board/listCri?page=${cri.page}&pageNum=${cri.pageNum}`);
		formObj.attr("method","get");
		formObj.submit();
	});
	
	});	
</script>
<body>
	<form role="form" method="post">
		<input type="hidden" name="bno" value="${article.bno}"/>
		<input type="hidden" name="page" value="${cri.page}"/>
		<input type="hidden" name="pageNum" value="${cri.pageNum}"/>
		<div class="box-body">
		<div class="form-group">
			<label for="exampleInputEmail1">Title</label>
			<input type="text" name="title" class="form-control" value="${article.title }"/>
		</div>
		<div class="form-group">
			<label for="exampleInputPassword1">Content</label>
			<textarea class="form-control" name="content" rows="3">${article.content }</textarea>
		</div>
		<div class="form-group">
			<label for="exampleInputEmail1">Title</label>
			<input type="text" name="writer" class="form-control" value="${article.writer }"/>
		</div>
	</div>
	</form>
	<div class="box-footer">
		<button type="submit" class="btn btn-primary">수정완료</button>
		<button type="submit" class="btn btn-warning">목록</button>
	</div>
	<%@ include file="../include/footer.jsp" %> 
</body>
</html>
```

😍 수정 내용을 받을 modifyPage에 따른 POST 메서드 작성 in 컨트롤러!

```java
		//글수정 with paging information
		@RequestMapping(value="/modifyPage",method=RequestMethod.POST)
		public String modifyOriginal(BoardVO board,
				Criteria cri,
				RedirectAttributes rttr) {

			int modRes=0;
			
			try {
				modRes=service.modify(board);
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			logger.info("수정할 내용: {}",board);
			
			rttr.addFlashAttribute("page", cri.getPage());
			rttr.addFlashAttribute("pageNum", cri.getPageNum());
			if(modRes>0) {
				//수정 성공
				rttr.addFlashAttribute("msg", "success");
			}else {
				//수정 실패
				rttr.addFlashAttribute("msg", "failed");
			}
			
			return "redirect:/board/listCri";
		}
```

결과를 확인할 수 있도록 alert 하는 js 코드를 추가!
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
	$(function(){
		$(".pagination li a").on("click",function(event){
			
			event.preventDefault();
			
			var targetPage=$(this).attr("href");//href속성 가져오기
			
			
			//form 요소
			var jobForm=$("#jobForm");
			
			//하위 요소 중 name=page인 input값을 targetPage의 값으로 해주기
			jobForm.find("[name='page']").val(targetPage);
			//form의 action,method 값 변경해주기
			jobForm.attr("action",`<%=request.getContextPath()%>/board/listCri`).attr("method","get");
			jobForm.submit();
			
		});
		
		var result='${msg}';
		
		if(result=="success"){
			alert(`처리가 완료되었습니다.`);
		}else if(result=="failed"){
			alert(`게시글 조회/등록 과정에 문제가 발생했습니다`);
		}
	});
</script>
<body>
	<form id="jobForm">
		<input type="hidden" name="page" value="${pageMaker.cri.pageNum }"/>
		<input type="hidden" name="pageNum" value="${pageMaker.cri.pageNum }"/>
	</form>
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
							<td><a href="<%=request.getContextPath()%>/board/readPage?bno=${item.bno}" title="${item.bno} 게시글 보기" target="_blank">${item.title }</a></td>
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
				<li><a href="${pageMaker.startPage-1}">이전</a></li>
			</c:if>
			<!-- 페이지 번호들 -->
			<c:forEach begin="${pageMaker.startPage}" end="${pageMaker.endPage }" var="idx">
			<!-- 버튼 활성 -->
				<li <c:out value="${pageMaker.cri.page == idx?'class =active':''}"/>>
					<a href="${idx}">${idx }</a>
				</li>
			</c:forEach>
			<c:if test="${pageMaker.next }">
			<!-- 이후페이지 -->
				<li><a href="${pageMaker.endPage+1}">이후</a></li>
			</c:if>
		</ul>
	</div>
	<%@ include file="../include/footer.jsp" %>
</body>
</html>
```



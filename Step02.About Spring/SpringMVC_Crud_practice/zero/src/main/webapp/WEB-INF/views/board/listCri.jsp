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
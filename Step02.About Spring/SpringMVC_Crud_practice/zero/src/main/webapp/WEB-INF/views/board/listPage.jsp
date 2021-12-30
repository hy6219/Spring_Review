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
				<li><a href="listCri${pageMaker.makeQuery(pageMaker.startPage-1)}">이전</a></li>
			</c:if>
			<!-- 페이지 번호들 -->
			<c:forEach begin="${pageMaker.startPage}" end="${pageMaker.endPage }" var="idx">
			<!-- 버튼 활성 -->
				<li <c:out value="${pageMaker.cri.page == idx?'class =active':''}"/>>
					<a href="listCri${pageMaker.makeQuery(idx)}">${idx }</a>
				</li>
			</c:forEach>
			<c:if test="${pageMaker.next }">
			<!-- 이후페이지 -->
				<li><a href="listCri${pageMaker.makeQuery(pageMaker.endPage+1)}">이후</a></li>
			</c:if>
		</ul>
	</div>
	<%@ include file="../include/footer.jsp" %>
</body>
</html>
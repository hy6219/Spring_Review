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
				<c:when test="${empty articles }">
					<tr>
						<td colspan="5">
						---등록된 게시글이 없습니다---
						</td>
					</tr>
				</c:when>
				<c:otherwise>
					<c:forEach items="${articles}" var="item">
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
	<%@ include file="../include/footer.jsp" %>
	<script>
		var result='${msg}';
		
		if(result=="success"){
			alert(`처리가 완료되었습니다.`);
		}else if(result=="failed"){
			alert(`게시글 조회/등록 과정에 문제가 발생했습니다`);
		}
	</script>
</body>
</html>
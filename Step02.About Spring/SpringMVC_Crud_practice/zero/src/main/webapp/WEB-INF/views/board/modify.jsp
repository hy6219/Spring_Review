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
		formObj.attr("action",`<%=request.getContextPath()%>/board/modify`);
		formObj.attr("method","post");
		formObj.submit();
	});
	
	$(".btn-warning").on("click",function(){
		location.href=`<%=request.getContextPath()%>/board/listAll`;
	});
	
	});	
</script>
<body>
	<form role="form" method="post">
		<input type="hidden" name="bno" value="${article.bno}"/>
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
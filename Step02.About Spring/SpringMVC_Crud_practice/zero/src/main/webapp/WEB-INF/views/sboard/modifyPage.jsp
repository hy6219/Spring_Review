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
		formObj.submit();
	});
	
	$(".btn-warning").on("click",function(){
		self.location=`<%=request.getContextPath()%>/sboard/list?page=${cri.page}&pageNum=${cri.pageNum}&searchType=${cri.searchType}&keyword=${cri.keyword}`;
	});
	
	});	
</script>
<body>
	<form role="form" action="modifyPage" method="post">
		<input type="hidden" name="bno" value="${target.bno}"/>
		<input type="hidden" name="page" value="${cri.page}"/>
		<input type="hidden" name="pageNum" value="${cri.pageNum}"/>
		<input type="hidden" name="searchType" value="${cri.searchType }"/>
		<input type="hidden" name="keyword" value="${cri.keyword }"/>
		<div class="box-body">
			<div class="form-group">
				<label for="exampleInputEmail1">Title</label>
				<input type="text" name="title" class="form-control" value="${target.title }"/>
			</div>
			<div class="form-group">
				<label for="exampleInputPassword1">Content</label>
				<textarea class="form-control" name="content" rows="3">${target.content }</textarea>
			</div>
			<div class="form-group">
				<label for="exampleInputEmail1">Title</label>
				<input type="text" name="writer" class="form-control" value="${target.writer }"/>
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
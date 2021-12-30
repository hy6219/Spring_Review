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
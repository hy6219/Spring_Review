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
		
		console.log("form์์: ",formObj);
		
		$(".btn-warning").on("click",function(){
			formObj.attr("action",`<%=request.getContextPath()%>/sboard/modifyPage`);
			formObj.attr("method","get");
			formObj.submit();
		});
		
		$(".btn-danger").on("click",function(){
			formObj.attr("method","post");
			formObj.attr("action",`<%=request.getContextPath()%>/sboard/removePage`);
			formObj.submit();
		});
		
		$(".btn-primary").on("click",function(){
			formObj.attr("method","get");
			formObj.attr("action",`<%=request.getContextPath()%>/sboard/list`);
			formObj.submit();
		});
	});
</script>
<body>
	<form role="form" action="modify" method="POST">
		<input type="hidden" name="bno" value="${target.bno}"/>
		<input type="hidden" name="page" value="${cri.page }"/>
		<input type="hidden" name="pageNum" value="${cri.pageNum }"/>
		<input type="hidden" name="searchType" value="${cri.searchType }"/>
		<input type="hidden" name="keyword" value="${cri.keyword }"/>
	<div class="box-body">
		<div class="form-group">
			<label for="exampleInputEmail1">Title</label>
			<input type="text" name="title" class="form-control" value="${target.title }" readonly/>
		</div>
		<div class="form-group">
			<label for="exampleInputPassword1">Content</label>
			<textarea class="form-control" name="content" rows="3"
				readonly>${target.content }</textarea>
		</div>
		<div class="form-group">
			<label for="exampleInputEmail1">Title</label>
			<input type="text" name="writer" class="form-control" value="${target.writer }" readonly/>
		</div>
	</div>
	</form>
	<div class="box-footer">
		<button type="submit" class="btn btn-warning">์์ </button>
		<button type="submit" class="btn btn-danger">์ญ์ </button>
		<button type="submit" class="btn btn-primary">๋ชฉ๋ก</button>
	</div>
	<%@ include file="../include/footer.jsp" %>	
</body>
</html>
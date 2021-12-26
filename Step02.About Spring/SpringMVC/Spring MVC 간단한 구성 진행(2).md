# Spring MVC 간단한 구성 진행(2)

✨ [Spring MVC 간단한 구성 진행(1)](https://github.com/hy6219/Spring_Review/blob/main/Step02.About%20Spring/SpringMVC/Spring%20MVC%20%EA%B0%84%EB%8B%A8%ED%95%9C%20%EA%B5%AC%EC%84%B1%20%EC%A7%84%ED%96%89(1).md)

이번에는 수정, 삭제 단계까지 진행하고
다음에는 예외처리와 페이징처리를 따로 파트를 정리할 예정이다.

## 01. 글 삭제
앞서 버튼을 눌렀을 때 url(action)값을 변동시키게 했다!
그 중 하나인 글 삭제을 해보자
```java
	//게시글 삭제
	@RequestMapping(value="/remove",method=RequestMethod.POST)
	public String remove(@RequestParam("bno") int bno,RedirectAttributes rttr) {
		int removeRes=0;
		
		try {
			removeRes=service.remove(bno);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		if(removeRes>0) {
			//삭제 성공
			rttr.addFlashAttribute("msg", "success");
		}else {
			//삭제 실패
			rttr.addFlashAttribute("msg", "failed");
		}
		
		return "redirect: /boa
rd/listAll";
	}
```
## 02. 글 수정

먼저, 수정할 내용 원본을 보여줄 페이지를 반환해주자
```java
	//수정할 내용 원본 보여주기
	@RequestMapping(value="/modify",method=RequestMethod.GET)
	public String modifyOriginal(@RequestParam("bno") int bno,Model model) {
		BoardVO target=new BoardVO();
		
		try {
			target=service.read(bno);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		logger.info("수정할 내용 원본:{}",target);
		
		model.addAttribute("article",target);
		
		return "/board/modify";
	}
```

그리고 modify.jsp에서 수정할 내용 원본을 보여주면서 수정하도록 해주자
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
```

그리고 이어서 뷰에서 받아온 내용을 컨트롤러부분에서 처리해주도록 하자

```java
	//글 수정
	@RequestMapping(value="/modify",method=RequestMethod.POST)
	public String modifyBoard(BoardVO board,Model model) {
		
		int modRes=0;
		
		try {
			modRes=service.modify(board);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		logger.info("수정할 내용: {}",board);
		
		if(modRes>0) {
			//수정 성공
			model.addAttribute("msg", "success");
		}else {
			//수정 실패
			model.addAttribute("msg", "failed");
		}
		
		return "redirect:/
board/listAll";
	}
```



<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>

<%@ include file="include/header.jsp" %>

<html>
<head>
    <title>Title</title>
</head>
<body>
    <div class="content">
        <div class="row">
            <div class="col-md-12">
                <div class="box">
                    <div class="box-header with-border">
                        <h3 class="box-title"><a href="<%=request.getContextPath()%>/board/listAll">Home Page</a></h3>
                    </div>
                </div>
            </div>
        </div> <!-- end row -->
    </div> <!-- end content -->

<%@ include file="include/footer.jsp" %>
</body>
</html>
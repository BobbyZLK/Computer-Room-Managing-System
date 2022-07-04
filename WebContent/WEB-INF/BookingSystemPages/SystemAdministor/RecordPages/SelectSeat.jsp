<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>选择机位</title>
<script type="text/javascript">
function goBack(){
	window.history.go(-1);
}
</script>
</head>
<body>
<h2>选择机位</h2>
机房：<input type="text" value="${roomName }" disabled="disabled"><br/>
<input type="button" value="返回" onclick="goBack()"><br/><br/>
<table border="1">
<tr><th>机位名</th><th>备注</th><th>操作</th></tr>
<c:forEach items="${seats }" var="seat">
<tr>
    <td>${seat.key.seatName }</td>
    
    <c:if test="${seat.value eq '可用'}">
    <td style="color:#00FF00;">${seat.value }</td></c:if>
    <c:if test="${seat.value!='可用' }">
    <td style="color:grey;">${seat.value }</td></c:if>
    
    <c:if test="${seat.value eq '可用'}">
    <td><a href="${pageContext.request.contextPath }/admin/record/toselectstudent?seatId=${seat.key.id}" style="color:blue;">选择</a></td></c:if>
    <c:if test="${seat.value!='可用' }">
    <td style="color:grey;">选择</td></c:if>

</tr>
</c:forEach>
</table>
</body>
</html>
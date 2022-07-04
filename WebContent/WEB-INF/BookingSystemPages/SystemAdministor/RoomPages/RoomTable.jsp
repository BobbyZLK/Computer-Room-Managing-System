<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>机房管理</title>
</head>
<head><script>
function jumpAdd(){
 window.location.href="${pageContext.request.contextPath }/admin/room/toadd";
	
}
function confirmReboot(){
	return confirm("确定启用？启用前请确认机房状态");
}

function confirmBan(){
	return confirm("是否禁用该房间？禁用后除启用外不允许任何操作");
}
function confirmDelete(){
	return confirm("确定要删除吗？删除会将机房内所有机位一并删除，请谨慎考虑！！！");
}
function backMainTable(){
	window.location.href="${pageContext.request.contextPath }/admin/tomaintable";
}
</script></head>
<body>
<h2>机房管理&nbsp;&nbsp;制作：张凌凯</h2>
<button onclick="jumpAdd()">新建</button>&nbsp;&nbsp;
<button onclick="backMainTable()">管理员主页</button><br/><br/>
<table border=1>
<tr><th>机房名</th><th>是否被禁用</th><th>开门时间</th><th>关门时间</th><th>操作</th></tr>
<c:forEach items="${page.results}" var="room">
<tr>
<td>${room.roomName}</td>

<c:if test="${false==room.isBanned }">
<td style="color:#00FF00;">正常</td></c:if>
<c:if test="${true==room.isBanned }">
<td style="color:orange;">被禁用</td></c:if>

<td>${room.openTime}</td><td>${room.closeTime }</td>

<td>

<c:if test="${false==room.isBanned }">
<a href="${pageContext.request.contextPath }/admin/room/toupdate/${room.id }"  style="color:blue;" >编辑</a></c:if> 
<c:if test="${true==room.isBanned }">
<a style="color:grey;">编辑</a></c:if>

<c:if test="${false==room.isBanned }">
<a href="${pageContext.request.contextPath }/admin/seat/totable?roomId=${room.id }" style="color:black;">进入机房</a></c:if>
<c:if test="${true==room.isBanned }">
<a style="color:grey;">进入机房</a></c:if>

<c:if test="${false==room.isBanned }">
<a href="${pageContext.request.contextPath }/admin/room/banroom?id=${room.id }" style="color:orange;" onclick="return confirmBan();">禁用</a></c:if>  
<c:if test="${true==room.isBanned }">
<a href="${pageContext.request.contextPath }/admin/room/banroom?id=${room.id }" style="color:#00FF00;" onclick="return confirmReboot();">启用</a></c:if>  

<c:if test="${false==room.isBanned }">
<a href="${pageContext.request.contextPath }/admin/room/confirmdelete?id=${room.id}" style="color:red;" onclick="return confirmDelete();" >删除</a></c:if>
<c:if test="${true==room.isBanned }">
<a style="color:grey;">删除</a></c:if>

</td>
</tr>
</c:forEach>
</table><br>

<c:if test="${page.totalPages<=5 }">
<c:if test="${page.nowPage==1 }">上一页</c:if>
<c:if test="${page.nowPage>1 }">
<a href="${pageContext.request.contextPath }/admin/room/totable?page=${page.nowPage-1}">上一页</a></c:if>

<c:forEach begin="1" end="${page.totalPages }" step="1" var="i">
<c:if test="${i==page.nowPage }">&nbsp;${i }</c:if>
<c:if test="${i!=page.nowPage }"><a href="${pageContext.request.contextPath }/admin/room/totable?page=${i}">&nbsp;${i }</a></c:if>
</c:forEach>

<c:if test="${page.nowPage==page.totalPages }">下一页</c:if>
<c:if test="${page.nowPage<page.totalPages }">
<a href="${pageContext.request.contextPath }/admin/room/totable?page=${page.nowPage+1}">下一页</a></c:if></c:if>



<c:if test="${page.totalPages>5 }">
<c:if test="${page.nowPage==1 }">上一页</c:if>
<c:if test="${page.nowPage>1 }">
<a href="${pageContext.request.contextPath }/admin/room/totable?page=${page.nowPage-1}">上一页</a></c:if>

<c:if test="${page.nowPage<=2 }">
<c:forEach begin="1" end="5" step="1" var="i">
<c:if test="${i==page.nowPage }">&nbsp;${i }</c:if>
<c:if test="${i!=page.nowPage }"><a href="${pageContext.request.contextPath }/admin/room/totable?page=${i}">&nbsp;${i }</a></c:if>
</c:forEach></c:if>

<c:if test="${page.nowPage>=page.totalPages-1 }">
<c:forEach begin="${page.totalPages-4 }" end="${page.totalPages }" step="1" var="i">
<c:if test="${i==page.nowPage }">&nbsp;${i }</c:if>
<c:if test="${i!=page.nowPage }"><a href="${pageContext.request.contextPath }/admin/room/totable?page=${i}">&nbsp;${i }</a></c:if>
</c:forEach></c:if>

<c:if test="${page.nowPage>2 && page.nowPage<page.totalPages-1 }">
<c:forEach begin="${page.nowPage-2 }" end="${page.nowPage+2 }" step="1" var="i">
<c:if test="${i==page.nowPage }">&nbsp;${i }</c:if>
<c:if test="${i!=page.nowPage }"><a href="${pageContext.request.contextPath }/admin/room/totable?page=${i}">&nbsp;${i }</a></c:if>
</c:forEach></c:if>

<c:if test="${page.nowPage==page.totalPages }">下一页</c:if>
<c:if test="${page.nowPage<page.totalPages }">
<a href="${pageContext.request.contextPath }/admin/room/totable?page=${page.nowPage+1}">下一页</a></c:if></c:if>

</body>
</html>
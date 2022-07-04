<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>机位管理</title>

<script type="text/javascript">
function jumpAdd(){	
	 window.location.href="${pageContext.request.contextPath }/admin/seat/toadd?roomId=${currentRoomId}";
	}
function roomTable(){
	 window.location.href="${pageContext.request.contextPath }/admin/room/totable";
	}
function searchRoom(){
	document.getElementById('chooseRoom').submit();
	}
function confirmReboot(){
	return confirm("确定启用？启用前请确认机位状态");
}
function confirmBan(){
	return confirm("是否禁用该机位？禁用后除启用外不允许任何操作");
}
function confirmDelete(){
	return confirm("确定要删除吗？");
}
function mainTable(){
	window.location.href="${pageContext.request.contextPath }/admin/tomaintable";
}
</script>
</head>
<body>
<h2>机位管理&nbsp;&nbsp;制作：张凌凯</h2>

<form action="${pageContext.request.contextPath }/admin/seat/totable?" id="chooseRoom">
<select name="roomId" onchange="searchRoom()">
<option value="-1">全部</option>
<c:forEach items="${rooms }" var="room" >
<option value="${room.id }" 
<c:if test="${room.id==currentRoomId }">selected</c:if>>
${room.roomName }
</option>
</c:forEach>
</select>
<input type="hidden" name="page" value="1">
</form>

<input type="button" value="新建" onclick="jumpAdd()">
<input type="button" value="机房主页" onclick="roomTable()"> 
<button onclick="mainTable()">管理员主页</button><br/><br/>
 
<table border=1>
<tr><th>机位名</th><th>所在机房<th>是否被禁用</th><th>操作</th></tr>

<c:forEach items="${page.results }" var="seat">
<tr>
<td>${seat.seatName }</td>

<c:if test="${seat.room==null }">
<td style="color:grey;">待分配</td>
</c:if>
<c:if test="${seat.room!=null }">
<td>${seat.room.roomName }</td>
</c:if>

<c:if test="${seat.isBanned==false }">
<c:if test="${seat.room!=null }">
<td style="color:#00FF00;">正常</td></c:if>
<c:if test="${seat.room==null }">
<td style="color:grey;">---</td></c:if>
</c:if>
<c:if test="${seat.isBanned==true }">
<td style="color:orange;">被禁用</td></c:if>

<td>
<c:if test="${seat.isBanned==false}">
<a href="${pageContext.request.contextPath }/admin/seat/toupdate/${seat.id }" style="color:blue;" >编辑</a></c:if>  
<c:if test="${seat.isBanned==true }">
<a style="color:grey;">编辑</a></c:if>

<c:if test="${seat.isBanned==false}">
<c:if test="${seat.room!=null }">
<a href="${pageContext.request.contextPath }/admin/seat/banseat?id=${seat.id }" style="color:orange;"  onclick="return confirmBan();">禁用</a></c:if>
<c:if test="${seat.room==null }">
<a style="color:grey;">禁用</a></c:if>
</c:if>  
<c:if test="${seat.isBanned==true }">
<a href="${pageContext.request.contextPath }/admin/seat/banseat?id=${seat.id }" style="color:#00FF00;" onclick="return confirmReboot();">启用</a></c:if> 

<c:if test="${seat.isBanned==false}">
<a  href="${pageContext.request.contextPath }/admin/seat/confirmdelete?id=${seat.id }" style="color:red;" onclick="return confirmDelete();" >删除</a></c:if>
<c:if test="${seat.isBanned==true }">
<a style="color:grey;">删除</a></c:if>

</td></tr></c:forEach>
</table><br>

<c:if test="${page.totalPages<=5 }">
<c:if test="${page.nowPage==1 }">上一页</c:if>
<c:if test="${page.nowPage>1 }">
<a href="${pageContext.request.contextPath }/admin/seat/totable?roomId=${currentRoomId }&page=${page.nowPage-1}">上一页</a></c:if>

<c:forEach begin="1" end="${page.totalPages }" step="1" var="i">
<c:if test="${i==page.nowPage }">&nbsp;${i }</c:if>
<c:if test="${i!=page.nowPage }"><a href="${pageContext.request.contextPath }/admin/seat/totable?roomId=${currentRoomId}&page=${i}">&nbsp;${i }</a></c:if>
</c:forEach>

<c:if test="${page.nowPage==page.totalPages }">下一页</c:if>
<c:if test="${page.nowPage<page.totalPages }">
<a href="${pageContext.request.contextPath }/admin/seat/totable?roomId=${currentRoomId }&page=${page.nowPage+1}">下一页</a></c:if></c:if>



<c:if test="${page.totalPages>5 }">
<c:if test="${page.nowPage==1 }">上一页</c:if>
<c:if test="${page.nowPage>1 }">
<a href="${pageContext.request.contextPath }/admin/seat/totable?roomId=${currentRoomId }&page=${page.nowPage-1}">上一页</a></c:if>

<c:if test="${page.nowPage<=2 }">
<c:forEach begin="1" end="5" step="1" var="i">
<c:if test="${i==nowPage }">&nbsp;${i }</c:if>
<c:if test="${i!=nowPage }"><a href="${pageContext.request.contextPath }/admin/seat/totable?roomId=${currentRoomId }&page=${i}">&nbsp;${i }</a></c:if>
</c:forEach></c:if>

<c:if test="${page.nowPage>=page.totalPages-1 }">
<c:forEach begin="${page.totalPages-4 }" end="${page.totalPages }" step="1" var="i">
<c:if test="${i==nowPage }">&nbsp;${i }</c:if>
<c:if test="${i!=nowPage }"><a href="${pageContext.request.contextPath }/admin/seat/totable?roomId=${currentRoomId}&page=${i}">&nbsp;${i }</a></c:if>
</c:forEach></c:if>

<c:if test="${page.nowPage>2 && page.nowPage<page.totalPages-1 }">
<c:forEach begin="${page.nowPage-2 }" end="${page.nowPage+2 }" step="1" var="i">
<c:if test="${i==nowPage }">&nbsp;${i }</c:if>
<c:if test="${i!=nowPage }"><a href="${pageContext.request.contextPath }/admin/seat/totable?roomId=${currentRoomId}&page=${i}">&nbsp;${i }</a></c:if>
</c:forEach></c:if>

<c:if test="${page.nowPage==page.totalPages }">下一页</c:if>
<c:if test="${page.nowPage<page.totalPages }">
<a href="${pageContext.request.contextPath }/admin/seat/totable?roomId=${currentRoomId }&page=${page.nowPage+1}">下一页</a></c:if></c:if>

</body>
</html>
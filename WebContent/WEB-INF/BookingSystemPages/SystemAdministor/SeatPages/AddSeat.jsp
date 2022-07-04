<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>新建机位</title>
</head>
<head><script>
function backTable(){
 window.location.href="${pageContext.request.contextPath }/admin/seat/totable?roomId=${roomId}";
}
</script></head>
<body>
<h3>新建机位</h3>
<form action="${pageContext.request.contextPath }/admin/seat/confirmsave" method="get">

机位名：<input type="text" name="seatName" ><br/><br/>

所在机房：
<select id="roomId" name="room.id" >
<option value="-1">待分配</option>
<c:forEach items="${rooms }" var="room" >
<option value="${room.id }" 
<c:if test="${room.id==roomId }">selected</c:if>>
${room.roomName }
</option>
</c:forEach>
</select>

<input type="hidden" value="false" name="isBanned"/>
<input type="hidden" value="空闲" name="seatCondition"/>
<input type="submit" value="确定" >
<input type="button" value="取消" onclick="backTable()">
</form>
</body>
</html>
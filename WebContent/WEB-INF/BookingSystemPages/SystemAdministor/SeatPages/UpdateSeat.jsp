<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>编辑机位</title>
</head>
<head>
<script type="text/javascript">

function backTable(){
	 window.location.href="${pageContext.request.contextPath }/admin/seat/totable?roomId=${roomId}";
	}
</script>
</head>
<body>
<h3>编辑机位</h3>
<form action="${pageContext.request.contextPath }/admin/seat/confirmsave" method="get">
机位名：<input type="text" name="seatName" value="${seat.seatName }" ><br/>
所在机房：
<select id="roomId" name="room.id" >
<option value="-1">待分配</option>
<c:forEach items="${rooms }" var="room" >
<option value="${room.id }" 
<c:if test="${room.id==seat.room.id }">selected</c:if>>
${room.roomName }
</option>
</c:forEach>
</select>
<input type="hidden" value="${seat.id }" name="id"/>
<input type="hidden" value="${seat.isBanned }" name="isBanned"/>
<input type="hidden" value="${seat.seatCondition }" name="seatCondition"/>
<input type="submit" value="确定" >
<input type="button" value="取消" onclick="backTable()">
</form>
</body>
</html>
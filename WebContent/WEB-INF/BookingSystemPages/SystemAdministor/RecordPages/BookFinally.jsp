<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>确认预约</title>
</head>
<body>
<h1>确认预约</h1>
<p> 日期：<fmt:formatDate value="${record.reserveDay }" pattern="yyyy-MM-dd"/><br>
    开始时间：${record.startingTime }时 &nbsp;&nbsp; 结束时间：${record.endingTime }时<br>
    预约人：<c:if test="${record.student==null }">管理员</c:if>
           <c:if test="${record.student!=null }">${record.student.studentName }</c:if><br>
    机房：${record.room.roomName } &nbsp;&nbsp; 机位：${record.seat.seatName }<br><br>
    备注（如有）：</p>    
<form action="${pageContext.request.contextPath }/admin/record/tofinallybook">
<textarea rows="4" cols="40" name="remark" ></textarea><br>
<input type="submit" value="确定">
<input type="button" value="取消" onclick="goBack()">
</form>
</body>
<script type="text/javascript">
function goBack(){
	window.history.go(-1);
}
</script>
</html>
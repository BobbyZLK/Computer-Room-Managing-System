<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>编辑机房</title>
</head>
<head><script>
function backTable(){
 window.location.href="${pageContext.request.contextPath }/admin/room/totable";
}
</script></head>
<body>
<h2>编辑机房</h2>
<form action="${pageContext.request.contextPath }/admin/room/confirmsave" method="get">
机房名：<input type="text" name="roomName" value="${room.roomName }"><br/>
开放时间(24小时制)：<input type="text" id="ot" name="openTime"  value="${room.openTime }" ><br>
关闭时间(24小时制)：<input type="text" id="ct" name="closeTime"  value="${room.closeTime }"><br>
<input type="hidden" value="${room.id }" name="id"/>
<input type="hidden" value="${room.isBanned }" name="isBanned"/>
<input type="hidden" value="${room.roomCondition }" name="roomCondition"/>
<input type="submit" value="确定" >
<input type="button" value="取消" onclick="backTable()">
</form>

</body>
</html>
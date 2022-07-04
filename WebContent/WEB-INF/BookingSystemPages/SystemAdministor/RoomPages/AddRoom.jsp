<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>新建机房</title>
</head>
<head><script>
function backTable(){
 window.location.href="${pageContext.request.contextPath }/admin/room/totable";
}
</script></head>

<body>
<h3>新建机房</h3>
<form action="${pageContext.request.contextPath }/admin/room/confirmsave" method="get">
机房名：<input type="text" name="roomName" ><br/>
开放时间(24小时制)：<input type="text" id="ot" name="openTime"  value="0" ><br>
关闭时间(24小时制)：<input type="text" id="ct" name="closeTime"  value="24"><br>
<input type="hidden" value="false" name="isBanned"/>
<input type="hidden" value="空闲" name="roomCondition"/>
<input type="submit" value="确定" >
<input type="button" value="取消" onclick="backTable()">
</form>

</body>
</html>
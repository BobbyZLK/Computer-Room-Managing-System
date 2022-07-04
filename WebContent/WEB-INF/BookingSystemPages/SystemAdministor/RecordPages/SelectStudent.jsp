<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>选择账户</title>
</head>
<body>
<h2>选择账户</h2>
<button onclick="goBack()">返回</button>&nbsp;&nbsp;
<button onclick="noStudent()">管理员预约（无用户）</button>
<table border="1">
<tr><th>账号</th><th>学生名</th><th>性别</th><th>本月剩余上机时长</th><th>备注</th><th>操作</th></tr>
<c:forEach items="${students}" var="student">
<tr>
    <td>${student.key.accountName }</td>
    <td>${student.key.studentName }</td>
    <td>${student.key.studentSex }</td>
    
    <c:if test="${student.key.remainingHours>=0}">
    <td><fmt:formatNumber value="${(student.key.remainingHours-student.key.remainingHours%24)/24}" pattern="#"/>天
	    <fmt:formatNumber value="${student.key.remainingHours%24}" pattern="#"/>小时</td></c:if>
	<c:if test="${student.key.remainingHours==-999}">
	<td>无限制</td></c:if>
	
	<c:if test="${student.value eq '可预约'}">
	<td style="color:#00FF00;">可预约</td></c:if>
	<c:if test="${student.value!='可预约' }">
	<td style="color:grey;">${student.value }</td></c:if>
	    
	<c:if test="${student.value eq '可预约'}">
	<td><a href="${pageContext.request.contextPath }/admin/record/toconfirmrecord?studentId=${student.key.id}">确定</a></c:if>
	<c:if test="${student.value!='可预约' }">
	<td style="color:grey;">确定</td></c:if>

</c:forEach>
</table>
</body>
<script type="text/javascript">
function goBack(){
	window.history.go(-1);
	}
function noStudent(){
	window.location.href="${pageContext.request.contextPath }/admin/record/toconfirmrecord";
}
</script>
</html>
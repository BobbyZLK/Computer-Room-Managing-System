<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>账户管理</title>
<script>
function jumpAdd(){
	window.location.href="${pageContext.request.contextPath }/admin/student/toadd";
}
function confirmBan(){
	return confirm("是否封禁账户？封禁后无法进行其他操作且该账号将无法登录！");
}
function confirmReboot(){
	return confirm("确定解禁该账户？");
}
function confirmDelete(){
	return confirm("确定要删除账户吗？所有账户的相关记录都将一并删除！！");
}
function jumpUDMaxTime(){
	window.location.href="${pageContext.request.contextPath }/admin/student/maxforall";
}
function jumpSearch(){
	window.location.href="${pageContext.request.contextPath }/admin/student/toquery";
}
function backMainTable(){
	window.location.href="${pageContext.request.contextPath }/admin/tomaintable";
}
function cleanSearch(){
	document.getElementById("account").value="";
	document.getElementById("student").value="";
}
</script>
</head>
<body>
<h2>账户管理&nbsp;&nbsp;制作：张凌凯</h2>
<button onclick="jumpAdd()">新建</button>
<button onclick="jumpUDMaxTime()">全体上机时长设置</button>
<button onclick="backMainTable()">管理员主页</button>
<br/><br>
<form action="${pageContext.request.contextPath }/admin/student/totable" method="get">
查询账户<br>
账号：<input id="account" type="text" name="queryAccount" value="${query.queryAccount }">&nbsp;&nbsp;
学生名：<input id="student" type="text" name="queryStudent" value="${query.queryStudent }"><br/>
<input type="hidden" name="nowPage" value="1">
<input type="submit" value="搜索">&nbsp;
<input type="button" value="清空" onclick="cleanSearch()">
</form><br><br>
 <table border=1>
<tr><th>账号</th><th>密码</th><th>学生名</th><th>性别</th>
<th>本月剩余上机时长</th><th>找回用验证码</th><th>账号状态</th>
<th>本月上机时长上限</th><th>上次登录时间</th><th>操作</th></tr>

<c:forEach items="${page.results }" var="student">
<tr><td>${student.accountName }</td>
	<td>${student.accountKey }</td>
	<td>${student.studentName }</td>
	<td>${student.studentSex }</td>
	
	<c:if test="${student.remainingHours>=999 }">
	<td>无限制</td></c:if>
	<c:if test="${student.remainingHours<0 }">
	<td style="color:red;">已超时</td></c:if>
	<c:if test="${student.remainingHours>0 && student.remainingHours<999 }">
	<td><fmt:formatNumber value="${(student.remainingHours-student.remainingHours%24)/24}" pattern="#"/>天
	    <fmt:formatNumber value="${student.remainingHours%24}" pattern="#"/>小时</td></c:if>

    <td>${student.referringKey }</td>
    
	<c:if test="${student.isBanned==false }">
	<td style="color:#00FF00;">正常</td></c:if>
	<c:if test="${student.isBanned==true }">
	<td style="color:orange;">封禁中</td></c:if> 
	
	<c:if test="${student.maxHours>=999 }">
	<td>无限制</td></c:if>
	<c:if test="${student.maxHours>=0 && student.maxHours<999}">
	<c:if test="${student.maxHours<24 }">
	<td>${student.maxHours }小时</td></c:if>
	<c:if test="${student.maxHours>=24 }">
	<td><fmt:formatNumber value="${(student.maxHours-student.maxHours%24)/24}" pattern="#"/>天
	    <fmt:formatNumber value="${student.maxHours%24}" pattern="#"/>小时</td></c:if></c:if>
	
	<c:if test="${empty student.logInDate}">
	<td>暂无记录</td></c:if>
	<c:if test="${not empty student.logInDate }">
	<td>${student.logInDate }</td></c:if>
	
	<td>
	<c:if test="${student.isBanned==false }">
	<a href="${pageContext.request.contextPath }/admin/student/toupdate/${student.id }" style="color:blue;">编辑</a></c:if>
	<c:if test="${student.isBanned==true }">
	<a style="color:grey">编辑</a></c:if>
	
	<c:if test="${student.isBanned==false }">
	<a href="${pageContext.request.contextPath }/admin/student/banstudent?id=${student.id}" style="color:orange;" onclick="return confirmBan();">封禁</a></c:if>
	<c:if test="${student.isBanned==true }">
	<a href="${pageContext.request.contextPath }/admin/student/banstudent?id=${student.id}" style="color:#00FF00;" onclick="return confirmReboot();">解禁</a></c:if>
	
	<c:if test="${student.isBanned==false }">
	<a href="${pageContext.request.contextPath }/admin/student/confirmdelete?id=${student.id}" style="color:red;" onclick="return confirmDelete();">删除</a></c:if>
	<c:if test="${student.isBanned==true }">
	<a style="color:grey">删除</a></c:if>
    
	</td>
</tr></c:forEach>	
</table><br>
<c:if test="${page.totalPages<=5 }">
<c:if test="${page.nowPage==1 }">上一页</c:if>
<c:if test="${page.nowPage>1 }">
<a href="${pageContext.request.contextPath }/admin/student/totable?nowPage=${page.nowPage-1}">上一页</a></c:if>

<c:forEach begin="1" end="${page.totalPages }" step="1" var="i">
<c:if test="${i==page.nowPage }">&nbsp;${i }</c:if>
<c:if test="${i!=page.nowPage }"><a href="${pageContext.request.contextPath }/admin/student/totable?nowPage=${i}">&nbsp;${i }</a></c:if>
</c:forEach>

<c:if test="${page.nowPage==page.totalPages }">下一页</c:if>
<c:if test="${page.nowPage<page.totalPages }">
<a href="${pageContext.request.contextPath }/admin/student/totable?nowPage=${page.nowPage+1}">下一页</a></c:if></c:if>





<c:if test="${page.totalPages>5 }">
<c:if test="${page.nowPage==1 }">上一页</c:if>
<c:if test="${page.nowPage>1 }">
<a href="${pageContext.request.contextPath }/admin/student/totable?nowPage=${page.nowPage-1}">上一页</a></c:if>

<c:if test="${page.nowPage<=2 }">
<c:forEach begin="1" end="5" step="1" var="i">
<c:if test="${i==page.nowPage }">&nbsp;${i }</c:if>
<c:if test="${i!=page.nowPage }"><a href="${pageContext.request.contextPath }/admin/student/totable?nowPage=${i}">&nbsp;${i }</a></c:if>
</c:forEach></c:if>

<c:if test="${page.nowPage>=page.totalPages-1 }">
<c:forEach begin="${page.totalPages-4 }" end="${page.totalPages }" step="1" var="i">
<c:if test="${i==page.nowPage }">&nbsp;${i }</c:if>
<c:if test="${i!=page.nowPage }"><a href="${pageContext.request.contextPath }/admin/student/totable?nowPage=${i}">&nbsp;${i }</a></c:if>
</c:forEach></c:if>

<c:if test="${page.nowPage>2 && page.nowPage<page.totalPages-1 }">
<c:forEach begin="${page.nowPage-2 }" end="${page.nowPage+2 }" step="1" var="i">
<c:if test="${i==page.nowPage }">&nbsp;${i }</c:if>
<c:if test="${i!=page.nowPage }"><a href="${pageContext.request.contextPath }/admin/student/totable?nowPage=${i}">&nbsp;${i }</a></c:if>
</c:forEach></c:if>

<c:if test="${page.nowPage==page.totalPages }">下一页</c:if>
<c:if test="${page.nowPage!=page.totalPages }">
<a href="${pageContext.request.contextPath }/admin/student/totable?nowPage=${page.nowPage+1}">下一页</a></c:if></c:if>

</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
 <%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>记录管理</title>
<script type="text/javascript">
function jumpAdd(){
	window.location.href="${pageContext.request.contextPath }/admin/record/toadd";
}
function confirmCancel(){
	return confirm("确定要取消吗?");
}
function jumpSearch(){
	window.location.href="${pageContext.request.contextPath }/admin/record/tosearch";
}
function backMainTable(){
	window.location.href="${pageContext.request.contextPath }/admin/tomaintable";
}
function openSeat(){
	if(document.getElementById("roomName").value!=""){
		document.getElementById("seatName").removeAttribute('disabled');
	}else{
		document.getElementById("seatName").value="";
		document.getElementById("seatName").setAttribute('disabled','disabled');
	}
}
function cleanSearch(){
	document.getElementById("roomName").value="";
	document.getElementById("seatName").value="";
	document.getElementById("accountName").value="";
	document.getElementById("studentName").value="";
}
</script>
</head>
<body>
<h2>记录管理&nbsp;&nbsp;制作：张凌凯</h2>
<button onclick="jumpAdd()">现在预约</button>&nbsp;&nbsp;
<button onclick="backMainTable()">管理员主页</button><br/><br/>

<form action="${pageContext.request.contextPath }/admin/record/totable" method="get">
按机房：<input id="roomName" type="text" name="roomName" value="${query.roomName }" onchange="openSeat()">&nbsp;&nbsp;

<c:choose>
<c:when test="${empty query.roomName }">
按机位：<input id="seatName" type="text" name="seatName" disabled="disabled"><br/><br></c:when>
<c:otherwise>
按机位：<input id="seatName" type="text" name="seatName" value="${query.seatName }"><br/><br></c:otherwise>
</c:choose>

按账号：<input id="accountName" type="text" name="accountName" value="${query.accountName }"><br/><br/>
按姓名：<input id="studentName" type="text" name="studentName" value="${query.studentName }"><br><br>

<input type="hidden" name="nowPage" value="1">
<input type="submit" value="搜索">&nbsp;
<input type="button" value="清空" onclick="cleanSearch()">
</form>

<table border="1">
<tr><th>开始时间</th><th>结束时间</th><th>机房</th><th>机位</th><th>预约账号</th><th>预约人</th><th>备注</th><th>预约状态</th><th>操作</th></tr>
<c:forEach items="${page.results }" var="record">
<tr>
	      <td>${record.startingTime}</td>
	      <td>${record.endingTime}</td>
	      <td>${record.room.roomName }</td>
	      
	      <c:choose>
	      <c:when test="${record.seat!=null }">
	      <td>${record.seat.seatName }</td></c:when>
	      <c:otherwise><td>所有机位</td></c:otherwise>
	      </c:choose>
	      
	      <c:choose>
	      <c:when test="${record.student!=null }">
	      <td>${record.student.accountName }</td></c:when>
	      <c:otherwise><td>管理员预定</td></c:otherwise>
	      </c:choose>
	      
	      <c:choose>
	      <c:when test="${record.student!=null }">
	      <td>${record.student.studentName }</td></c:when>
	      <c:otherwise><td>---</td></c:otherwise>
	      </c:choose>
	      
	      <c:choose>
	      <c:when test="${record.remark!=null }">
	      <td>${record.remark }</td></c:when>
	      <c:otherwise><td>---</td></c:otherwise>
	      </c:choose>
	      
	      <c:if test="${record.recordCondition==0 }">
	      <c:choose>
	      <c:when test="${record.startingTime>Bjnow }">
	      <td style="color:#00FF00;">未开始</td>
	      <td><a style="color:red;" href="${pageContext.request.contextPath }/admin/record/cancel?id=${record.id}" onclick="confirmCancel()">取消预约</a></td></c:when>
	      
	      <c:when test="${record.endingTime<Bjnow }">
	      <td style="color:grey;">已结束</td>
	      <td><a href="${pageContext.request.contextPath }/admin/record/confirmdelete?id=${record.id}">删除记录</a></td></c:when>
	      
	      <c:otherwise><td style="color:orange;">进行中</td>
	      <td><a style="color:red;" href="${pageContext.request.contextPath }/admin/record/cancel?id=${record.id}" onclick="confirmCancel()">取消预约</a></td></c:otherwise></c:choose></c:if>
	      
	      <c:if test="${record.recordCondition!=0 }">
	      <td style="color:grey;">已取消</td>
	      <td><a href="${pageContext.request.contextPath }/admin/record/confirmdelete?id=${record.id}">删除记录</a></td></c:if>
	      
	          
	  </tr>
</c:forEach>
</table><br>

<c:if test="${page.nowPage==1 }">首页 &nbsp;上一页</c:if>
<c:if test="${page.nowPage>1 }">
<a href="${pageContext.request.contextPath }/admin/record/totable?nowPage=1">首页</a>
<a href="${pageContext.request.contextPath }/admin/record/totable?nowPage=${page.nowPage-1}">&nbsp;上一页</a></c:if>


<c:if test="${page.totalPages<5 }">
<c:forEach begin="1" end="${page.totalPages }" step="1" var="i">
<c:if test="${page.nowPage==i }">&nbsp;${i }</c:if>
<c:if test="${page.nowPage!=i }"><a href="${pageContext.request.contextPath }/admin/record/totable?nowPage=${i}">&nbsp;${i }</a></c:if>
</c:forEach></c:if>

<c:if test="${page.totalPages>=5 }">
<c:if test="${page.nowPage<=2 }">
<c:forEach begin="1" end="5" step="1" var="i">
<c:if test="${page.nowPage==i }">&nbsp;${i }</c:if>
<c:if test="${page.nowPage!=i }"><a href="${pageContext.request.contextPath }/admin/record/totable?nowPage=${i}">&nbsp;${i }</a></c:if>
</c:forEach></c:if>

<c:if test="${page.nowPage>=page.totalPages-1 }">
<c:forEach begin="${page.totalPages-4 }" end="${page.totalPages }" step="1" var="i">
<c:if test="${page.nowPage==i }">&nbsp;${i }</c:if>
<c:if test="${page.nowPage!=i }"><a href="${pageContext.request.contextPath }/admin/record/totable?nowPage=${i}">&nbsp;${i }</a></c:if>
</c:forEach></c:if>

<c:if test="${page.nowPage>2 && page.nowPage<page.totalPages-1 }">
<c:forEach begin="${page.nowPage-2 }" end="${page.nowPage+2 }" step="1" var="i">
<c:if test="${page.nowPage==i }">&nbsp;${i }</c:if>
<c:if test="${page.nowPage!=i }"><a href="${pageContext.request.contextPath }/admin/record/totable?nowPage=${i}">&nbsp;${i }</a></c:if>
</c:forEach></c:if></c:if>


<c:if test="${page.nowPage==page.totalPages }">下一页 &nbsp;尾页</c:if>
<c:if test="${page.nowPage<page.totalPages }">
<a href="${pageContext.request.contextPath }/admin/record/totable?nowPage=${page.nowPage+1}">下一页</a>
<a href="${pageContext.request.contextPath }/admin/record/totable?nowPage=${page.totalPages}">&nbsp;尾页</a></c:if>

</body>
</html>
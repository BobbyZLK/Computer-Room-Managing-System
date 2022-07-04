<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>选择机房</title>
</head>
<body>
<h2>选择机房</h2>
<form action="${pageContext.request.contextPath }/admin/record/toadd" method="get">
选择日期（仅限当前月份）：  <input type="text" value="${time.start.month.value }" disabled="disabled">月
<input id="day" type="text" name="day" value="${time.start.dayOfMonth }" onchange="checkDay()">日<br/>
开始时间（最早从下一小时开始）： <input id="startingHour" type="text" name="startingHour" value="${time.start.hour }" onchange="checkHour()">时&nbsp;
使用时长： <input id="lastingHours" type="text" name="lastingHours" value="${time.lastingHours }" onchange="checkLastingHours()">小时<br>
机房名：<input name="queryName" type="text" value="${roomQuery.queryName }"><br>
<input type="hidden" name="nowPage" value="1">

<input type="submit" value="查询">&nbsp;
<input type="button" value="返回" onclick="backTable()">          
</form><br/>

<table border="1" >
<tr><th>机房名</th><th>开门时间</th><th>关门时间</th><th>状态</th><th>操作</th></tr>
<c:forEach items="${page.results }" var="room">
<tr>
    <td>${room.entity.roomName }</td>
    <td>${room.entity.openTime }时</td>
    <td>${room.entity.closeTime }时</td>
    
    <c:choose>
    <c:when test="${room.condition eq '可用' }">
    <td style="color:#00FF00;">${room.condition }</td>
    <td><a style="color:darkgreen;" href="${pageContext.request.contextPath }/admin/record/bookwholeroom?roomId=${room.entity.id}">预定整个机房</a>
        <a style="color:blue;" href="${pageContext.request.contextPath }/admin/record/toselectseat?roomId=${room.entity.id}">进入机房预定机位</a></td>
    </c:when><c:otherwise>
    <td style="color:grey;white-space: pre-line;">${room.condition }</td>
    <td><a style="color:grey;">预定整个机房</a>
        <a style="color:grey;">进入机房预定机位</a></td>
    </c:otherwise></c:choose>
     
</tr>
</c:forEach>
</table><br>

<c:if test="${page.nowPage==1 }">首页 &nbsp;上一页</c:if>
<c:if test="${page.nowPage>1 }">
<a href="${pageContext.request.contextPath }/admin/record/toadd?nowPage=1">首页</a>
<a href="${pageContext.request.contextPath }/admin/record/toadd?nowPage=${page.nowPage-1}">&nbsp;上一页</a></c:if>

<c:if test="${page.totalPages<5 }">
<c:forEach begin="1" end="${page.totalPages }" step="1" var="i">
<c:if test="${page.nowPage==i }">&nbsp;${i }</c:if>
<c:if test="${page.nowPage!=i }"><a href="${pageContext.request.contextPath }/admin/record/toadd?nowPage=${i}">&nbsp;${i }</a></c:if>
</c:forEach></c:if>

<c:if test="${page.totalPages>=5 }"><c:choose>
<c:when test="${page.nowPage<=2 }">
<c:forEach begin="1" end="5" step="1" var="i">
<c:if test="${page.nowPage==i }">&nbsp;${i }</c:if>
<c:if test="${page.nowPage!=i }"><a href="${pageContext.request.contextPath }/admin/record/toadd?nowPage=${i}">&nbsp;${i }</a></c:if>
</c:forEach></c:when>

<c:when test="${page.nowPage>=page.totalPages-1 }">
<c:forEach begin="${page.totalPages-4 }" end="${page.totalPages }" step="1" var="i">
<c:if test="${page.nowPage==i }">&nbsp;${i }</c:if>
<c:if test="${page.nowPage!=i }"><a href="${pageContext.request.contextPath }/admin/record/toadd?nowPage=${i}">&nbsp;${i }</a></c:if>
</c:forEach></c:when>

<c:otherwise>
<c:forEach begin="${page.nowPage-2 }" end="${page.nowPage+2 }" step="1" var="i">
<c:if test="${page.nowPage==i }">&nbsp;${i }</c:if>
<c:if test="${page.nowPage!=i }"><a href="${pageContext.request.contextPath }/admin/record/toadd?nowPage=${i}">&nbsp;${i }</a></c:if>
</c:forEach></c:otherwise>
</c:choose></c:if>

<c:if test="${page.nowPage==page.totalPages }">下一页 &nbsp;尾页</c:if>
<c:if test="${page.nowPage<page.totalPages }">
<a href="${pageContext.request.contextPath }/admin/record/toadd?nowPage=${page.nowPage+1}">下一页</a>
<a href="${pageContext.request.contextPath }/admin/record/toadd?nowPage=${page.totalPages}">&nbsp;尾页</a></c:if>

</body>
<script type="text/javascript">
function backTable(){
	 window.history.go(-1);
	}

//不变的参照物
var mD=${time.maxDay};
var today=${time.now.dayOfMonth};
var nowHour=${time.now.hour};

//表单中可供更改的部分
var day=document.getElementById("day");
var start=document.getElementById("startingHour");
var length=document.getElementById("lastingHours");

//为防回滚而设置的缓存
var currentLength=length;
var currentDay=day;
var currentStarting=start;

function checkDay(){
	
if(!isNaN(parseInt(day.value))){
	if(parseInt(day.value)<=mD && parseInt(day.value)>=today){
		if(parseInt(day.value)==today && parseInt(start.value)<nowHour){
		//防止定位到过去
		start.value=nowHour;	
		}
		//合法更改成功，且更新缓存
		currentDay=day.value;
	}else{
		alert("请指定本月剩余的一天！");
		day.value=currentDay;
	}

}else{
	alert("请输入一个数字！");
	day.value=currentDay;
}
}


function checkHour(){
	if(!isNaN(parseInt(start.value))){
		if(parseInt(start.value)<=23 ){
			if(parseInt(start.value)>=0){
				if(parseInt(day.value)==today){
					if(parseInt(start.value)>=nowHour){
						currentHour=start.value;
					}else{
						start.value=nowHour;
						currentHour=nowHour;
					}
				}else{
					currentHour=start.value;
				}
			}else{
				start.value=0;
				currentHour=0;
			}
		}else{
			start.value=23;
			currentHour=23;
		}
		
		checkLastingHours();
	
	}else{
		alert("请输入正确的时间格式！");
		start.value=currentHour;
	}
}

function checkLastingHours(){
	if(!isNaN(parseInt(length.value))){
		if(parseInt(length.value)>=1){	  
			    if(parseInt(length.value)<=24-parseInt(start.value)){
				currentLength=length.value;
			    }else{
				  length.value=24-parseInt(start.value);
				  currentLength=24-parseInt(start.value);
			    }
		}else{
			length.value=1;
			currentLength=1;
		}
	}else{
		alert("时间格式不正确！");
		length.value=currentLength;
	}
}

</script>
</html>
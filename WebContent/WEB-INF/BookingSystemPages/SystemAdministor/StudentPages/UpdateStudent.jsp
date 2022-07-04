<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>编辑账户</title>
</head>
<body>
<h2>编辑账户</h2>
<form action="${pageContext.request.contextPath }/admin/student/confirmsave" method="get" onsubmit="assemble()">
账号：<input type="text" name="accountName" value="${student.accountName}"><br/>
密码：<input type="text" name="accountKey" value="${student.accountKey}"><br/>
学生姓名：<input type="text" name="studentName" value="${student.studentName }"><br/>

<c:if test="${student.studentSex eq '女' }">
	性别：<input type="radio" name="studentSex" value="男">男
	       <input type="radio" name="studentSex" value="女" checked="checked">女<br/></c:if>
<c:if test="${student.studentSex=='男' }">
	性别：<input type="radio" name="studentSex" value="男" checked="checked">男</c:if>
	       <input type="radio" name="studentSex" value="女">女

用于找回的验证密码：<input type="text" name="referringKey" value="${student.referringKey }"><br/>

<c:if test="${student.maxHours<999 }">
规定每月上机时间：<input id="all" type="radio" name="maxHours" value="999" onclick="yesManual()">无限制
               <input id="manual" type="radio" name="maxHours" value="1" onclick="yesManual()" checked="checked">自定义(*上限为28天0小时*)<br/>
               <input id="days" type="text" value="<fmt:formatNumber value="${(student.maxHours-student.maxHours%24)/24}" pattern="#"/>">天
               &nbsp;&nbsp;&nbsp;
               <input id="hours" type="text" value="<fmt:formatNumber value="${student.maxHours%24}" pattern="#"/>">小时<br/><br/>
               
               <b style="color:red">本月剩余上机时间：</b>
               <input id="remainingDays" type="text" value="<fmt:formatNumber value="${(student.remainingHours-student.remainingHours%24)/24}" pattern="#"/>" onchange="checkRemainDays()">天
               &nbsp;&nbsp;&nbsp;
               <input id="remainingHours" type="text" value="<fmt:formatNumber value="${student.remainingHours%24}" pattern="#"/>" onchange="checkRemainHours()">小时<br/><br/></c:if>
<c:if test="${student.maxHours>=999 }">
规定每月上机时间：<input id="all" type="radio" name="maxHours" value="-999" onclick="yesManual()" checked="checked" >无限制
               <input id="manual" type="radio" name="maxHours" value="1" onclick="yesManual()">自定义(*上限为28天0小时*)<br/> 
               <b style="color:red">本月剩余上机时间：</b>
               <input id="days" type="text" disabled="disabled" value="<fmt:formatNumber value="${(student.maxHours-student.maxHours%24)/24}" pattern="#"/>">天
               &nbsp;&nbsp;&nbsp;
               <input id="hours" type="text" disabled="disabled" value="<fmt:formatNumber value="${student.maxHours%24}" pattern="#"/>">小时<br/><br/>
               
               <input id="remainingDays" type="text" disabled="disabled">天&nbsp;&nbsp;&nbsp;
               <input id="remainingHours" type="text" disabled="disabled">小时<br/><br/></c:if> 
 
 
 
               <input type="hidden" name="id" value="${student.id }">
               <input type="hidden" name="isBanned" value="${student.isBanned }">
               <input type="hidden" name="logInDate" value="${student.logInDate }">
               <input type="hidden" name="remainingHours" value="0" id="assembledRemaining">
                              
               <input type="submit" value="确定">
               <input type="button" value="取消" onclick="backTable()">  <br/><br/>
               <b style="color:red">(*若自定义上机时间修改失败，请先调至“无限制”再调回“自定义”！！)</b>
</form>
</body>

<script type="text/javascript">
var day=document.getElementById("days");
var hour=document.getElementById("hours");
var remainDay=document.getElementById("remainingDays");
var remainHour=document.getElementById("remainingHours");


function yesManual(){	
		if(document.getElementById("manual").checked){
        day.removeAttribute('disabled');
        hour.removeAttribute('disabled');
        remainDay.removeAttribute('disabled');
        remainHour.removeAttribute('disabled');
		}else {
		day.setAttribute('disabled', 'disabled');
		hour.setAttribute('disabled','disabled');
		remainDay.setAttribute('disabled','disabled');
		remainHour.setAttribute('disabled','disabled');
		}
}

function assemble(){	
	if(document.getElementById('all').checked){
		document.getElementById('assembledRemaining').value=document.getElementById('all').value;
	}
	if(document.getElementById('manual').checked){	
		document.getElementById('manual').value=parseInt(day.value)*24+parseInt(hour.value);
			
		if(parseInt(remainDay.value)*24+parseInt(remainHour.value)>=parseInt(day.value)*24+parseInt(hour.value)){
			document.getElementById('assembledRemaining').value=parseInt(day.value)*24+parseInt(hour.value);
		}else{
			document.getElementById('assembledRemaining').value=parseInt(remainDay.value)*24+parseInt(remainHour.value);	
		}
		
	}		
	return true;
}

function backTable(){
	window.history.go(-1);
}


var currentDay=day.value;
var currentHour=hour.value;
var currentRemainDay=remainDay.value;
var currentRemainHour=remainHour.value;
function checkDays(){
	if(!isNaN(parseInt(day.value))){
		if(parseInt(day.value)>=0 && parseInt(day.value)<=28){
			if(parseInt(remainDay.value)<=parseInt(day.value)){
				currentDay=day.value;
			}else{
				remainDay.value=day.value;
				currentDay=day.value;
			}
			
			checkHours();
			checkRemainDays();
			
		}else{
			alert("请输入0~28范围内的正数!");
			day.value=currentDay;
		}
	}else{
		alert("请输入一个数字！");
		day.value=currentDay;
	}
}


function checkHours(){
	//day为28时要保证hour为0
	if(!isNaN(parseInt(hour.value))){
		if(parseInt(hour.value)>=0){
			if(parseInt(day.value)<28){
			if(parseInt(hour.value)<=23){
				currentHour=hour.value;
			}else{
				hour.value=23;
				currentHour=23;
			  }
			}else{
				hour.value=0;
				currentHour=0;
			}
			
			checkRemainHours();
			
		}else{
			hour.value=0;
			currentHour=0;
		}
	}else{
		alert("请输入一个数字！");
		hour.value=currentHour;
	}
}	
	

function checkRemainDays(){
	if(!isNaN(parseInt(remainDay.value))){
		if(parseInt(remainDay.value)>=0){
			if(parseInt(remainDay.value)<=parseInt(day.value)){
				currentRemainDay=remainDay.value;
			}else{
				remainDay.value=day.value;
				currentRemainDay=day.value;
			}
			
			checkRemainHours();
			
		}else{
			remainDay.value=0;
			currentRemainDay=0;
		}
	}else{
		alert("请输入一个数字！");
		remainDay.value=currentRemainDay;
	}
}



function checkRemainHours(){
	if(!isNaN(parseInt(remainHour.value))){
		if(parseInt(remainHour.value)>=0){
			if(parseInt(remainDay.value)<parseInt(day.value)){
				if(parseInt(remainHour.value)<=23){
					currentRemainHour=remainHour.value;
				}else{
					remainHour.value=23;
					currentRemainHour=23;
				}
			}else{
				remainHour.value=0;
				currentRemainHour=0;
			}
		}else{
			remainHour.value=0;
			currentRemainHour=0;
		}
	}else{
		alert("请输入一个数字！");
		remainHour.value=currentRemainHour;
	}
}
</script>

</html>
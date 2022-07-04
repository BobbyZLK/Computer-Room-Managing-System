<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>新建账户</title>
<script type="text/javascript">
function yesManual(){	
		if(document.getElementById('manual').checked){
        document.getElementById('days').removeAttribute('disabled');
        document.getElementById('hours').removeAttribute('disabled');
		}else {
		document.getElementById('days').setAttribute('disabled', 'disabled');
		document.getElementById('hours').setAttribute('disabled','disabled');
		}
}

//还需解决手动设置上限最高为当月天数的问题
function assemble(){	
	if(document.getElementById('all').checked){
		document.getElementById('remainingHours').value=document.getElementById('all').value;
	}
	if(document.getElementById('manual').checked){
		var days=document.getElementById('days').value;
		var hours=document.getElementById('hours').value;
		if(isNaN(days)||isNaN(hours)){
			alert("请输入数字！");
			return false
		}else{
			document.getElementById('manual').value=parseInt(days)*24+parseInt(hours);
			document.getElementById('remainingHours').value=parseInt(days)*24+parseInt(hours);
		}
	}		
	return true;
}

function backTable(){
	window.history.go(-1);
}

</script>
</head>
<body>
<h2>新建账户</h2>
<form action="${pageContext.request.contextPath }/admin/student/confirmsave" method="get" onsubmit="assemble()">
账号: <input type="text" name="accountName"><br/>
密码: <input type="text" name="accountKey"><br/>
学生姓名： <input type="text" name="studentName"><br/>
性别：<input type="radio" name="studentSex" value="男" checked="checked">男
     <input type="radio" name="studentSex" value="女">女<br/>
用于找回的验证密码：<input type="text" name="referringKey"><br/>
规定每月上机时间：<input id="all" type="radio" name="maxHours" value="999" onclick="yesManual()" checked="checked" >无限制
               <input id="manual" type="radio" name="maxHours" value="1" onclick="yesManual()">自定义(*上限为28天0小时*)<br/>
               <input id="days" type="text" disabled="disabled">天&nbsp;&nbsp;&nbsp;
               <input id="hours" type="text" disabled="disabled">小时<br/><br/>
               
               <input type="hidden" value="false" name="isBanned"/>
               <input type="hidden" value="暂无记录" name="logInDate"/>
               <input type="hidden" value="0" name="remainingHours" id="remainingHours">
               
               <input type="submit" value="确定">
               <input type="button" value="取消" onclick="backTable()">  <br/><br/>
               <b style="color:red">(*若自定义上机时间创建失败，请先调至“无限制”再调回“自定义”！！)</b>
</form>
</body>
</html>
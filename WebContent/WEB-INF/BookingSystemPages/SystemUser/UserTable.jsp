<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.crbooking.service.*" %>
<%@ page import="com.crbooking.bean.Student" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%
StudentServiceImp studentService=new StudentServiceImp();
Integer studentId=0;
%>
<!DOCTYPE html>
<html>
<%
try{
if(request.getParameter("isLogIn")!=null && request.getParameter("isLogIn").equals("1")){
String account= new String((request.getParameter("account")).getBytes("ISO-8859-1"),"UTF-8");
String key= new String((request.getParameter("key")).getBytes("ISO-8859-1"),"UTF-8");
   if(studentService.queryStudent(account)!=null){
	   Student student=studentService.queryStudent(account);
	   if(student.getAccountKey().equals(key)){
		   if(!student.getIsBanned()){
			studentId=student.getStudentId();
			
			String dateFormat="yyyy-MM-dd";
			SimpleDateFormat sdf=new SimpleDateFormat(dateFormat);
			sdf.setTimeZone(TimeZone.getTimeZone("UTC+8"));

			Calendar cal=Calendar.getInstance(Locale.CHINA);
			int hour=cal.get(Calendar.HOUR_OF_DAY);
			Date date=cal.getTime();

			String lastLogIn=student.getLogInDate();
			Date lastTime=sdf.parse(lastLogIn);
			Calendar oldcal=Calendar.getInstance();
			oldcal.setTime(lastTime);
			if(student.getMaxHours()>-1){
				
			if(oldcal.get(Calendar.YEAR)<cal.get(Calendar.YEAR)){
			studentService.updateRemainTime(studentId, student.getMaxHours());
			}
			if(oldcal.get(Calendar.YEAR)==cal.get(Calendar.YEAR)
			   && oldcal.get(Calendar.MONTH)<cal.get(Calendar.MONTH)){
			studentService.updateRemainTime(studentId, student.getMaxHours());	
			}
			}
			
			String trans=sdf.format(date);
            student.setLogInDate(trans);
            
            
		   }else{
	   %><script type="text/javascript">alert("账号封禁中！");window.location.href="LogIn.jsp";</script><% 	   
		   }
	   }else{
      %><script type="text/javascript">alert("账号密码不正确！");window.location.href="LogIn.jsp";</script><% 	   
	   }
   }else{
	   %><script type="text/javascript">alert("账号不存在！");window.location.href="LogIn.jsp";</script><% 
   }

}
}catch(Exception e){
	e.printStackTrace();
}

if(studentId<=0){
	%><script type="text/javascript">alert("必须登录一个账号！");window.location.href="LogIn.jsp";</script><%
}
%>
<head>
<meta charset="UTF-8">
<title>系统主页</title>
</head>
<body>
<h2>系统主页&nbsp;欢迎回来：<%=studentService.queryStudent(studentId).getStudentName() %></h2>
<b>制作者：兰州大学2017级数学与应用数学  张凌凯  320170929181</b><br/><br/>
<button>现在预约</button><br/><br/>
<button>历史记录</button>
</body>
</html>
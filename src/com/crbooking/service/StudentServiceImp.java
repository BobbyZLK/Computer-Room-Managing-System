package com.crbooking.service;


import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crbooking.bean.*;
import com.crbooking.bean.query.*;
import com.crbooking.dao.*;

//事实证明：“不限时长”最好是设成999而非-999，顺应数学逻辑能少很多判断时的麻烦
@Service("studentService")
public class StudentServiceImp implements StudentService{
	@Autowired
	private StudentDao studentDao;
	@Autowired
	private RecordService recordService;
	
	
	@Override
	public Integer saveStudent(Student student)throws Exception{	
	//根据情况规整，提高运行效率
    //通用
	if(student.getAccountName().isEmpty()) {
		throw new messageException("账号名不能为空！");
	}
	if(student.getAccountKey().isEmpty()) {
		throw new messageException("账号密码不能为空！");
	}
	if(student.getReferringKey().isEmpty()) {
		throw new messageException("找回验证码不能为空！");
	}
	
	//新建时
	if(student.getId()==null) {
		if(this.queryStudent(student.getAccountName())!=null) {
		   throw new messageException("该账号已经存在！");	
		}
	}
	
	//修改时
	if(student.getId()!=null) {
		if(this.queryStudent(student.getAccountName())!=null && this.queryStudent(student.getAccountName()).getId()!=student.getId()) {
		   throw new messageException("有同名账号，修改失败！");
		}
	}
    
		return studentDao.saveStudent(student);		
	}


	
	@Override
	public Integer updateRemainTime(Integer studentId,Integer newhours)throws Exception{
	Student student=this.queryStudent(studentId);
	if(newhours<0 || (newhours>student.getMaxHours())) {
		throw new messageException("输入时间超出范围！");
	}
	
	student.setRemainingHours(newhours);
	return this.saveStudent(student);
	}



	
	@Override
	public Integer banStudent(Integer id)throws Exception{
	Student student=this.queryStudent(id);
	if(student.getIsBanned()) {
		student.setIsBanned(false);
		return this.saveStudent(student);
	}
		
	student.setIsBanned(true);
	return this.saveStudent(student);		
    }


	
	@Override
	public Boolean updateAllMaxTime(Integer newhours)throws Exception {
		//获取当月总时长,然后又引申出自动更新的问题
	Calendar cal=Calendar.getInstance(Locale.CHINA);
	if( newhours<0 || newhours>24*cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
		throw new messageException("输入时间超出当月总时长范围");
	} 
	
	List<Student> students=this.queryAllStudents();
	for(Student student:students) {
			student.setMaxHours(newhours);
		    if(newhours<0) {
		    	student.setRemainingHours(newhours);
		    }else {
		    	if(student.getRemainingHours()==999) {
		    		student.setRemainingHours(newhours);
		    	}else if(student.getRemainingHours()>newhours) {
		    	    student.setRemainingHours(newhours);	
		    	}
		    }
			this.saveStudent(student);
	}	

	return true;
}

	
    @Override
	public Integer minusRemainHours(Integer id,Long differentHours)throws Exception{
		//输入的就是整除过的以小时为计算单位，这里只需转换一下就行！！
		int difference=differentHours.intValue();
		Student student=this.queryStudent(id);
    	
		if(student.getMaxHours()!=999) {
			Integer i=student.getRemainingHours();
			i=i-difference;
			student.setRemainingHours(i);
			this.saveStudent(student);
		}else {
			student.setRemainingHours(999);
			this.saveStudent(student);
		}
		return student.getId();
	}

    @Override
    public StudentQuery mergeQueries(StudentQuery accepted,StudentQuery insert) {
    	if(insert.getQueryAccount()!=null) {
    		accepted.setQueryAccount(insert.getQueryAccount());
    	}
    	if(insert.getQueryStudent()!=null) {
    		accepted.setQueryStudent(insert.getQueryStudent());
    	}
    	if(insert.getNowPage()!=null) {
    		accepted.setNowPage(insert.getNowPage());
    	}
    	return accepted;
    }
    


	
	





@Override
public Boolean deleteStudent(Integer id)throws Exception{
	recordService.deleteRecordByStudent(this.queryStudent(id));
	return studentDao.deleteStudent(id);
	
}










@Override
public List<Student> queryStudents(StudentQuery studentQuery)throws Exception{
	return studentDao.queryStudents(studentQuery);
}

@Override
public Integer queryTotalAmount(StudentQuery studentQuery)throws Exception{
	return studentDao.queryTotalNumber(studentQuery);
}

@Override
public List<Student> queryAllStudents()throws Exception{
	StudentQuery studentQuery=new StudentQuery();
	return this.queryStudents(studentQuery);
}

@Override
public Student queryStudent(Integer id)throws Exception{
	StudentQuery studentQuery=new StudentQuery();
	studentQuery.setId(id);
	List<Student>studentsList=this.queryStudents(studentQuery);	
	if(studentsList.size()>0) {
		Student student=studentsList.get(0);
		return student;
	}	
	return null;
}


@Override
public Student queryStudent(String accountName)throws Exception{
	StudentQuery studentQuery=new StudentQuery();
	studentQuery.setAccountName(accountName);
	List<Student>studentsList=this.queryStudents(studentQuery);	
	if(studentsList.size()>0) {
		Student student=studentsList.get(0);
		return student;
	}
	return null;
}



@Override
public String queryAccountName(Integer id)throws Exception{
	Student student=this.queryStudent(id);
	return student.getAccountName();
}








@Override
public Boolean judgeStudentOccupied(Student student,Integer lastingHours)throws Exception{
	Boolean isOccupied=false;
	if(student.getIsBanned()) {
		isOccupied=true;
	}
	if(lastingHours>student.getRemainingHours()) {
		isOccupied=true;
	}
	
	BookingRecordQuery recordQuery=new BookingRecordQuery();
	recordQuery.setStudent(student);
	recordQuery.setRecordCondition(0);
	recordQuery.setDateofStart(recordService.currentBjTime());
	List<BookingRecord>recordsOfStudent=recordService.queryRecords(recordQuery);
	if(recordsOfStudent.size()>0) {
		isOccupied=true;
	}
	
	return isOccupied;
}


@Override
public String judgeStudentOccupiedReason(Student student,Integer lastingHours)throws Exception{
	//本方法意在“判断不可用的原因”，不可用已经是默认的事实了。虽然可以做得像编程软件一样把所有阻碍原因都列出，但我相信这不是普通使用者喜欢看到的，要体现“引导”
	String reason="不可预约 \n";
	
	BookingRecordQuery recordQuery=new BookingRecordQuery();
	recordQuery.setStudent(student);
	recordQuery.setRecordCondition(0);
	recordQuery.setDateofStart(recordService.currentBjTime());
	List<BookingRecord>recordsOfStudent=recordService.queryRecords(recordQuery);
	
	if(!student.getIsBanned()) {
		if(lastingHours>student.getRemainingHours()) {
					reason+="本月剩余上机时长不足";
				}else if(recordsOfStudent.size()>0) {
					BookingRecord record=recordsOfStudent.get(0);
					if(record.getStartingTime().compareTo(recordService.currentBjTime())<=0) {
						reason+="有进行中的预约，于"+recordService.formatTime(record.getEndingTime())+"结束";
					}else {
						reason+="有待进行的预约，于"+recordService.formatTime(record.getStartingTime())+"开始";
					}
				}
	}else {
		reason+="账号封禁中";
	}
	return reason;
}


//Controller层只管为页面准备数据，所以应该在后台Service准备无需再加工的数据给前端
@Override
public List<Student> queryStudentsAvailable(Integer joinRecordByLastingHours)throws Exception{
	List<Student>available=new ArrayList<Student>();
	List<Student>unavailable=new ArrayList<Student>();
	LinkedHashMap<Student,String>results=new LinkedHashMap<Student,String>();
	for(Student student:this.queryAllStudents()) {
		if(this.judgeStudentOccupied(student, lastingHour)) {
			unavailable.add(student);
		}else {
			available.add(student);
		}
	}
	for(Student student:available) {
		results.put(student, "可预约");
	}
	for(Student student:unavailable) {
		results.put(student, this.judgeStudentOccupiedReason(student, lastingHour));
	}
	return results;
}

}

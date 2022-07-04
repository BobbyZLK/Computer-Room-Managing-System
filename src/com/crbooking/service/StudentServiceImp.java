package com.crbooking.service;


import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crbooking.bean.*;
import com.crbooking.bean.query.*;
import com.crbooking.dao.*;

//��ʵ֤����������ʱ������������999����-999��˳Ӧ��ѧ�߼����ٺܶ��ж�ʱ���鷳
@Service("studentService")
public class StudentServiceImp implements StudentService{
	@Autowired
	private StudentDao studentDao;
	@Autowired
	private RecordService recordService;
	
	
	@Override
	public Integer saveStudent(Student student)throws Exception{	
	//��������������������Ч��
    //ͨ��
	if(student.getAccountName().isEmpty()) {
		throw new messageException("�˺�������Ϊ�գ�");
	}
	if(student.getAccountKey().isEmpty()) {
		throw new messageException("�˺����벻��Ϊ�գ�");
	}
	if(student.getReferringKey().isEmpty()) {
		throw new messageException("�һ���֤�벻��Ϊ�գ�");
	}
	
	//�½�ʱ
	if(student.getId()==null) {
		if(this.queryStudent(student.getAccountName())!=null) {
		   throw new messageException("���˺��Ѿ����ڣ�");	
		}
	}
	
	//�޸�ʱ
	if(student.getId()!=null) {
		if(this.queryStudent(student.getAccountName())!=null && this.queryStudent(student.getAccountName()).getId()!=student.getId()) {
		   throw new messageException("��ͬ���˺ţ��޸�ʧ�ܣ�");
		}
	}
    
		return studentDao.saveStudent(student);		
	}


	
	@Override
	public Integer updateRemainTime(Integer studentId,Integer newhours)throws Exception{
	Student student=this.queryStudent(studentId);
	if(newhours<0 || (newhours>student.getMaxHours())) {
		throw new messageException("����ʱ�䳬����Χ��");
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
		//��ȡ������ʱ��,Ȼ����������Զ����µ�����
	Calendar cal=Calendar.getInstance(Locale.CHINA);
	if( newhours<0 || newhours>24*cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
		throw new messageException("����ʱ�䳬��������ʱ����Χ");
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
		//����ľ�������������СʱΪ���㵥λ������ֻ��ת��һ�¾��У���
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
	//���������ڡ��жϲ����õ�ԭ�򡱣��������Ѿ���Ĭ�ϵ���ʵ�ˡ���Ȼ���������������һ���������谭ԭ���г������������ⲻ����ͨʹ����ϲ�������ģ�Ҫ���֡�������
	String reason="����ԤԼ \n";
	
	BookingRecordQuery recordQuery=new BookingRecordQuery();
	recordQuery.setStudent(student);
	recordQuery.setRecordCondition(0);
	recordQuery.setDateofStart(recordService.currentBjTime());
	List<BookingRecord>recordsOfStudent=recordService.queryRecords(recordQuery);
	
	if(!student.getIsBanned()) {
		if(lastingHours>student.getRemainingHours()) {
					reason+="����ʣ���ϻ�ʱ������";
				}else if(recordsOfStudent.size()>0) {
					BookingRecord record=recordsOfStudent.get(0);
					if(record.getStartingTime().compareTo(recordService.currentBjTime())<=0) {
						reason+="�н����е�ԤԼ����"+recordService.formatTime(record.getEndingTime())+"����";
					}else {
						reason+="�д����е�ԤԼ����"+recordService.formatTime(record.getStartingTime())+"��ʼ";
					}
				}
	}else {
		reason+="�˺ŷ����";
	}
	return reason;
}


//Controller��ֻ��Ϊҳ��׼�����ݣ�����Ӧ���ں�̨Service׼�������ټӹ������ݸ�ǰ��
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
		results.put(student, "��ԤԼ");
	}
	for(Student student:unavailable) {
		results.put(student, this.judgeStudentOccupiedReason(student, lastingHour));
	}
	return results;
}

}

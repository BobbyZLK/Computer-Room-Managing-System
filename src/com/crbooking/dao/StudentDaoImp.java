package com.crbooking.dao;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.crbooking.bean.*;
import com.crbooking.bean.query.StudentQuery;

@Repository("studentDao")
public class StudentDaoImp implements StudentDao{
	private String format="yyyy-MM-dd";
    public DateTimeFormatter dtf=DateTimeFormatter.ofPattern(format);
	
@Override	
public Integer saveStudent(Student student) throws Exception{
	
  return action.save(student);
}

@Override
public Boolean updateStudents(List<Students> students)throws Exception{
	
}


@Override	
public Boolean deleteStudent(Integer id) throws Exception{
	
	return action.delete(Student.class, id);
}


@Override
public List<Student> queryStudents(StudentQuery studentQuery)throws Exception{
	//�����ѯ����
	String hql="FROM Student WHERE 1=1 ";
	if(studentQuery.getId()!=null) {
		hql+=" and id='"+studentQuery.getId()+"'";
	}
	if(studentQuery.getAccountName()!=null) {
		hql+=" and accountName='"+studentQuery.getAccountName()+"'";
	}
	if(studentQuery.getStudentName()!=null) {
		hql+=" and studentName='"+studentQuery.getStudentName()+"'";
	}
	if(studentQuery.getLogInDate()!=null) {
		hql+=" and logInDate='"+dtf.format(studentQuery.getLogInDate())+"'";
	}
	if(StringUtils.isNotBlank(studentQuery.getQueryAccount())) {
		hql+=" and accountName LIKE '%"+studentQuery.getQueryAccount()+"%' ";
	}
	if(StringUtils.isNotBlank(studentQuery.getQueryStudent())) {
		hql+=" and studentName LIKE '%"+studentQuery.getQueryStudent()+"%' ";
	}
	
	
	
	//����ѯ����,û�������־���һ�µĻ����ֶ�����ɣ�������һ��϶�����ʵ����
	//���ȥ����SQL��������������������֮ǰ����ֻҪfrom������һ����Ϳɸ��������������
		if(studentQuery.getJoinRecordByLastingHours()!=null) {
			hql="SELECT s from Student AS s LEFT JOIN BookingRecord AS r "
					+ "WHERE s.id=r.student.id AND r.startingTime="
					+ "(SELECT MAX(startingTime) FROM BookingRecord WHERE recordCondition=0 GROUP BY student)"
					+ "ORDER BY s.isBanned asc, s.remainingHours desc, r.startingTime asc nulls first";
		}
		
	Session session=getSession.sessionGetter();
    Query query=session.createQuery(hql);
    
    if(studentQuery.getNowPage()!=null && studentQuery.getPageSize()!=null) {
    	query.setFirstResult((studentQuery.getNowPage()-1)*studentQuery.getPageSize());
    	query.setMaxResults(studentQuery.getPageSize());
    }
    List<Student>results=query.list();
    session.close();
	return results;
}

@Override
public Integer queryTotalNumber(StudentQuery studentQuery)throws Exception{
	String hql="select count(*) from Student where 1=1";
	if(studentQuery.getId()!=null) {
		hql+=" and id='"+studentQuery.getId()+"'";
	}
	if(studentQuery.getAccountName()!=null) {
		hql+=" and accountName='"+studentQuery.getAccountName()+"'";
	}
	if(studentQuery.getStudentName()!=null) {
		hql+=" and studentName='"+studentQuery.getStudentName()+"'";
	}
	if(StringUtils.isNotBlank(studentQuery.getQueryAccount())) {
		hql+=" and accountName LIKE '%"+studentQuery.getQueryAccount()+"%' ";
	}
	if(StringUtils.isNotBlank(studentQuery.getQueryStudent())) {
		hql+=" and studentName LIKE '%"+studentQuery.getQueryStudent()+"%' ";
	}
	
	//��Ϊ����߼��Ǹ�ÿ��student�������δ��ɵ�ԤԼ������count(*)Ӧ�����ò����ر�ȥָ��
	if(studentQuery.getJoinRecordByLastingHours()!=null) {
		hql="SELECT count(*) from Student AS s LEFT JOIN BookingRecord AS r "
				+ "WHERE s.id=r.student.id AND r.startingTime="
				+ "(SELECT MAX(startingTime) FROM BookingRecord WHERE recordCondition=0 GROUP BY student)";
	}
	
	Session session=getSession.sessionGetter();
    Query query=session.createQuery(hql);
    int i=((Number) query.uniqueResult()).intValue();
    session.close();
    return i;
}
}

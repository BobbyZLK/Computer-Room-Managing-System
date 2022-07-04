package com.crbooking.dao;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;


import java.time.format.DateTimeFormatter;

import com.crbooking.bean.*;
import com.crbooking.bean.query.*;

import javax.annotation.Resource;


@Repository("recordDao")
public class RecordDaoImp implements RecordDao{
    @Resource(name="sessionFactory")
	private SessionFactory sessionFactory;
	
 
	private String format="yyyy-MM-dd HH:mm:ss";
    public DateTimeFormatter dtf=DateTimeFormatter.ofPattern(format);
	
    private Session getSession() {
    	return sessionFactory.getCurrentSession();
    }
    
    @Override
    public Integer saveRecord(BookingRecord record)throws Exception{
		if(record.getId()==null) {
			this.getSession().save(record);
		}else {
			this.getSession().update(record);
		}
		return record.getId(); 
	}
	
    //���������²���������
    @Override
    public Boolean updateRecords(List<BookingRecord> records)throws Exception{
    	Session session=this.getSession();
    	for(BookingRecord record:records) {
    	session.update(record);	
    	}
    	return true;
    }
    
    
	@Override
	public Boolean deleteRecord(Integer id)throws Exception{
		Session session=this.getSession();
		BookingRecord record=(BookingRecord)session.get(BookingRecord.class, id);
		session.delete(record);
		return true;
	}
	
	@Override
	public Boolean deleteRecords(List<Integer> ids)throws Exception{
		Boolean result=false;
		if(ids.size()>0) {
			StringBuffer hql=new StringBuffer("DELETE FROM BookingRecord WHERE id= ");
			for(int i=0;i<ids.size();i++) {
				if(i==0) {
					hql.append(ids.get(i));
				}else {
					hql.append(", "+ids.get(i));
				}
			}
			
			Query query=this.getSession().createQuery(hql.toString());
			if(query.executeUpdate()>0) result=true;
		}	
		return result;
	}
	
	
	@Override
	public List<BookingRecord> queryRecords(BookingRecordQuery recordQuery)throws Exception{
		StringBuffer hql=new StringBuffer("FROM BookingRecord AS r WHERE 1=1");
		if(recordQuery.getId()!=null) {
			hql.append(" AND r.id='"+recordQuery.getId()+"'");
		}
		//Ҫ���ֵ�����Room�����
		
		if(recordQuery.getRoom()!=null) {
			hql.append(" and r.room=?0");
		}
		
		if(recordQuery.getRoomOnly()) {
			hql.append(" and r.seat IS NULL");
		}else if(recordQuery.getSeat()!=null) {
			hql.append(" and r.seat=?1");
	    }
		
		if(recordQuery.getStudent()!=null) {
			hql.append(" and r.student=?2");
		}
		if(recordQuery.getRecordCondition()!=null) {
			hql.append(" and r.recordCondition='"+recordQuery.getRecordCondition()+"'");
		}
		//hql���ڱȽ�Ҫ�ӵ����š�����������쳣
		/*if(recordQuery.getStartingTime()!=null) {
			hql+=" and r.startingTime='"+dtf.format(recordQuery.getStartingTime())+"'";
		}
		if(recordQuery.getEndingTime()!=null) {
			hql+=" and r.endingTime='"+dtf.format(recordQuery.getEndingTime())+"'";
		}*/
		
		
		if(StringUtils.isNotBlank(recordQuery.getRoomName())) {
			hql.append(" and r.room.roomName LIKE '%"+recordQuery.getRoomName()+"%'");
		}
		if(StringUtils.isNotBlank(recordQuery.getSeatName())) {
			hql.append(" and r.seat.seatName LIKE '%"+recordQuery.getSeatName()+"%'");
		}
		if(StringUtils.isNotBlank(recordQuery.getStudentName())) {
			hql.append(" and r.student.studentName LIKE '%"+recordQuery.getStudentName()+"%'");
		}
		if(StringUtils.isNotBlank(recordQuery.getAccountName()) ) {
			hql.append(" and r.student.accountName LIKE '%"+recordQuery.getAccountName()+"%'");
		}
		
		
		//ֻ��start��û��end�����������ʱ�������ԤԼ���Ǿ��ǽ���ʱ�����ڴ���ʱ�䣨�պõ�������Բ����漰��ʱ��Σ����õȺ�����ת���ַ�����ֵ��������
		if(recordQuery.getDateofStart()!=null) {
			hql.append(" and r.endingTime >'"+dtf.format(recordQuery.getDateofStart())+"'");
		}
		//ֻ��endû��start�����������ʱ��֮ǰ��ԤԼ������ʼʱ�����ڴ���ʱ��
		if(recordQuery.getDateofEnd()!=null) {
			hql.append(" and r.startingTime <'"+dtf.format(recordQuery.getDateofEnd())+"'");
		}
	
		
		
		
		//�����Ƿ��������
		hql.append(" order by r.recordCondition asc, r.startingTime desc, r.endingTime asc");
		
		Session session=this.getSession();
        Query query=session.createQuery(hql.toString());
        
        if(recordQuery.getRoom()!=null) {
        	query.setParameter(0, recordQuery.getRoom());
        }
        if(recordQuery.getSeat()!=null && !recordQuery.getRoomOnly()) {
        	query.setParameter(1, recordQuery.getSeat());
        }
        if(recordQuery.getStudent()!=null) {
        	query.setParameter(2, recordQuery.getStudent());
        }
       
        if(recordQuery.getNowPage()!=null && recordQuery.getPageSize()!=null) {
        	query.setFirstResult((recordQuery.getNowPage()-1)*recordQuery.getPageSize());
        	query.setMaxResults(recordQuery.getPageSize());
        }
        
        List<BookingRecord>results=query.list();
        return results;    
		}
	
	
	@Override
	public Integer queryTotalNumber(BookingRecordQuery recordQuery)throws Exception{
		StringBuffer hql=new StringBuffer("select count(*) from BookingRecord AS r where 1=1");
		if(recordQuery.getId()!=null) {
			hql.append(" and r.id='"+recordQuery.getId()+"'");
		}
		if(recordQuery.getRoom()!=null) {
			hql.append(" and r.room=?0");
		}
		if(recordQuery.getSeat()!=null) {
			hql.append(" and r.seat=?1");
		}
		if(recordQuery.getStudent()!=null) {
			hql.append(" and r.student=?2");
		}
		if(recordQuery.getRecordCondition()!=null) {
			hql.append(" and r.recordCondition='"+recordQuery.getRecordCondition()+"'");
		}
		/*if(recordQuery.getStartingTime()!=null) {
			hql+=" and r.startingTime='"+recordQuery.getStartingTime()+"'";
		}
		if(recordQuery.getEndingTime()!=null) {
			hql+=" and r.endingTime='"+recordQuery.getEndingTime()+"'";
		}*/
		
		
		if(StringUtils.isNotBlank(recordQuery.getRoomName())) {
			hql.append(" and r.room.roomName LIKE '%"+recordQuery.getRoomName()+"%'");
		}
		if(StringUtils.isNotBlank(recordQuery.getSeatName())) {
			hql.append(" and r.seat.seatName LIKE '%"+recordQuery.getSeatName()+"%'");
		}
		if(StringUtils.isNotBlank(recordQuery.getStudentName())) {
			hql.append(" and r.student.studentName LIKE '%"+recordQuery.getStudentName()+"%'");
		}
		if(StringUtils.isNotBlank(recordQuery.getAccountName())) {
			hql.append(" and r.student.accountName LIKE '%"+recordQuery.getAccountName()+"%'");
		}
		if(recordQuery.getDateofStart()!=null && recordQuery.getDateofEnd()==null) {
			hql.append(" and r.endingTime >='"+dtf.format(recordQuery.getDateofStart())+"'");
		}
		if(recordQuery.getDateofStart()==null && recordQuery.getDateofEnd()!=null) {
			hql.append(" and r.startingTime <='"+dtf.format(recordQuery.getDateofEnd())+"'");
		}
				
		Session session=this.getSession();
        Query query=session.createQuery(hql.toString());
        if(recordQuery.getRoom()!=null) {
        	query.setParameter(0, recordQuery.getRoom());
        }
        if(recordQuery.getSeat()!=null) {
        	query.setParameter(1, recordQuery.getSeat());
        }
        if(recordQuery.getStudent()!=null) {
        	query.setParameter(2, recordQuery.getStudent());
        }
        
        int i=((Number) query.uniqueResult()).intValue();
        return i;
	}
}


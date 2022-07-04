package com.crbooking.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.crbooking.bean.BookingRecord;
import com.crbooking.bean.Room;
import com.crbooking.bean.Seat;
import com.crbooking.bean.Student;
import com.crbooking.bean.query.*;
import com.crbooking.dao.RecordDao;

@Service("recordService")
public class RecordServiceImp implements RecordService{
    
	@Autowired
	private RecordDao recordDao;
	@Autowired
	private StudentService studentService;
	
	//��ǰʱ�䣨ע���κ�ʱ�䶼�Ǹ�ֵ��������Ҳ����˵һ����ֵ�Ͳ����˲����Զ�����)	
	//����Щ���÷�����ӵ��ӿ�������mvc��Ͻ��������controller������д����
	@Override
	public LocalDateTime currentBjTime() {
        LocalDateTime now=LocalDateTime.now();
        //�ƺ��й����Ĵ������Ϻ������Ǳ���
        ZoneId zone=ZoneId.of("Asia/Shanghai");
		return now.atZone(zone).toLocalDateTime();
	}
	
	@Override
	public String formatTime(LocalDateTime dateTime) {
		//ͳһʱ���ʽ 
		String format="yyyy-MM-dd HH:mm:ss";
	    DateTimeFormatter dtf=DateTimeFormatter.ofPattern(format);
		return dtf.format(dateTime);
	}
	
	//ò��Ҫת���ɺ���Ҫ��UTC���ﾳ��,ʹ��offset������utc,��ô����������ڱ���ʱ��
	//������Сʱ��Ϊʣ��ʱ��ĵ�λ��������˼·һ�ǻ��ɺ���Ȼ��1Сʱ=3600000 ���룬��һ���Ǹ���ҵ��������Ϊά������������İ汾֧�ֿ�������������*24��
	/*public Long totalHours(LocalDateTime subtracted,LocalDateTime subtract) {
		return (subtracted.toInstant(ZoneOffset.of("+8")).toEpochMilli()-subtract.toInstant(ZoneOffset.of("+8")).toEpochMilli())/3600000;
	}*/
	
	
	@Override
	 public Integer saveRecord(BookingRecord record)throws Exception{	 
	
		//��Ҫ������Ϊ�˷�ֹ�ж���û�г�ͻ��ԤԼ�����г�ͻԤԼд�룬���ܵ���serialized���������Ǻܷ���Դ�ĵ�ͨ���ֲ������ٿ���
		 if(record.getStartingTime()==null) {
    		 throw new messageException("��ʼʱ�䲻��Ϊ�գ�");
    	 }
    	 if(record.getEndingTime()==null) {
    		 throw new messageException("����ʱ�䲻��Ϊ�գ�");
    	 }
    	 if(record.getRoom()==null) {
    		 throw new messageException("��������Ϊ�գ�");
    	 }
    	
    	//����hibernateһ�������ԭ��ɸѡ��������Ҫ�����������ų��Ǳ䶯ԤԼ���ĵ����
    	 if(record.getId()!=null) {
    		 BookingRecord cache=this.queryRecord(record.getId());
    	
    		 //�˴�ע�⣺��д��record��equals������Ϊ��ʵ�����ϵĵ�ͬ��ע��ʱ���ԤԼ�����Ա�������ؿ��������ڱ��������λ
    		 if(record.equals(cache)) {
    			 return update(record);
    		 }
    	 }
    	 
    	 
    	 if(!this.judgeCondition(record).equals("δ��ʼ")) {
			 throw new messageException("��ԤԼ�ѿ�ʼ���ѽ�����������ԤԼ��"); 
		 }
    	 return this.create(record);
     }
    
	
	//���ݵ�����ԭ��mvc����Ҫ֪����ֳ���ʵ��ϸ��
	@Transactional(isolation=Isolation.READ_COMMITTED)
	protected Integer update(BookingRecord record)throws Exception{
		//�������޸�ԤԼ״̬�ȷǶ���������,û�н����ж����ݿ��������ٲ����Ĺ������Բ������޸�
		return recordDao.saveRecord(record);
	}
	
	
	
	@Transactional(isolation=Isolation.SERIALIZABLE,rollbackFor=messageException.class)
	protected Integer create(BookingRecord record)throws Exception{
		//����ԭ����û��id��������������Ҫ����û�п�λ��������
		if(record.getSeat()==null) {
       	 if(this.judgeOverlap(record)) {
       	   throw new messageException("���ڳ�ͻ�Ļ���ԤԼ��");
       	 }
        }else {
       	 if(this.judgeOverlap(record)) {
       	   throw new messageException("���ڳ�ͻ�Ļ�λԤԼ��"); 
       	 }
        }
		
		
		if(record.getId()==null) {
		 if(record.getStudent()!=null) {
    		 Student student=studentService.queryStudent(record.getStudent().getId());
    		 //toHours�������Сʱ����toHoursPart����period��������ʽ
    		 if(Duration.between(record.getStartingTime(), record.getEndingTime()).toHours()>student.getRemainingHours()){
    			 throw new messageException("�˻�����ʣ���ϻ�ʱ�����㣡");
    		 }else {
    			 //�����Ȳ��ٱ����һ�ɼ��������Ҳ�Ҫ����service��ֱ������dao
    			 studentService.minusRemainHours(student.getId(), Duration.between(record.getStartingTime(), record.getEndingTime()).toHours());
    		 }
    	 }  
	    }
		 return recordDao.saveRecord(record);
	}
	
	
	
	
	@Override
	@Transactional(isolation=Isolation.READ_COMMITTED)
	//��Ҫ�����������ݽ����޸ģ����Կ϶�������Ƕ������Ȼǰ����˺���ʧ�ܾ����ˣ�����Ȼû��ͬһ�������жϺ��޸��������޸�
     public Integer cancelRecord(Integer recordId)throws Exception{
    	 BookingRecord record=this.queryRecord(recordId);
    	 if(record==null) throw new messageException("�Ҳ�����¼��");
    	 
    	 //���˽ṹ�Ժ��ܰ��˻ص�ʱ���Ⱦ�����ø���
    	 if(record.getStudent()!=null) {
    		 LocalDateTime date=this.currentBjTime();
    		 //time��һ��Ľ�����ֱ��֧�ֱȽϺͼ���
    		 if(date.isBefore(record.getStartingTime())) {
    		   //ԤԼδ��ʼ
    			 studentService.minusRemainHours(record.getStudent().getId(), Duration.between(record.getEndingTime(), record.getStartingTime()).toHours()); 
    		 }else if(date.isBefore(record.getEndingTime())) {
    		   //ԤԼ�����е�δ���� 
    			 studentService.minusRemainHours(record.getStudent().getId(), Duration.between(record.getEndingTime(), date).toHours());		 
    		 }
    	 }
    	 
    	 record.setRecordCondition(1);
    	 return recordDao.saveRecord(record);
     }
    
	
	@Override
	@Transactional(isolation=Isolation.SERIALIZABLE)
	//�������Ŀ�����ſ�ʱ�����Ȼ����������ԤԼ
	public Boolean cancelRecord(LocalDateTime start,LocalDateTime end)throws Exception{
		//����ȡ������ԤԼ������˵�ָ���ȡ���ģ������ǵ���ȡ���߿����Ѿ�����ԤԼ����������ͻ��������ʱ����
		List<BookingRecord>records=this.queryNormalRecordByDate(start, end);
		//�Ҷ�����ѭ�����÷����ܲ����⣬ÿ����������һ�������ǵ������������ޱȣ�Ӧ�ó���һ���ύ
		//���������Ƕ��ԭ���֪Ĭ�ϴ�����Ϊ�������������ڲ������������񣬵���û���һ������������ݿ��е�����
		for(BookingRecord record:records) {
			this.cancelRecord(record.getId());
		}
		return true;
	}
	
	
	@Override
	@Transactional(isolation=Isolation.READ_COMMITTED)
	//���õı�������һ����ȫд�����ʽ���ٶȿ�û��Ҫ�����޸���
	public Boolean updateRecords(List<BookingRecord> records)throws Exception{
		return recordDao.updateRecords(records);
	}
	
	
	
	
	
	
	
	
	
	
	
     @Transactional(isolation=Isolation.READ_UNCOMMITTED)
     @Override
     public Boolean deleteRecord(Integer id) throws Exception{
    	return recordDao.deleteRecord(id);
    	 
     }   
     
     
     @Override
     @Transactional(isolation=Isolation.READ_UNCOMMITTED)
     public Boolean deleteRecords(BookingRecordQuery recordQuery)throws Exception{
    	 List<BookingRecord> records=queryRecords(recordQuery);
 		ArrayList<Integer> ids=new ArrayList<Integer>();
 		for(BookingRecord record:records) {
 			ids.add(record.getId());
 		}
    	 return recordDao.deleteRecords(ids);
     }
      
     
     
     @Override
     public Boolean deleteRecordByRoom(Room room)throws Exception{
    	 BookingRecordQuery recordQuery=new BookingRecordQuery();
    	 recordQuery.setRoom(room);
    	 return this.deleteRecords(recordQuery);
     }
     
     
     @Override
     public Boolean deleteRecordBySeat(Seat seat)throws Exception{
    	 BookingRecordQuery recordQuery=new BookingRecordQuery();
    	 recordQuery.setSeat(seat);
    	 return this.deleteRecords(recordQuery);
     }
     
     
     @Override
     public Boolean deleteRecordByStudent(Student student)throws Exception{
    	 BookingRecordQuery recordQuery=new BookingRecordQuery();
    	 recordQuery.setStudent(student);
    	 return this.deleteRecords(recordQuery);
     }
     
     
     @Override
     public Boolean deleteRecordByDate(LocalDateTime start,LocalDateTime end)throws Exception{
    	 BookingRecordQuery recordQuery=new BookingRecordQuery();
    	 recordQuery.setDateofStart(start);
    	 recordQuery.setDateofEnd(end);
    	 return this.deleteRecords(recordQuery);
     }
     
     
     
     
     
     
  
     
     
 
     
     @Transactional(readOnly=true,isolation=Isolation.READ_COMMITTED)
     @Override
     public List<BookingRecord>queryRecords(BookingRecordQuery recordQuery) throws Exception{
    	 return recordDao.queryRecords(recordQuery);
     } 
     
     @Transactional(readOnly=true,isolation=Isolation.READ_COMMITTED)
     @Override
     public Integer queryTotalAmount(BookingRecordQuery recordQuery)throws Exception{
    	 return recordDao.queryTotalNumber(recordQuery);
     }
     
     @Override
     public BookingRecord queryRecord(Integer recordId)throws Exception{
    	 BookingRecordQuery recordQuery=new BookingRecordQuery();
    	 recordQuery.setId(recordId);
    	 List<BookingRecord>resultList=this.queryRecords(recordQuery);
    	 if(resultList.size()>0) {
    		 return resultList.get(0);
    	 }
    		 return null;
     }
    
 
     @Override
     public List<BookingRecord> queryNormalRecordByDate(LocalDateTime start,LocalDateTime end)throws Exception{
    	 BookingRecordQuery recordQuery=new BookingRecordQuery();
    	 recordQuery.setDateofStart(start);
    	 recordQuery.setDateofEnd(end);
    	 recordQuery.setRecordCondition(0);
    	 return this.queryRecords(recordQuery);
     }
 
       
     
     
     

     
     
     
     @Override
     public Boolean judgeOverlap(BookingRecord record)throws Exception{
    	 BookingRecordQuery recordQuery=new BookingRecordQuery();
    	 //Ҫע�����������saveRecord�е�λ�ã�����ͨ��ǰ����ų���д����
    	 recordQuery.setRoom(record.getRoom());
    	 /*recordQuery.setStartingTime(record.getStartingTime());
    	 recordQuery.setEndingTime(record.getEndingTime());*/
    	 //ԭ���Ǵֲڵ�ֱ�Ӳ���һ��׼ȷʱ�䣬����Ȼ��ûʲô�õģ�������ѯ���������˾͸ÿ�������ʱ��������޳�ͻ
    	 recordQuery.setDateofStart(record.getStartingTime());
    	 recordQuery.setDateofEnd(record.getEndingTime());
    	 recordQuery.setRecordCondition(0);
    	 if(record.getSeat()!=null) {
    		 recordQuery.setSeat(record.getSeat());
    	 }else {
    		 recordQuery.setRoomOnly(true);
    	 }
    	 
    	 List<BookingRecord>resultList=queryRecords(recordQuery);
    	 if(resultList.size()<=0) {
    		 return false;
    	 }else {
    		 //��������������������
    		 if(record.getId()!=null && record.getId()==resultList.get(0).getId()) {
    			 return false;
    		 }else {
    			 return true;
    		 }
    	 }
     }
     
  
     @Override
     public String judgeCondition(BookingRecord record)throws Exception{
         LocalDateTime date=this.currentBjTime();
         
         //0Ϊ������1Ϊȡ��
    	 if(record.getRecordCondition()==0) {
    		 //���ﱣ��compareTo����Ϊ������һ�����ۣ���ֹ���ڵ����
    		 if(date.compareTo(record.getEndingTime())>=0) {
    			 return "�ѽ���";
    		 }
    		 else if(date.compareTo(record.getEndingTime())<0 &&  date.compareTo(record.getStartingTime())>=0) {
    			 return "������";
    		 }
    		 else if(date.compareTo(record.getStartingTime())<0) {
    			 return "δ��ʼ";
    		 }else {
    			 return "״̬δ֪";
    		 }
    	 }else {
    		 return "��ȡ��";
    	 }
     }
     	
 	
}

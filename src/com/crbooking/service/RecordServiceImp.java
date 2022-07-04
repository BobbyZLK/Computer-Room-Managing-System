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
	
	//当前时间（注意任何时间都是赋值到变量，也就是说一旦赋值就不变了不会自动更新)	
	//把这些常用方法添加到接口里纳入mvc管辖，可以在controller调用少写代码
	@Override
	public LocalDateTime currentBjTime() {
        LocalDateTime now=LocalDateTime.now();
        //似乎中国区的代表是上海而不是北京
        ZoneId zone=ZoneId.of("Asia/Shanghai");
		return now.atZone(zone).toLocalDateTime();
	}
	
	@Override
	public String formatTime(LocalDateTime dateTime) {
		//统一时间格式 
		String format="yyyy-MM-dd HH:mm:ss";
	    DateTimeFormatter dtf=DateTimeFormatter.ofPattern(format);
		return dtf.format(dateTime);
	}
	
	//貌似要转换成毫秒要在UTC的语境下,使用offset回退至utc,那么这个仅适用于北京时间
	//我们用小时作为剩余时间的单位！！两种思路一是化成毫秒然后1小时=3600000 毫秒，另一种是根据业务需求人为维护（比如最近的版本支持跨天则新增天数*24）
	/*public Long totalHours(LocalDateTime subtracted,LocalDateTime subtract) {
		return (subtracted.toInstant(ZoneOffset.of("+8")).toEpochMilli()-subtract.toInstant(ZoneOffset.of("+8")).toEpochMilli())/3600000;
	}*/
	
	
	@Override
	 public Integer saveRecord(BookingRecord record)throws Exception{	 
	
		//很要命的是为了防止判断完没有冲突的预约后又有冲突预约写入，可能得用serialized即锁表，这是很费资源的得通过分拆尽量减少开销
		 if(record.getStartingTime()==null) {
    		 throw new messageException("开始时间不能为空！");
    	 }
    	 if(record.getEndingTime()==null) {
    		 throw new messageException("结束时间不能为空！");
    	 }
    	 if(record.getRoom()==null) {
    		 throw new messageException("机房不能为空！");
    	 }
    	
    	//仿照hibernate一级缓存的原理，筛选出真正需要锁表的情况，排除非变动预约核心的情况
    	 if(record.getId()!=null) {
    		 BookingRecord cache=this.queryRecord(record.getId());
    	
    		 //此处注意：重写了record的equals，定义为现实意义上的等同，注意时间和预约者难以变更建议重开，仅用于变更机房机位
    		 if(record.equals(cache)) {
    			 return update(record);
    		 }
    	 }
    	 
    	 
    	 if(!this.judgeCondition(record).equals("未开始")) {
			 throw new messageException("该预约已开始或已结束，请重新预约！"); 
		 }
    	 return this.create(record);
     }
    
	
	//根据迪米特原则，mvc不需要知道拆分出的实现细节
	@Transactional(isolation=Isolation.READ_COMMITTED)
	protected Integer update(BookingRecord record)throws Exception{
		//仅用作修改预约状态等非定义性属性,没有进行判断数据库内数据再操作的过程所以不用锁修改
		return recordDao.saveRecord(record);
	}
	
	
	
	@Transactional(isolation=Isolation.SERIALIZABLE,rollbackFor=messageException.class)
	protected Integer create(BookingRecord record)throws Exception{
		//不管原本有没有id，进入此情况都是要看有没有空位，故锁表
		if(record.getSeat()==null) {
       	 if(this.judgeOverlap(record)) {
       	   throw new messageException("存在冲突的机房预约！");
       	 }
        }else {
       	 if(this.judgeOverlap(record)) {
       	   throw new messageException("存在冲突的机位预约！"); 
       	 }
        }
		
		
		if(record.getId()==null) {
		 if(record.getStudent()!=null) {
    		 Student student=studentService.queryStudent(record.getStudent().getId());
    		 //toHours换算成总小时数，toHoursPart则是period的那种形式
    		 if(Duration.between(record.getStartingTime(), record.getEndingTime()).toHours()>student.getRemainingHours()){
    			 throw new messageException("账户本月剩余上机时长不足！");
    		 }else {
    			 //凡是先查再保存的一律加上事务，且不要引用service层直接引用dao
    			 studentService.minusRemainHours(student.getId(), Duration.between(record.getStartingTime(), record.getEndingTime()).toHours());
    		 }
    	 }  
	    }
		 return recordDao.saveRecord(record);
	}
	
	
	
	
	@Override
	@Transactional(isolation=Isolation.READ_COMMITTED)
	//先要对其他表内容进行修改，所以肯定得做成嵌套事务不然前面改了后面失败就糟了，且依然没有同一个表先判断后修改无需锁修改
     public Integer cancelRecord(Integer recordId)throws Exception{
    	 BookingRecord record=this.queryRecord(recordId);
    	 if(record==null) throw new messageException("找不到记录！");
    	 
    	 //改了结构以后能把退回的时间额度精度提得更高
    	 if(record.getStudent()!=null) {
    		 LocalDateTime date=this.currentBjTime();
    		 //time的一大改进就是直接支持比较和计算
    		 if(date.isBefore(record.getStartingTime())) {
    		   //预约未开始
    			 studentService.minusRemainHours(record.getStudent().getId(), Duration.between(record.getEndingTime(), record.getStartingTime()).toHours()); 
    		 }else if(date.isBefore(record.getEndingTime())) {
    		   //预约进行中但未结束 
    			 studentService.minusRemainHours(record.getStudent().getId(), Duration.between(record.getEndingTime(), date).toHours());		 
    		 }
    	 }
    	 
    	 record.setRecordCondition(1);
    	 return recordDao.saveRecord(record);
     }
    
	
	@Override
	@Transactional(isolation=Isolation.SERIALIZABLE)
	//这个函数目的是排空时间段自然不允许新增预约
	public Boolean cancelRecord(LocalDateTime start,LocalDateTime end)throws Exception{
		//对于取消机房预约曾考虑说恢复已取消的，但考虑到被取消者可能已经重新预约（即产生冲突）还是暂时算了
		List<BookingRecord>records=this.queryNormalRecordByDate(start, end);
		//我对这种循环调用方法很不满意，每个方法都是一次事务那等它跑完奇慢无比，应该尝试一并提交
		//根据事务的嵌套原理得知默认传播行为下有外事务则内部不再另起事务，但最好还是一次性完成在数据库中的任务
		for(BookingRecord record:records) {
			this.cancelRecord(record.getId());
		}
		return true;
	}
	
	
	@Override
	@Transactional(isolation=Isolation.READ_COMMITTED)
	//采用的本来就是一次性全写入的形式，速度快没必要再锁修改了
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
    	 //要注意这个方法在saveRecord中的位置，可以通过前面的排除少写代码
    	 recordQuery.setRoom(record.getRoom());
    	 /*recordQuery.setStartingTime(record.getStartingTime());
    	 recordQuery.setEndingTime(record.getEndingTime());*/
    	 //原本是粗糙的直接查那一个准确时间，这显然是没什么用的，后来查询能力提升了就该看看整个时间段内有无冲突
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
    		 //若搜索到仅能是它本身
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
         
         //0为正常，1为取消
    	 if(record.getRecordCondition()==0) {
    		 //这里保留compareTo是因为必须有一个结论，防止等于的情况
    		 if(date.compareTo(record.getEndingTime())>=0) {
    			 return "已结束";
    		 }
    		 else if(date.compareTo(record.getEndingTime())<0 &&  date.compareTo(record.getStartingTime())>=0) {
    			 return "进行中";
    		 }
    		 else if(date.compareTo(record.getStartingTime())<0) {
    			 return "未开始";
    		 }else {
    			 return "状态未知";
    		 }
    	 }else {
    		 return "已取消";
    	 }
     }
     	
 	
}

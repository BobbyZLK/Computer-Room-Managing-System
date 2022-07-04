package com.crbooking.bean;

import java.time.LocalDateTime;
import java.util.Objects;


public class BookingRecord extends Entity{
	private LocalDateTime startingTime;
	private LocalDateTime endingTime;
	private Student student;
	private Room room;
	private Seat seat;
	private Integer recordCondition;
	private String remark;
	
	//实体跟查询类可不一样，想想按照事实情况实体被新建出来应该具有什么，但查询类就不该这样了
	public BookingRecord() {
		recordCondition=0;
	}
	
	
	//在数据库中（现实规则也是），没有设置预约号（订单号），非空的预约时间、预约对象和预约者共同组成唯一性
	@Override
	public boolean equals(Object o) {
		if(this==o) return true;
		if(o==null || o.getClass()!=getClass()) return true;
		
		BookingRecord record=(BookingRecord)o;
		//同样时间里同样的人（可能无对象）同样的机房（可能还得有同样的机位）
		if(startingTime!=record.getStartingTime() || endingTime !=record.getEndingTime()) return false;
		if(student!=null) {
			if(!student.equals(record.getStudent())) return false;
		}else {
			if(record.getStudent()!=null) return false;
		}
	    if(seat!=null) {
	    	if(!seat.equals(record.getSeat())) return false;
	    }else {
	    	if(record.getSeat()!=null) return false;
	    }
	    
		return room.equals(record.getRoom());
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(startingTime,endingTime,student,room,seat);
	}
	
	
	
	
	
	public LocalDateTime getStartingTime() {
		return startingTime;
	}
	public void setStartingTime(LocalDateTime startingTime) {
		this.startingTime = startingTime;
	}
	public LocalDateTime getEndingTime() {
		return endingTime;
	}
	public void setEndingTime(LocalDateTime endingTime) {
		this.endingTime = endingTime;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public Room getRoom() {
		return room;
	}
	public void setRoom(Room room) {
		this.room = room;
	}
	public Seat getSeat() {
		return seat;
	}
	public void setSeat(Seat seat) {
		this.seat = seat;
	}
	public Integer getRecordCondition() {
		return recordCondition;
	}
	public void setRecordCondition(Integer recordCondition) {
		this.recordCondition = recordCondition;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}

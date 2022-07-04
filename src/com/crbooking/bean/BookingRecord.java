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
	
	//ʵ�����ѯ��ɲ�һ�������밴����ʵ���ʵ�屻�½�����Ӧ�þ���ʲô������ѯ��Ͳ���������
	public BookingRecord() {
		recordCondition=0;
	}
	
	
	//�����ݿ��У���ʵ����Ҳ�ǣ���û������ԤԼ�ţ������ţ����ǿյ�ԤԼʱ�䡢ԤԼ�����ԤԼ�߹�ͬ���Ψһ��
	@Override
	public boolean equals(Object o) {
		if(this==o) return true;
		if(o==null || o.getClass()!=getClass()) return true;
		
		BookingRecord record=(BookingRecord)o;
		//ͬ��ʱ����ͬ�����ˣ������޶���ͬ���Ļ��������ܻ�����ͬ���Ļ�λ��
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

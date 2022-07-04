package com.crbooking.bean.query;

import java.time.LocalDateTime;
import com.crbooking.bean.Room;
import com.crbooking.bean.Seat;
import com.crbooking.bean.Student;

public class BookingRecordQuery extends Query{
	//基础
	private LocalDateTime startingTime;
	private LocalDateTime endingTime;
	private Room room;
	private Seat seat;
	private Student student;
	private Integer recordCondition;
	private String remark;
	//自定义需求
	private String roomName;
	private String studentName;
	private String seatName;
	private String accountName;
	private LocalDateTime dateofStart;
	private LocalDateTime dateofEnd;
	private Boolean roomOnly;
	//分页
	private Integer nowPage;
	private Integer pageSize;
	
	public BookingRecordQuery() {
		//设置新查询时默认状态
		nowPage=1;
		roomOnly=false;
	}
	
	
	//合并
	public BookingRecordQuery merge(BookingRecordQuery another) {
		//记住，负责查询的始终是session中的唯一query，所以不要想着直接用新的替换省事，老老实实地更新
		if(another.getAccountName()!=null) {
			accountName=another.getAccountName();
		}
		if(another.getStudentName()!=null) {
			studentName=another.getStudentName();
		}
		if(another.getRoomName()!=null) {
			roomName=another.getRoomName();
		}
		if(another.getSeatName()!=null) {
			seatName=another.getSeatName();
		}
		
		return this;
		
	}
	
	
	public LocalDateTime getDateofStart() {
		return dateofStart;
	}
	public void setDateofStart(LocalDateTime dateofStart) {
		this.dateofStart = dateofStart;
	}
	public LocalDateTime getDateofEnd() {
		return dateofEnd;
	}
	public void setDateofEnd(LocalDateTime dateofEnd) {
		this.dateofEnd = dateofEnd;
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
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
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
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getSeatName() {
		return seatName;
	}
	public void setSeatName(String seatName) {
		this.seatName = seatName;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public Integer getNowPage() {
		return nowPage;
	}
	public void setNowPage(Integer nowPage) {
		this.nowPage = nowPage;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Boolean getRoomOnly() {
		return roomOnly;
	}
	public void setRoomOnly(Boolean roomOnly) {
		this.roomOnly = roomOnly;
	}

	
}

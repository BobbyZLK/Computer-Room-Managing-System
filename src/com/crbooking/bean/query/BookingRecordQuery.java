package com.crbooking.bean.query;

import java.time.LocalDateTime;
import com.crbooking.bean.Room;
import com.crbooking.bean.Seat;
import com.crbooking.bean.Student;

public class BookingRecordQuery extends Query{
	//����
	private LocalDateTime startingTime;
	private LocalDateTime endingTime;
	private Room room;
	private Seat seat;
	private Student student;
	private Integer recordCondition;
	private String remark;
	//�Զ�������
	private String roomName;
	private String studentName;
	private String seatName;
	private String accountName;
	private LocalDateTime dateofStart;
	private LocalDateTime dateofEnd;
	private Boolean roomOnly;
	//��ҳ
	private Integer nowPage;
	private Integer pageSize;
	
	public BookingRecordQuery() {
		//�����²�ѯʱĬ��״̬
		nowPage=1;
		roomOnly=false;
	}
	
	
	//�ϲ�
	public BookingRecordQuery merge(BookingRecordQuery another) {
		//��ס�������ѯ��ʼ����session�е�Ψһquery�����Բ�Ҫ����ֱ�����µ��滻ʡ�£�����ʵʵ�ظ���
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

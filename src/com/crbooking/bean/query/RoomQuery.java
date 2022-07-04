package com.crbooking.bean.query;

import java.time.LocalDateTime;

public class RoomQuery extends Query{
    private String roomName;
    private Boolean isBanned;
    private String roomCondition;
    private Integer openTime;
    private Integer closeTime;
    private String queryName;
   
    //���ڲ����ѯ
    private LocalDateTime joinRecordStart;
    private LocalDateTime joinRecordEnd;
    
    //���ڷ�ҳ��ѯ
    private Integer nowPage;
    private Integer pageSize;
    
    public RoomQuery() {
    	nowPage=1;
    }
    
    //�ϲ���ע��RoomQuery�漰���ű����Բ�����BookingRecordQuery����ֱ�����µ��滻��ֻ��һ������
    public RoomQuery merge(RoomQuery another) {
    	if(another.getQueryName()!=null) {
    		queryName=another.getQueryName();
    	}
    	if(another.getNowPage()!=null) {
    		nowPage=another.getNowPage();
    	}
    	
    	return this;
    	
    }
    
    
    public Integer getOpenTime() {
		return openTime;
	}
	public void setOpenTime(Integer openTime) {
		this.openTime = openTime;
	}
	public Integer getCloseTime() {
		return closeTime;
	}
	public void setCloseTime(Integer closeTime) {
		this.closeTime = closeTime;
	}
    
    public void setRoomName(String s) {
   	 roomName=s;
    }
    public String getRoomName() {
   	 return roomName;
    }
    public void setIsBanned(Boolean b) {
   	 isBanned=b;
    }
    public Boolean getIsBanned() {
   	 return isBanned;
    }
    public void setRoomCondition(String s) {
   	 roomCondition=s;
    }
    public String getRoomCondition() {
   	 return roomCondition;
    }
	public String getQueryName() {
		return queryName;
	}
	public void setQueryName(String queryName) {
		this.queryName = queryName;
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
	public LocalDateTime getJoinRecordStart() {
		return joinRecordStart;
	}
	public void setJoinRecordStart(LocalDateTime joinRecordStart) {
		this.joinRecordStart = joinRecordStart;
	}
	public LocalDateTime getJoinRecordEnd() {
		return joinRecordEnd;
	}
	public void setJoinRecordEnd(LocalDateTime joinRecordEnd) {
		this.joinRecordEnd = joinRecordEnd;
	}
	
	
    
}

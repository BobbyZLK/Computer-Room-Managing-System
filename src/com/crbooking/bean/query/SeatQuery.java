package com.crbooking.bean.query;

import com.crbooking.bean.Room;

public class SeatQuery extends Query{
    private String seatName;
    private Boolean isBanned;
    private String seatCondition;
    private Room room;
    private String akaInRoom;
    private String queryName;

    private Integer nowPage;
    private Integer pageSize;
    
    public Room getRoom() {
		return room;
	}
	public void setRoom(Room room) {
		this.room = room;
	}
	public void setSeatName(String s) {
   	 seatName=s;
    }
    public String getSeatName() {
   	 return seatName;
    }
    
    public void setIsBanned(Boolean b) {
   	 isBanned=b;
    }
    public Boolean getIsBanned() {
   	 return isBanned;
    }
    public void setSeatCondition(String s) {
   	 seatCondition=s;
    }
    public String getSeatCondition() {
   	 return seatCondition;
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
	public String getAkaInRoom() {
		return akaInRoom;
	}
	public void setAkaInRoom(String akaInRoom) {
		this.akaInRoom = akaInRoom;
	}
    
}

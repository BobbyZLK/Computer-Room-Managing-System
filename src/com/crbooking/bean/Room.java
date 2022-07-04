package com.crbooking.bean;

import java.util.Objects;

public class Room extends Entity {
	 private String roomName;
     private Boolean isBanned;
     private String roomCondition;
     private Integer openTime;
     private Integer closeTime;
   
     //现实中（无id）如何确保机房的唯一性？靠名字（数据库中也设置成unique）
     @Override
     public boolean equals(Object o) {
    	 if(this==o) return true;
    	 if(o==null || o.getClass()!=getClass()) return false;
    	 
    	 Room room=(Room)o;
    	 return roomName.equals(room.getRoomName());
     }
     
     @Override
     public int hashCode() {
    	 return Objects.hash(roomName);
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
     
		public Boolean getIsBanned() {
		return isBanned;
	}
	public void setIsBanned(Boolean isBanned) {
		this.isBanned = isBanned;
	}
		public void setRoomCondition(String s) {
    	 roomCondition=s;
     }
     public String getRoomCondition() {
    	 return roomCondition;
     }
}

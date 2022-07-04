package com.crbooking.bean;

import java.util.Objects;

public class Seat extends Entity {
     private String seatName;
     private Boolean isBanned;
     private String seatCondition;
     private Room room;
     private String akaInRoom;
    
     //现实世界中椅子都有一个序列号一类的唯一码
     @Override
     public boolean equals(Object o) {
    	 if(o==this) return true;
    	 if(o==null || getClass()!=o.getClass()) return false;
    	 
    	 Seat seat=(Seat)o;
    	 return seatName.equals(seat.getSeatName());
     }
     
     @Override
     public int hashCode() {
    	 return Objects.hash(seatName);
     }
     
     
     
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
	public Boolean getIsBanned() {
		return isBanned;
	}
	public void setIsBanned(Boolean isBanned) {
		this.isBanned = isBanned;
	}
	public void setSeatCondition(String s) {
    	 seatCondition=s;
     }
     public String getSeatCondition() {
    	 return seatCondition;
     }
	public String getAkaInRoom() {
		return akaInRoom;
	}
	public void setAkaInRoom(String akaInRoom) {
		this.akaInRoom = akaInRoom;
	}
   
}

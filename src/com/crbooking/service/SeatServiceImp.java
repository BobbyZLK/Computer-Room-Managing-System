package com.crbooking.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crbooking.bean.BookingRecord;
import com.crbooking.bean.Room;
import com.crbooking.bean.Seat;

import com.crbooking.bean.query.SeatQuery;
import com.crbooking.controller.TimepackForReservation;
import com.crbooking.dao.SeatDao;

@Service("seatService")
public class SeatServiceImp implements SeatService{ 
	
	@Autowired
	private SeatDao seatDao;
	@Autowired
	private RoomService roomService;
	@Autowired
	private RecordService recordService;
	
	
	@Override
	        public Integer saveSeat(Seat seat) throws Exception{
            	if(seat.getSeatName().isEmpty()) {
            		throw new messageException("机位名不能为空！");
            	}            	
	        	if(seat.getId()==null && seat.getRoom()!=null 
            			&& this.querySeat(seat.getSeatName(), seat.getRoom())!=null) {
            		throw new messageException("该机位已存在！");
            	}
	        	
            	return seatDao.saveSeat(seat);
            }
	        
	
	@Override
	        public Integer changeRoom(Integer seatId, Room room)throws Exception {          	
            	Seat seat=querySeat(seatId);
            	if((seat.getRoom()!=null && seat.getRoom().getId()!=room.getId())
            			&& this.querySeat(seat.getSeatName(), room)!=null) {
            		throw new messageException("目标机房已有同名机位！");
            	}else {
            	seat.setRoom(room);
            	return saveSeat(seat);
            	}
            }
            
	
	@Override
            public Integer banSeat(Integer id)throws Exception{
            	Seat seat=querySeat(id);
                if(seat.getIsBanned()) {
                	seat.setIsBanned(false);
                }else {
                	if(seat.getRoom()==null) {
                		throw new messageException("该机位需要一个机房！");
                	}
                	seat.setIsBanned(true);
                }
            	return saveSeat(seat);          	
            }
                     
	
	@Override
            public Integer setSeatCondition(Integer id,String s)throws Exception{
            	Seat seat=querySeat(id);
            	seat.setSeatCondition(s);
            	return saveSeat(seat);
            	
            }
            
            
	
	
	
	@Override
	      public Boolean updateSeats(List<Seat>seats)throws Exception{
		
	      }
            
            


            
            
            
         @Override   
            public Boolean deleteSeat(Integer id)throws Exception {
            	recordService.deleteRecordBySeat(this.querySeat(id));
            	return seatDao.deleteSeat(id);
            }
              
      
     
            
            
            
            
            
            
            
           @Override 
            public List<Seat> querySeats(SeatQuery seatQuery)throws Exception{
            	
            	return seatDao.querySeat(seatQuery);          	
            }
           
           @Override
           public Integer queryTotalAmount(SeatQuery seatQuery)throws Exception{
        	   return seatDao.queryTotalNumber(seatQuery);
           }
           
           @Override
            public List<Seat> queryAllSeats()throws Exception{
        	   return seatDao.querySeat(new SeatQuery());
           }
           
           @Override
            public List<Seat> querySeats(Room room)throws Exception{            	           	
            	SeatQuery seatquery=new SeatQuery();
            	seatquery.setRoom(room);             
            	return this.querySeats(seatquery);
            }
           
            @Override
            public List<Seat> queryByRoomId(Integer roomId)throws Exception{
            	Room room=roomService.queryRoom(roomId);
            	return this.querySeats(room);
            }
           
           @Override
            public Seat querySeat(SeatQuery seatQuery) throws Exception {
            	List<Seat> seats = querySeats(seatQuery);
            	if(seats.size()>0) {
            		return seats.get(0);
            	}
            	return null;
            }
                    
           
           @Override
            public Seat querySeat(Integer id)throws Exception{
            	SeatQuery seatquery=new SeatQuery();
            	seatquery.setId(id);           	
            	return querySeat(seatquery);
            	          	
            }
              
           
           @Override
            public Seat querySeat(String seatName,Room room)throws Exception{  	
            	SeatQuery seatquery=new SeatQuery();
                seatquery.setRoom(room);
            	seatquery.setSeatName(seatName);            	
            	return querySeat(seatquery);
            	            	
              }
             
           
           @Override
            public String querySeatName(Integer id)throws Exception{
            	Seat seat=this.querySeat(id);
            	return seat.getSeatName();
            }
            
            
            
            
            
            
            
            
            
            
               Calendar calendar=Calendar.getInstance(Locale.CHINA); 
           @Override 
            public Boolean judgeSeatOccupied(Seat seat,TimepackForReservation time)throws Exception{
        	    calendar.set(time.getYear(),time.getMonth(),time.getDay());
        	    Date date=calendar.getTime();
            	Integer startingHour=time.getStartingHour();
            	Integer lastingHours=time.getLastingHours();
            	
        	    List<BookingRecord>recordsThisDay=recordService.querySeatRecordByDay(date);
            	Boolean isOccupied=false;
            	if(seat.getIsBanned()){
       		    	isOccupied=true;
       		    }
            	for(BookingRecord record:recordsThisDay){
               		if(record.getSeat()!=null && record.getSeat().getId()==seat.getId() && 
               			(  (record.getStartingTime()>=startingHour && record.getStartingTime()<startingHour+lastingHours) || 
               			   (record.getEndingTime()>startingHour && record.getEndingTime()<=startingHour+lastingHours) ||
               			   (record.getStartingTime()<startingHour && record.getEndingTime()>startingHour+lastingHours)
               			 )){
               					isOccupied=true;
               				}
               		
               			}
            	return isOccupied;
            }
            
            
           
           @Override
            public String judgeSeatOccupiedReason(Seat seat,TimepackForReservation time)throws Exception{
        	    calendar.set(time.getYear(),time.getMonth(),time.getDay());
       	        Date date=calendar.getTime();
           	    Integer startingHour=time.getStartingHour();
           	    Integer lastingHours=time.getLastingHours();
        	   
            	List<BookingRecord>recordsThisDay=recordService.querySeatRecordByDay(date);
            	String reason="不可用 \n";
            	if(!seat.getIsBanned()) {
            		for(BookingRecord record:recordsThisDay){
               			
               			if(record.getSeat()!=null && record.getSeat().getId()==seat.getId()){
               				if(     (record.getStartingTime()>=startingHour && record.getStartingTime()<startingHour+lastingHours) || 
               						(record.getEndingTime()>startingHour && record.getEndingTime()<=startingHour+lastingHours) ||
               						(record.getStartingTime()<startingHour && record.getEndingTime()>startingHour+lastingHours)
               				   ) {
               					if(record.getRemark().isBlank()) {
               					reason+=record.getStartingTime()+"点至"+record.getEndingTime()+"点该机位有预约  \n";
               				}else {
               					reason+=record.getStartingTime()+"点至"+record.getEndingTime()+"点该机位有预约  ("+record.getRemark()+")\n";
               				}
               				}
               				}
            		}
            	}else {
            		reason+="机位停用中 \n";
            	}
            	return reason;
            }
           
           
           @Override
           public LinkedHashMap<Seat,String>querySeatByTime(Integer roomId,TimepackForReservation time)throws Exception{
        	   List<Seat>available=new ArrayList<Seat>();
        	   List<Seat>unavailable=new ArrayList<Seat>();
        	   LinkedHashMap<Seat,String>results=new LinkedHashMap<Seat,String>();
        	   for(Seat seat:this.queryByRoomId(roomId)) {
        		   if(this.judgeSeatOccupied(seat, time)) {
        			   unavailable.add(seat);
        		   }else {
        			   available.add(seat);
        		   }
        	   }
        	   for(Seat seat:available) {
        		   results.put(seat, "可用");
        	   }
        	   for(Seat seat:unavailable) {
        		   results.put(seat, this.judgeSeatOccupiedReason(seat, time));
        	   }
        	   return results;
           }
 }

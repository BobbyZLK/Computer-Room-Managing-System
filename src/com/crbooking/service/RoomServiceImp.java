package com.crbooking.service;


import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.crbooking.bean.BookingRecord;
import com.crbooking.bean.Room;
import com.crbooking.bean.Seat;
import com.crbooking.bean.query.*;
import com.crbooking.dao.RoomDao;


@Service("roomService")
public class RoomServiceImp implements RoomService{
	
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private RecordService recordService;
	@Autowired
	private SeatService seatService;
	
	@Override
	@Transactional(rollbackFor=messageException.class,isolation=Isolation.READ_COMMITTED)
      public  Integer saveRoom(Room room) throws Exception{
    	  //通用
		  if(room.getRoomName().isEmpty()) {
    		  throw new messageException("机房名不能为空！");
    	  }
    	  if(room.getOpenTime()==null || room.getCloseTime()==null) {
    		  throw new messageException("机房开关门时间不能为空！");
    	  }
    	  if(room.getOpenTime()<0 || room.getCloseTime()>23) {
    		  throw new messageException("机房开放的时间范围是0至23时！");
    	  }
    	    
    	  //新建
    	  if(room.getId()==null) {
    		  return create(room);
    	  }
    	  
    	  return roomDao.saveRoom(room);
      }   
        
	
	  @Transactional(isolation=Isolation.SERIALIZABLE,rollbackFor=messageException.class,propagation=Propagation.REQUIRES_NEW)
	  protected Integer create(Room room)throws Exception{
		  if(this.queryRoom(room.getRoomName())!=null) {
  		    throw new messageException("该机房已存在！"); 
  		  }
		  return roomDao.saveRoom(room);
	  }
	
	
	
	@Override
	  public Integer banRoom(Integer id)throws Exception {
		  Room room=queryRoom(id);  
		  if(room.getIsBanned()) {
			  room.setIsBanned(false);
		  }else {
	      room.setIsBanned(true);
		  }
		  
		  return saveRoom(room);
	  }
	
	 
	  
	  
	  
	  @Override
	  @Transactional(isolation=Isolation.SERIALIZABLE)
	  //这里涉及删除其他表的内容，也要做成一个ACID，且开始删除一个机房就不能再往里添加预约了
      public  Boolean deleteRoom(Integer id) throws Exception{
  	      recordService.deleteRecordByRoom(this.queryRoom(id));
  	      List<Seat>seats=seatService.queryByRoomId(id);
  	      for(Seat seat:seats) {
  	    	  seat.setRoom(null);
  	      }
  	      seatService.updateSeats(seats);
    	  return roomDao.deleteRoom(id);
      }

    
      
      
      
    
	  @Override
	  @Transactional(readOnly=true,isolation=Isolation.READ_COMMITTED)
      public  List<Room> queryRooms (RoomQuery roomquery) throws Exception{
  	      return roomDao.queryRoom(roomquery); 	  
      }  
     
	  @Override
	  @Transactional(readOnly=true,isolation=Isolation.READ_COMMITTED)
	  public Integer queryTotalAmount(RoomQuery roomquery)throws Exception{
		  return roomDao.queryTotalNumber(roomquery);
	  }
	  
	  @Override
	  public List<Room> queryAllRooms()throws Exception{
		  RoomQuery roomquery=new RoomQuery();
		  return this.queryRooms(roomquery);
	  }
	  
	  @Override
      public  Room queryRoom(Integer id)throws Exception{
  	      RoomQuery roomquery=new RoomQuery();
  	      roomquery.setId(id);
  	      List<Room> list=queryRooms(roomquery);
  	 
  	      if(list.size()>0) {
  	    	return list.get(0);  
  	      }
  		  return null;
  	  }  
  	  
     
	  
	  @Override
      public  Room queryRoom(String roomName)throws Exception{
  	      RoomQuery roomquery=new RoomQuery();
  	      roomquery.setRoomName(roomName);
  	      List<Room>list=queryRooms(roomquery);
  	  
  	      if(list.size()>0) {
  	    	return list.get(0);
  	      }
  		  return null;
      }
    
	  
    
   
	  //最终决定为了能实现判断加分页，还是直接输入RoomQuery比较灵活,但是要确保时间不为空！！
	  //小林叔叔指出这是一个二级分类的问题，根据他的实践经验，把两级分类变成两次查询（两个list），再放到service中去遍历找对应，这样不必往返数据库，效率大幅提升
	  //但一定要注意减少查询返回的数据量，因为如果两个List都十分庞大（成千上万），同样会很慢，然后每次连接断开数据库的速度也很慢！！！
	  //多级分类是为了贯彻mvc模式和高聚合低耦合，虽然在数据库中已经用到了本方法的判断逻辑，但如果图省事直接从数据库传出来，dao的方法就变得只适合给这个业务功能了（耦合过紧）
	  
	  @Override
	public List<EntityWithCondition<Room>>queryRoomByTime(RoomQuery roomQuery)throws Exception{
		  List<EntityWithCondition<Room>> results=new ArrayList<EntityWithCondition<Room>>();
		
		//准备第二层分类的查询，第一层分类查询已经体现在roomQuery中，注意控制返回数据总数！！
		//为了复用性和运行效率只能再写一遍判断逻辑了
		BookingRecordQuery recordQuery=new BookingRecordQuery();
		recordQuery.setRecordCondition(0);
		//开启仅查询机房预约,就是因为这种没法单纯用sql表达的业务逻辑才要用到分层查询
		recordQuery.setRoomOnly(true);
		recordQuery.setDateofStart(roomQuery.getJoinRecordStart());
		recordQuery.setDateofEnd(roomQuery.getJoinRecordEnd());
		List<BookingRecord>records=recordService.queryRecords(recordQuery);
		
		//对于MIN()的部分还是决定放到service，你不能因为这种太特化的需求再去加一个query变量甚至是dao中的方法，不通用
		for(Room room:this.queryRooms(roomQuery)) {
			//基于跨天要重新预约的假设，用于确认机房是否开放
			LocalDateTime open=roomQuery.getJoinRecordStart().withHour(room.getOpenTime());
			LocalDateTime close=roomQuery.getJoinRecordEnd().withHour(room.getCloseTime());
			
			String condition="可用";
			if(room.getIsBanned()) {
				condition="停用中";
			}else if(roomQuery.getJoinRecordStart().isBefore(open)|| roomQuery.getJoinRecordEnd().isAfter(close)) {
				condition="不在机房开放时间内";
			}else {
				//一二层分类结合点，因为机房只有空与不空所以找到最早的记录就是当前正进行的记录（如有）
				LocalDateTime nearest=null;
				LocalDateTime itsEnd=null;
				for(BookingRecord record:records) {
					if(record.getRoom().getId()==room.getId()) {
						if(nearest==null || (nearest!=null && record.getStartingTime().isBefore(nearest))) {
							nearest=record.getStartingTime();
							itsEnd=record.getEndingTime();
						}
					}
				}
				
				if(nearest!=null) {
					if(nearest.isBefore(roomQuery.getJoinRecordStart())) {
						condition="机房使用中，"+itsEnd.getHour()+"时结束";
					}else {
						condition="机房被预定，于"+nearest.getHour()+"时开始，"+itsEnd.getHour()+"时结束";
					}
				}
			}
			
		   results.add(new EntityWithCondition<Room>(room,condition)); 
		}
		
		return results;
	}
    
}
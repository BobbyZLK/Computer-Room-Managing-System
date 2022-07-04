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
    	  //ͨ��
		  if(room.getRoomName().isEmpty()) {
    		  throw new messageException("����������Ϊ�գ�");
    	  }
    	  if(room.getOpenTime()==null || room.getCloseTime()==null) {
    		  throw new messageException("����������ʱ�䲻��Ϊ�գ�");
    	  }
    	  if(room.getOpenTime()<0 || room.getCloseTime()>23) {
    		  throw new messageException("�������ŵ�ʱ�䷶Χ��0��23ʱ��");
    	  }
    	    
    	  //�½�
    	  if(room.getId()==null) {
    		  return create(room);
    	  }
    	  
    	  return roomDao.saveRoom(room);
      }   
        
	
	  @Transactional(isolation=Isolation.SERIALIZABLE,rollbackFor=messageException.class,propagation=Propagation.REQUIRES_NEW)
	  protected Integer create(Room room)throws Exception{
		  if(this.queryRoom(room.getRoomName())!=null) {
  		    throw new messageException("�û����Ѵ��ڣ�"); 
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
	  //�����漰ɾ������������ݣ�ҲҪ����һ��ACID���ҿ�ʼɾ��һ�������Ͳ������������ԤԼ��
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
    
	  
    
   
	  //���վ���Ϊ����ʵ���жϼӷ�ҳ������ֱ������RoomQuery�Ƚ����,����Ҫȷ��ʱ�䲻Ϊ�գ���
	  //С������ָ������һ��������������⣬��������ʵ�����飬���������������β�ѯ������list�����ٷŵ�service��ȥ�����Ҷ�Ӧ�����������������ݿ⣬Ч�ʴ������
	  //��һ��Ҫע����ٲ�ѯ���ص�����������Ϊ�������List��ʮ���Ӵ󣨳�ǧ���򣩣�ͬ���������Ȼ��ÿ�����ӶϿ����ݿ���ٶ�Ҳ����������
	  //�༶������Ϊ�˹᳹mvcģʽ�͸߾ۺϵ���ϣ���Ȼ�����ݿ����Ѿ��õ��˱��������ж��߼��������ͼʡ��ֱ�Ӵ����ݿ⴫������dao�ķ����ͱ��ֻ�ʺϸ����ҵ�����ˣ���Ϲ�����
	  
	  @Override
	public List<EntityWithCondition<Room>>queryRoomByTime(RoomQuery roomQuery)throws Exception{
		  List<EntityWithCondition<Room>> results=new ArrayList<EntityWithCondition<Room>>();
		
		//׼���ڶ������Ĳ�ѯ����һ������ѯ�Ѿ�������roomQuery�У�ע����Ʒ���������������
		//Ϊ�˸����Ժ�����Ч��ֻ����дһ���ж��߼���
		BookingRecordQuery recordQuery=new BookingRecordQuery();
		recordQuery.setRecordCondition(0);
		//��������ѯ����ԤԼ,������Ϊ����û��������sql����ҵ���߼���Ҫ�õ��ֲ��ѯ
		recordQuery.setRoomOnly(true);
		recordQuery.setDateofStart(roomQuery.getJoinRecordStart());
		recordQuery.setDateofEnd(roomQuery.getJoinRecordEnd());
		List<BookingRecord>records=recordService.queryRecords(recordQuery);
		
		//����MIN()�Ĳ��ֻ��Ǿ����ŵ�service���㲻����Ϊ����̫�ػ���������ȥ��һ��query����������dao�еķ�������ͨ��
		for(Room room:this.queryRooms(roomQuery)) {
			//���ڿ���Ҫ����ԤԼ�ļ��裬����ȷ�ϻ����Ƿ񿪷�
			LocalDateTime open=roomQuery.getJoinRecordStart().withHour(room.getOpenTime());
			LocalDateTime close=roomQuery.getJoinRecordEnd().withHour(room.getCloseTime());
			
			String condition="����";
			if(room.getIsBanned()) {
				condition="ͣ����";
			}else if(roomQuery.getJoinRecordStart().isBefore(open)|| roomQuery.getJoinRecordEnd().isAfter(close)) {
				condition="���ڻ�������ʱ����";
			}else {
				//һ��������ϵ㣬��Ϊ����ֻ�п��벻�������ҵ�����ļ�¼���ǵ�ǰ�����еļ�¼�����У�
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
						condition="����ʹ���У�"+itsEnd.getHour()+"ʱ����";
					}else {
						condition="������Ԥ������"+nearest.getHour()+"ʱ��ʼ��"+itsEnd.getHour()+"ʱ����";
					}
				}
			}
			
		   results.add(new EntityWithCondition<Room>(room,condition)); 
		}
		
		return results;
	}
    
}
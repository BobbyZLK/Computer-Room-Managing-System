package com.crbooking.service;

import java.util.List;

import com.crbooking.bean.Room;
import com.crbooking.bean.query.RoomQuery;

public interface RoomService {
	Integer saveRoom(Room room) throws Exception;
	Integer banRoom(Integer id)throws Exception;
	
	Boolean deleteRoom(Integer id) throws Exception;
	
	List<Room> queryRooms (RoomQuery roomquery) throws Exception;
	Integer queryTotalAmount(RoomQuery roomquery)throws Exception;
	List<Room> queryAllRooms() throws Exception;
	Room queryRoom(Integer id)throws Exception;
	Room queryRoom(String roomName)throws Exception;

    List<EntityWithCondition<Room>>queryRoomByTime(RoomQuery roomQuery)throws Exception;
}

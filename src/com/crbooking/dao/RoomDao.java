package com.crbooking.dao;

import java.util.List;

import com.crbooking.bean.Room;
import com.crbooking.bean.query.RoomQuery;

public interface RoomDao {
Integer saveRoom(Room room) throws Exception;
Boolean deleteRoom(Integer i) throws Exception;
List<Room> queryRoom(RoomQuery roomQuery) throws Exception;
Integer queryTotalNumber(RoomQuery roomQuery)throws Exception;
}

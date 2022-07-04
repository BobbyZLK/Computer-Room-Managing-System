package com.crbooking.service;

import java.util.LinkedHashMap;
import java.util.List;

import com.crbooking.bean.Room;
import com.crbooking.bean.Seat;
import com.crbooking.bean.query.SeatQuery;
import com.crbooking.controller.TimepackForReservation;

public interface SeatService {
	Integer saveSeat(Seat seat) throws Exception;
	Integer changeRoom(Integer seatId, Room room)throws Exception;
	Integer banSeat(Integer id)throws Exception;
	Integer setSeatCondition(Integer id,String s)throws Exception;
	Boolean updateSeats(List<Seat>seats)throws Exception;
	
	Boolean deleteSeat(Integer id)throws Exception;
	
	List<Seat> querySeats(SeatQuery seatQuery)throws Exception;
	Integer queryTotalAmount(SeatQuery seatQuery)throws Exception;
	List<Seat> queryAllSeats()throws Exception;
	List<Seat> querySeats(Room room)throws Exception;
	List<Seat> queryByRoomId(Integer roomId)throws Exception;
	Seat querySeat(SeatQuery seatQuery) throws Exception;
	Seat querySeat(Integer id)throws Exception;
	Seat querySeat(String seatName,Room room)throws Exception;
	String querySeatName(Integer id)throws Exception;
	
	Boolean judgeSeatOccupied(Seat seat,TimepackForReservation time)throws Exception;
	String judgeSeatOccupiedReason(Seat seat,TimepackForReservation time)throws Exception;
	LinkedHashMap<Seat,String>querySeatByTime(Integer roomId,TimepackForReservation time)throws Exception;
}

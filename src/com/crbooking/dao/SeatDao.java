package com.crbooking.dao;

import java.util.List;
import com.crbooking.bean.Seat;
import com.crbooking.bean.query.SeatQuery;

public interface SeatDao {
Integer saveSeat(Seat seat)throws Exception;
Boolean updateSeats(List<Seat>seats)throws Exception;
Boolean deleteSeat(Integer i)throws Exception;
Boolean deleteSeats(List<Integer> ids)throws Exception;
List<Seat> querySeat(SeatQuery seatquery)throws Exception;
Integer queryTotalNumber(SeatQuery seatquery)throws Exception;
}

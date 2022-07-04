package com.crbooking.service;

import java.time.LocalDateTime;
import java.util.*;

import com.crbooking.bean.BookingRecord;
import com.crbooking.bean.Room;
import com.crbooking.bean.Seat;
import com.crbooking.bean.Student;
import com.crbooking.bean.query.BookingRecordQuery;

public interface RecordService {
LocalDateTime currentBjTime();
String formatTime(LocalDateTime dateTime);
Integer saveRecord(BookingRecord record)throws Exception;
Integer cancelRecord(Integer recordId) throws Exception;
Boolean cancelRecord(LocalDateTime start,LocalDateTime end)throws Exception;
Boolean updateRecords(List<BookingRecord> records)throws Exception;

Boolean deleteRecord(Integer id) throws Exception;
Boolean deleteRecords(BookingRecordQuery recordQuery) throws Exception;
Boolean deleteRecordByRoom(Room room)throws Exception;
Boolean deleteRecordBySeat(Seat seat)throws Exception;
Boolean deleteRecordByStudent(Student student)throws Exception;
Boolean deleteRecordByDate(LocalDateTime start,LocalDateTime end)throws Exception;

List<BookingRecord> queryRecords(BookingRecordQuery recordQuery)throws Exception;
Integer queryTotalAmount(BookingRecordQuery recordQuery)throws Exception;
BookingRecord queryRecord(Integer recordId)throws Exception;
List<BookingRecord> queryNormalRecordByDate(LocalDateTime start,LocalDateTime end)throws Exception;

Boolean judgeOverlap(BookingRecord record)throws Exception;
String judgeCondition(BookingRecord record)throws Exception;

}

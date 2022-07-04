package com.crbooking.dao;

import java.util.List;
import com.crbooking.bean.BookingRecord;
import com.crbooking.bean.query.BookingRecordQuery;

public interface RecordDao {
Integer saveRecord(BookingRecord record)throws Exception;
Boolean updateRecords(List<BookingRecord> records)throws Exception;
Boolean deleteRecord(Integer id) throws Exception;
Boolean deleteRecords(List<Integer> ids)throws Exception;
List<BookingRecord> queryRecords(BookingRecordQuery recordQuery)throws Exception;
Integer queryTotalNumber(BookingRecordQuery recordQuery)throws Exception;
}

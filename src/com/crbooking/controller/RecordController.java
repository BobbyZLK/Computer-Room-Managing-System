package com.crbooking.controller;

import java.time.Duration;
import java.time.ZoneId;
import java.util.*;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.crbooking.bean.BookingRecord;
import com.crbooking.bean.Room;
import com.crbooking.bean.Seat;
import com.crbooking.bean.Student;
import com.crbooking.bean.query.BookingRecordQuery;
import com.crbooking.bean.query.RoomQuery;
import com.crbooking.service.EntityWithCondition;
import com.crbooking.service.RecordService;
import com.crbooking.service.RoomService;
import com.crbooking.service.SeatService;
import com.crbooking.service.StudentService;


@Controller
@RequestMapping("admin/record")
public class RecordController {
    @Autowired 
	private RecordService recordService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private SeatService seatService;
    @Autowired
    private StudentService studentService;
   
    static Integer pageSize=2; 
    
    //��װrequest��session��ȡ��
    public HttpSession getSession() {
    	ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    	
    	    return attrs.getRequest().getSession();
    	}

   
    
    
    //����������
    @RequestMapping("/totable")
    public ModelAndView toTable(BookingRecordQuery outQuery)throws Exception{
    	HttpSession session=getSession();  	
    	BookingRecordQuery recordQuery=(BookingRecordQuery)session.getAttribute("recordQuery");
    	//��������queryInSessionΪ����û��ָ����ǰҳ�������½��������棬���˳�ʼ���⻹Ҫ��ԭ�������е�session�е�query���
    	if(recordQuery==null || outQuery.getNowPage()==null) {
    		recordQuery=new BookingRecordQuery();
    		recordQuery.setPageSize(pageSize);
    	}else {
    		recordQuery.merge(outQuery);
    	}
    	
    	//page����,ÿ�ζ��Ƿ���һ����ҳ������page��������ʵ���󣬸����Ժ�
    	Page<BookingRecord>page=new Page<BookingRecord>(recordService.queryTotalAmount(recordQuery),pageSize);
    	//�ڽ���������ѯǰ�ټ��һ��nowPage
    	if(recordQuery.getNowPage()<1) {
    	   recordQuery.setNowPage(1);
    	}
    	if(recordQuery.getNowPage()>page.getTotalPages()) {
    	   recordQuery.setNowPage(page.getTotalPages());
    	}
    	page.setNowPage(recordQuery.getNowPage());
    	page.setResults(recordService.queryRecords(recordQuery));
    	
    	//Ҫ�ϴ��Ķ������ѱ��ã�������ֻʣ�ϴ�
    	session.setAttribute("recordQuery", recordQuery);
    	
    	ModelAndView m=new ModelAndView();
    	m.addObject("page", page);
    	m.addObject("query", recordQuery);
    	//���뵱ǰ��������ʱ��
    	m.addObject("Bjnow",recordService.currentBjTime());
        m.setViewName("SystemAdministor/RecordPages/RecordTable");
    	return m;
    }
    
    
    
    //��ʼԤԼ
    @RequestMapping("/toadd")
    public ModelAndView toAdd(RoomQuery outQuery,TimepackForReservation outTime)throws Exception {
        //��ʱ�䲿��ֱ�Ӳ���Record�ݸ��У�Ŭ������ֻ��session�д�ݸ�(��ʱ��)��RoomQuery(�ܻ���)
    	HttpSession session=getSession();
    	RoomQuery roomQuery=(RoomQuery)session.getAttribute("roomQuery");
    	BookingRecord sketch=(BookingRecord)session.getAttribute("sketch");
    	TimepackForReservation time=new TimepackForReservation();
    	//��ʼ��(��һ�ν�ҳ�棩
    	if(roomQuery==null || outQuery.getNowPage()==null || sketch==null) {
    		//ȡ��ǰʱ�����һ��Сʱ,ע��ȫ�̶���Long
       	    time.setStart(recordService.currentBjTime().plusHours(1L));
       	    time.setLastingHours(1L);
    		
    		roomQuery=new RoomQuery();
    		roomQuery.setPageSize(pageSize);
    		roomQuery.setJoinRecordStart(time.getStart());
    		roomQuery.setJoinRecordEnd(time.getStart().plusHours(time.getLastingHours()));
    		
    		//��ʼ����ʱ��׼��һ��ԤԼ�ݸ�
    	    sketch=new BookingRecord();
    		sketch.setStartingTime(time.getStart());
    		sketch.setEndingTime(time.getStart().plusHours(time.getLastingHours()));
    		//������������ʱ��ĸ�����
    	}else {
    		//ʱ��䶯
    		if(outTime!=null) {
    		//���Ҫ��session�д�Ķ���������ס����ô�sketch�г��time���ٸ���sketch���ٽ�roomQuery�е�ʱ�䲿�ָ���
    		time.setStart(sketch.getStartingTime());
    		time.setLastingHours(Duration.between(sketch.getStartingTime(), sketch.getEndingTime()).toHours());
    		time.merge(outTime);
    		sketch.setStartingTime(time.getStart());
    		sketch.setEndingTime(time.getStart().plusHours(time.getLastingHours()));
    		roomQuery.setJoinRecordStart(sketch.getStartingTime());
    		roomQuery.setJoinRecordEnd(sketch.getEndingTime());
    		}
    		//��ʱ���ѯ�������뵥���������Ĺ���һ�£�
            if(outQuery!=null) {
            roomQuery.merge(outQuery);
            }
    	}
    	
    	//��׼��page��Ϊ���жԵ�ǰҳ���Ƿ�Ϸ��ļ��
    	Page<EntityWithCondition<Room>>page=new Page<EntityWithCondition<Room>>(roomService.queryTotalAmount(roomQuery),pageSize);
    	if(roomQuery.getNowPage()<1) {
    		roomQuery.setNowPage(1);
    	}
    	if(roomQuery.getNowPage()>page.getTotalPages()) {
    		roomQuery.setNowPage(page.getTotalPages());
    	}
    	page.setNowPage(roomQuery.getNowPage());
    	page.setResults(roomService.queryRoomByTime(roomQuery));
    	
    	//�������¾߱�ֻ��Ѷ���һ��ҳ��һ��
    	session.setAttribute("roomQuery", roomQuery);
    	session.setAttribute("sketch", sketch);
    	
    	ModelAndView m=new ModelAndView();
    	m.addObject("page", page);
    	m.addObject("time", time);
    	m.addObject("roomQuery", roomQuery);
    	m.setViewName("SystemAdministor/RecordPages/SelectRoom");   	
    	return m;
    }
    
    //ִ�б���
    @RequestMapping("/confirmsave")
    public ModelAndView confirmSave(BookingRecord record)throws Exception{
    	recordService.saveRecord(record);
    	ModelAndView m=new ModelAndView();
    	m.addObject("message", "ԤԼ�ɹ���");
    	m.setViewName("SystemAdministor/RecordPages/ShowMessage");
    	
    	return m;
    }
    
    //ִ��ɾ��
    @RequestMapping("/confirmdelete")
    public ModelAndView confirmDelete(@RequestParam(value="id")String id)throws Exception{
    	Integer i=Integer.parseInt(id);
    	recordService.deleteRecord(i);
    	ModelAndView m=new ModelAndView();
    	m.addObject("message", "ɾ���ɹ���");
    	m.setViewName("SystemAdministor/RecordPages/ShowMessage");
    	return m;
    }
    
    //ȡ��ԤԼ
    @RequestMapping("/cancel")
    public ModelAndView cancelRecord(@RequestParam(value="id")String id)throws Exception{
    	Integer i=Integer.parseInt(id);
    	recordService.cancelRecord(i);
    	ModelAndView m=new ModelAndView();
    	m.addObject("message", "ȡ���ɹ���");
    	m.setViewName("SystemAdministor/RecordPages/ShowMessage");
    	return m;
    }
    
    
    
    //ԤԼ��������
    @RequestMapping("/bookwholeroom")
    public ModelAndView bookWholeRoom(@RequestParam(value="roomId")String roomId)throws Exception{
    	Integer id=Integer.parseInt(roomId);
    	HttpSession session=getSession();
    	BookingRecord sketch=(BookingRecord)session.getAttribute("sketch");
        TimepackForReservation time=(TimepackForReservation)session.getAttribute("time");
    	sketch.setRoom(roomService.queryRoom(id));
    	recordService.setTime(sketch, time);
    	
    	return this.confirmSave(sketch);
    }
    
    //ȷ����λ
    @RequestMapping("/toselectseat")
    public ModelAndView toSelectSeat(@RequestParam(value="roomId")String roomId)throws Exception{
    	Integer id=Integer.parseInt(roomId);
    	HttpSession session=getSession();
    	BookingRecord sketch=(BookingRecord)session.getAttribute("sketch");
        TimepackForReservation time=(TimepackForReservation)session.getAttribute("time");
    	sketch.setRoom(roomService.queryRoom(id));
    	recordService.setTime(sketch, time);
    	LinkedHashMap<Seat,String>seats=seatService.querySeatByTime(id,time);
    	session.setAttribute("sketch", sketch);
    	
    	ModelAndView m=new ModelAndView();
    	m.addObject("seats", seats);
    	m.addObject("roomName", roomService.queryRoomName(id));
    	m.setViewName("SystemAdministor/RecordPages/SelectSeat");
    	return m;
    }
    
    //ȷ���˻�
    @RequestMapping("/toselectstudent")
    public ModelAndView toSelectStudent(@RequestParam(value="seatId")String seatId)throws Exception {
    	Integer id=Integer.parseInt(seatId);
    	HttpSession session=getSession();
    	BookingRecord sketch=(BookingRecord)session.getAttribute("sketch");
    	TimepackForReservation time=(TimepackForReservation)session.getAttribute("time");
    	sketch.setSeat(seatService.querySeat(id));
    	LinkedHashMap<Student,String>students=studentService.queryStudentsAvailable(time.getLastingHours());
    	session.setAttribute("sketch", sketch);
    	
    	ModelAndView m=new ModelAndView();
    	m.addObject("students", students);
    	m.setViewName("SystemAdministor/RecordPages/SelectStudent");
    	return m;
    }
    
    //�ύǰȷ��
    @RequestMapping("/toconfirmrecord")
    public ModelAndView toConfirmRecord(@RequestParam(value="studentId",required=false)String studentId)throws Exception {
    	HttpSession session=getSession();
    	BookingRecord sketch=(BookingRecord)session.getAttribute("sketch");
        
    	if(studentId!=null) {
    		Integer id=Integer.parseInt(studentId);
    		sketch.setStudent(studentService.queryStudent(id));
    	}else {
    		sketch.setStudent(null);
    	}
    	session.setAttribute("sketch", sketch);
    	
    	ModelAndView m=new ModelAndView();
    	m.addObject("record", sketch);
    	m.setViewName("SystemAdministor/RecordPages/BookFinally");
    	return m;
    }
    
    //����ϱ�ע�����У�
    @RequestMapping("/tofinallybook")
    public ModelAndView toFinallyBook(String remark)throws Exception{
    	HttpSession session=getSession();
    	BookingRecord sketch=(BookingRecord)session.getAttribute("sketch");
    	sketch.setRemark(remark);
    	return this.confirmSave(sketch);
    }
    
 
}

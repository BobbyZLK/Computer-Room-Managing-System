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
    
    //封装request和session的取得
    public HttpSession getSession() {
    	ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    	
    	    return attrs.getRequest().getSession();
    	}

   
    
    
    //返回主界面
    @RequestMapping("/totable")
    public ModelAndView toTable(BookingRecordQuery outQuery)throws Exception{
    	HttpSession session=getSession();  	
    	BookingRecordQuery recordQuery=(BookingRecordQuery)session.getAttribute("recordQuery");
    	//后续将以queryInSession为主，没有指定当前页数即重新进入主界面，除了初始化外还要将原来可能有的session中的query清空
    	if(recordQuery==null || outQuery.getNowPage()==null) {
    		recordQuery=new BookingRecordQuery();
    		recordQuery.setPageSize(pageSize);
    	}else {
    		recordQuery.merge(outQuery);
    	}
    	
    	//page配置,每次都是返回一个新页面所以page是描述真实对象，复用性好
    	Page<BookingRecord>page=new Page<BookingRecord>(recordService.queryTotalAmount(recordQuery),pageSize);
    	//在进行真正查询前再检查一次nowPage
    	if(recordQuery.getNowPage()<1) {
    	   recordQuery.setNowPage(1);
    	}
    	if(recordQuery.getNowPage()>page.getTotalPages()) {
    	   recordQuery.setNowPage(page.getTotalPages());
    	}
    	page.setNowPage(recordQuery.getNowPage());
    	page.setResults(recordService.queryRecords(recordQuery));
    	
    	//要上传的东西都已备好，接下来只剩上传
    	session.setAttribute("recordQuery", recordQuery);
    	
    	ModelAndView m=new ModelAndView();
    	m.addObject("page", page);
    	m.addObject("query", recordQuery);
    	//放入当前（北京）时间
    	m.addObject("Bjnow",recordService.currentBjTime());
        m.setViewName("SystemAdministor/RecordPages/RecordTable");
    	return m;
    }
    
    
    
    //开始预约
    @RequestMapping("/toadd")
    public ModelAndView toAdd(RoomQuery outQuery,TimepackForReservation outTime)throws Exception {
        //将时间部分直接并入Record草稿中，努力尝试只在session中存草稿(管时间)和RoomQuery(管机房)
    	HttpSession session=getSession();
    	RoomQuery roomQuery=(RoomQuery)session.getAttribute("roomQuery");
    	BookingRecord sketch=(BookingRecord)session.getAttribute("sketch");
    	TimepackForReservation time=new TimepackForReservation();
    	//初始化(第一次进页面）
    	if(roomQuery==null || outQuery.getNowPage()==null || sketch==null) {
    		//取当前时间的下一个小时,注意全程都是Long
       	    time.setStart(recordService.currentBjTime().plusHours(1L));
       	    time.setLastingHours(1L);
    		
    		roomQuery=new RoomQuery();
    		roomQuery.setPageSize(pageSize);
    		roomQuery.setJoinRecordStart(time.getStart());
    		roomQuery.setJoinRecordEnd(time.getStart().plusHours(time.getLastingHours()));
    		
    		//初始化的时候准备一个预约草稿
    	    sketch=new BookingRecord();
    		sketch.setStartingTime(time.getStart());
    		sketch.setEndingTime(time.getStart().plusHours(time.getLastingHours()));
    		//接下来跳到对时间的更改上
    	}else {
    		//时间变动
    		if(outTime!=null) {
    		//如果要把session中存的东西数限制住，则得从sketch中抽出time，再更新sketch，再将roomQuery中的时间部分更新
    		time.setStart(sketch.getStartingTime());
    		time.setLastingHours(Duration.between(sketch.getStartingTime(), sketch.getEndingTime()).toHours());
    		time.merge(outTime);
    		sketch.setStartingTime(time.getStart());
    		sketch.setEndingTime(time.getStart().plusHours(time.getLastingHours()));
    		roomQuery.setJoinRecordStart(sketch.getStartingTime());
    		roomQuery.setJoinRecordEnd(sketch.getEndingTime());
    		}
    		//非时间查询条件（与单独机房板块的功能一致）
            if(outQuery!=null) {
            roomQuery.merge(outQuery);
            }
    	}
    	
    	//先准备page因为还有对当前页数是否合法的检查
    	Page<EntityWithCondition<Room>>page=new Page<EntityWithCondition<Room>>(roomService.queryTotalAmount(roomQuery),pageSize);
    	if(roomQuery.getNowPage()<1) {
    		roomQuery.setNowPage(1);
    	}
    	if(roomQuery.getNowPage()>page.getTotalPages()) {
    		roomQuery.setNowPage(page.getTotalPages());
    	}
    	page.setNowPage(roomQuery.getNowPage());
    	page.setResults(roomService.queryRoomByTime(roomQuery));
    	
    	//至此万事具备只需把东西一存页面一传
    	session.setAttribute("roomQuery", roomQuery);
    	session.setAttribute("sketch", sketch);
    	
    	ModelAndView m=new ModelAndView();
    	m.addObject("page", page);
    	m.addObject("time", time);
    	m.addObject("roomQuery", roomQuery);
    	m.setViewName("SystemAdministor/RecordPages/SelectRoom");   	
    	return m;
    }
    
    //执行保存
    @RequestMapping("/confirmsave")
    public ModelAndView confirmSave(BookingRecord record)throws Exception{
    	recordService.saveRecord(record);
    	ModelAndView m=new ModelAndView();
    	m.addObject("message", "预约成功！");
    	m.setViewName("SystemAdministor/RecordPages/ShowMessage");
    	
    	return m;
    }
    
    //执行删除
    @RequestMapping("/confirmdelete")
    public ModelAndView confirmDelete(@RequestParam(value="id")String id)throws Exception{
    	Integer i=Integer.parseInt(id);
    	recordService.deleteRecord(i);
    	ModelAndView m=new ModelAndView();
    	m.addObject("message", "删除成功！");
    	m.setViewName("SystemAdministor/RecordPages/ShowMessage");
    	return m;
    }
    
    //取消预约
    @RequestMapping("/cancel")
    public ModelAndView cancelRecord(@RequestParam(value="id")String id)throws Exception{
    	Integer i=Integer.parseInt(id);
    	recordService.cancelRecord(i);
    	ModelAndView m=new ModelAndView();
    	m.addObject("message", "取消成功！");
    	m.setViewName("SystemAdministor/RecordPages/ShowMessage");
    	return m;
    }
    
    
    
    //预约整个机房
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
    
    //确定机位
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
    
    //确定账户
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
    
    //提交前确认
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
    
    //最后补上备注（如有）
    @RequestMapping("/tofinallybook")
    public ModelAndView toFinallyBook(String remark)throws Exception{
    	HttpSession session=getSession();
    	BookingRecord sketch=(BookingRecord)session.getAttribute("sketch");
    	sketch.setRemark(remark);
    	return this.confirmSave(sketch);
    }
    
 
}

package com.crbooking.controller;

import java.util.*;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.crbooking.bean.Room;
import com.crbooking.bean.Seat;
import com.crbooking.bean.query.SeatQuery;
import com.crbooking.service.RoomService;
import com.crbooking.service.SeatService;

@Controller
@RequestMapping("/admin/seat")
public class SeatController {

	@Autowired
	private SeatService seatService;
	@Autowired
	private RoomService roomService;
	
	static Integer pageSize=10;
	
	//��װrequest��session��ȡ��
    public HttpSession getSession() {
    	ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();	
    	return attrs.getRequest().getSession();
    	}

    
	//���ػ�λ������
	@RequestMapping("/totable")
	public ModelAndView toTable(@RequestParam(value="roomId",defaultValue="-1")String roomId,@RequestParam(value="page",required=false)Integer pageNumber)throws Exception{
	Integer i=Integer.parseInt(roomId);
    HttpSession session=getSession();
    
    //����SeatQuery������
    SeatQuery seatQuery=(SeatQuery)session.getAttribute("seatQuery");
    if(seatQuery==null) {
    	seatQuery=new SeatQuery();
    	seatQuery.setPageSize(pageSize);
    }
    if(pageNumber!=null) {
    	seatQuery.setNowPage(pageNumber);
    }else {
    	seatQuery.setNowPage(1);
    }
    if(i!=-1) {
    	seatQuery.setRoom(roomService.queryRoom(i));
    }else {
    	seatQuery.setRoom(null);
    }
    
	//����ҳ��page������
    Page<Seat>page=new Page<Seat>();
    page.setTotalRecords(seatService.queryTotalAmount(seatQuery));
    page.setPageSize(pageSize);
    //���п�����Ҫ�ľ���
    if(seatQuery.getNowPage()>page.getTotalPages()) {
    	seatQuery.setNowPage(page.getTotalPages());
    }
    if(seatQuery.getNowPage()<1) {
    	seatQuery.setNowPage(1);
    }
    page.setNowPage(seatQuery.getNowPage());
    page.setResults(seatService.querySeats(seatQuery));
	
    //����ModelAndView������
    List<Room>rooms=roomService.queryAllRooms();
    session.setAttribute("seatQuery", seatQuery);
    ModelAndView m=new ModelAndView();
	m.addObject("rooms", rooms);
	m.addObject("currentRoomId", i);
	m.addObject("page", page);
	m.setViewName("SystemAdministor/SeatPages/SeatTable");
	return m;
	}
	
	//������λ
	@RequestMapping("/toadd")
	public ModelAndView toAdd(@RequestParam(value="roomId")String roomId) throws Exception{
		List<Room>rooms;
		rooms=roomService.queryAllRooms();
		ModelAndView m=new ModelAndView();
		m.addObject("rooms", rooms);
	    m.addObject("roomId", roomId);
		m.setViewName("SystemAdministor/SeatPages/AddSeat");
		return m;
	}
	
	//�޸Ļ�λ
	@RequestMapping("/toupdate/{id}")
	public ModelAndView toUpdate(@PathVariable String id)throws Exception{
		Integer i=Integer.parseInt(id);
		Seat seat=seatService.querySeat(i);
		List<Room>rooms;
		rooms=roomService.queryAllRooms();
		ModelAndView m=new ModelAndView();
		m.addObject("seat", seat);
		m.addObject("rooms", rooms);
		m.setViewName("SystemAdministor/SeatPages/UpdateSeat");
		return m;
	}
	
	//ִ�б���
	@RequestMapping("/confirmsave")
	public ModelAndView confirmSave(Seat seat)throws Exception{
		if(seat.getRoom().getId()==-1) {
			seat.setRoom(null);
		}
		seatService.saveSeat(seat);
		ModelAndView m=new ModelAndView();
		m.addObject("message", "�����ɹ���");
	    if(seat.getRoom()!=null) {
	    	m.addObject("roomId", seat.getRoom().getId());
	    }
		m.setViewName("SystemAdministor/SeatPages/ShowMessage");
		return m;
	}
	
	//ִ��ɾ��
	@RequestMapping("/confirmdelete")
	public ModelAndView confirmDelete(@RequestParam(value="id")String id)throws Exception{
		Integer i=Integer.parseInt(id);
		ModelAndView m=new ModelAndView();
		m.addObject("message", "ɾ���ɹ���");
		if(seatService.querySeat(i).getRoom()!=null) {
			m.addObject("roomId", seatService.querySeat(i).getRoom().getId());
		}
		m.setViewName("SystemAdministor/SeatPages/ShowMessage");
		seatService.deleteSeat(i);
		return m;
	}
	
	//���ã����ã���λ
	@RequestMapping("/banseat")
	public ModelAndView banSeat(@RequestParam(value="id")String id)throws Exception{
		Integer i=Integer.parseInt(id);
		ModelAndView m=new ModelAndView();
		seatService.banSeat(i);
		if(seatService.querySeat(i).getRoom()!=null) {
			m.addObject("roomId", seatService.querySeat(i).getRoom().getId());
		}
		if(seatService.querySeat(i).getIsBanned()) {
			m.addObject("message", "���û�λ�ɹ���");
		}else {
			m.addObject("message", "���û�λ�ɹ���");
		}
		m.setViewName("SystemAdministor/SeatPages/ShowMessage");
		return m;
	}
}

package com.crbooking.controller;

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
import com.crbooking.bean.query.RoomQuery;
import com.crbooking.service.RoomService;

@Controller
@RequestMapping("/admin/room")
public class RoomController {

	@Autowired
	private RoomService roomService;

	static Integer pageSize=5;
    
	//��װrequest��session��ȡ��
    public HttpSession getSession() {
    	ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();	
    	return attrs.getRequest().getSession();
    	}
    
	
	//���ػ���������,ÿ�������ڵĽ����Seat��
	@RequestMapping("/totable")
	public ModelAndView toTable(@RequestParam(value="page",required=false) Integer pageNumber) throws Exception {
		HttpSession session=getSession();
		
		//����RoomQuery������
		RoomQuery roomQuery=(RoomQuery)session.getAttribute("roomQuery");
		if(roomQuery==null) {
			roomQuery=new RoomQuery();
			roomQuery.setPageSize(pageSize);
		}	
		if(pageNumber!=null) {
			roomQuery.setNowPage(pageNumber);
		}else {
			roomQuery.setNowPage(1);
		}
		
		//����ҳ��page������
		Page<Room> page=new Page<Room>();
		//Ϊ��ֹ���벻��ҳ����Χ���������Ҫ�ȼ������ҳ��
		page.setTotalRecords(roomService.queryTotalAmount(roomQuery));
		page.setPageSize(pageSize);
		//���п�����Ҫ�ľ���
		if(roomQuery.getNowPage()<1) {
			roomQuery.setNowPage(1);
		}
		if(roomQuery.getNowPage()>page.getTotalPages()) {
			roomQuery.setNowPage(page.getTotalPages());
		}
		page.setNowPage(roomQuery.getNowPage());
		page.setResults(roomService.queryRooms(roomQuery));
		page.setTotalRecords(roomService.queryTotalAmount(roomQuery));
		
	    //����ModelAndView������
		session.setAttribute("roomQuery", roomQuery);
		ModelAndView m=new ModelAndView();
		m.addObject("page", page);
		m.setViewName("SystemAdministor/RoomPages/RoomTable"); 
		return m;
	}
	
	//��������
	@RequestMapping("/toadd")
	public String toAdd() {
		return "SystemAdministor/RoomPages/AddRoom";
	}
	
	//�޸Ļ���
	@RequestMapping("/toupdate/{id}")
	public ModelAndView toUpdate(@PathVariable String id) throws Exception{
		Integer i= Integer.parseInt(id);
		Room room=roomService.queryRoom(i);
		ModelAndView m=new ModelAndView();
		m.addObject("room",room);
		m.setViewName("SystemAdministor/RoomPages/UpdateRoom");
		return m;
	}

	//ִ�б���
	@RequestMapping("/confirmsave")
	public ModelAndView confirmSave(Room room) throws Exception {
		roomService.saveRoom(room);
		ModelAndView m=new ModelAndView();
		m.addObject("message", "�����ɹ���");
		m.setViewName("SystemAdministor/RoomPages/ShowMessage");
		return m;
	}
	
	//ִ��ɾ��
	@RequestMapping("/confirmdelete")
	public ModelAndView confirmDelete(@RequestParam(value="id")String id)throws Exception{
		Integer i=Integer.parseInt(id);
		roomService.deleteRoom(i);
		ModelAndView m=new ModelAndView();
		m.addObject("message", "ɾ���ɹ���");
		m.setViewName("SystemAdministor/RoomPages/ShowMessage");
		return m;
	}
	
	//���ã����ã�����
	@RequestMapping("/banroom")
	public ModelAndView banRoom(@RequestParam(value="id")String id)throws Exception{
		Integer i=Integer.parseInt(id);
		ModelAndView m=new ModelAndView();
		roomService.banRoom(i);
		if(roomService.queryRoom(i).getIsBanned()) {
			m.addObject("message", "���û����ɹ���");
		}else {
			m.addObject("message", "���û����ɹ���");
		}
		m.setViewName("SystemAdministor/RoomPages/ShowMessage");
		return m;
	}
	
}

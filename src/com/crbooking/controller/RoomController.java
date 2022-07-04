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
    
	//封装request和session的取得
    public HttpSession getSession() {
    	ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();	
    	return attrs.getRequest().getSession();
    	}
    
	
	//返回机房主界面,每个机房内的界面归Seat管
	@RequestMapping("/totable")
	public ModelAndView toTable(@RequestParam(value="page",required=false) Integer pageNumber) throws Exception {
		HttpSession session=getSession();
		
		//进行RoomQuery的配置
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
		
		//进行页面page的配置
		Page<Room> page=new Page<Room>();
		//为防止输入不在页数范围的情况，需要先计算出总页数
		page.setTotalRecords(roomService.queryTotalAmount(roomQuery));
		page.setPageSize(pageSize);
		//进行可能需要的纠正
		if(roomQuery.getNowPage()<1) {
			roomQuery.setNowPage(1);
		}
		if(roomQuery.getNowPage()>page.getTotalPages()) {
			roomQuery.setNowPage(page.getTotalPages());
		}
		page.setNowPage(roomQuery.getNowPage());
		page.setResults(roomService.queryRooms(roomQuery));
		page.setTotalRecords(roomService.queryTotalAmount(roomQuery));
		
	    //进行ModelAndView的配置
		session.setAttribute("roomQuery", roomQuery);
		ModelAndView m=new ModelAndView();
		m.addObject("page", page);
		m.setViewName("SystemAdministor/RoomPages/RoomTable"); 
		return m;
	}
	
	//新增机房
	@RequestMapping("/toadd")
	public String toAdd() {
		return "SystemAdministor/RoomPages/AddRoom";
	}
	
	//修改机房
	@RequestMapping("/toupdate/{id}")
	public ModelAndView toUpdate(@PathVariable String id) throws Exception{
		Integer i= Integer.parseInt(id);
		Room room=roomService.queryRoom(i);
		ModelAndView m=new ModelAndView();
		m.addObject("room",room);
		m.setViewName("SystemAdministor/RoomPages/UpdateRoom");
		return m;
	}

	//执行保存
	@RequestMapping("/confirmsave")
	public ModelAndView confirmSave(Room room) throws Exception {
		roomService.saveRoom(room);
		ModelAndView m=new ModelAndView();
		m.addObject("message", "操作成功！");
		m.setViewName("SystemAdministor/RoomPages/ShowMessage");
		return m;
	}
	
	//执行删除
	@RequestMapping("/confirmdelete")
	public ModelAndView confirmDelete(@RequestParam(value="id")String id)throws Exception{
		Integer i=Integer.parseInt(id);
		roomService.deleteRoom(i);
		ModelAndView m=new ModelAndView();
		m.addObject("message", "删除成功！");
		m.setViewName("SystemAdministor/RoomPages/ShowMessage");
		return m;
	}
	
	//禁用（启用）机房
	@RequestMapping("/banroom")
	public ModelAndView banRoom(@RequestParam(value="id")String id)throws Exception{
		Integer i=Integer.parseInt(id);
		ModelAndView m=new ModelAndView();
		roomService.banRoom(i);
		if(roomService.queryRoom(i).getIsBanned()) {
			m.addObject("message", "禁用机房成功！");
		}else {
			m.addObject("message", "启用机房成功！");
		}
		m.setViewName("SystemAdministor/RoomPages/ShowMessage");
		return m;
	}
	
}

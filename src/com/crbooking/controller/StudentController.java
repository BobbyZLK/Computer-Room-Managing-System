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

import com.crbooking.bean.Student;
import com.crbooking.bean.query.StudentQuery;
import com.crbooking.service.StudentService;

@Controller
@RequestMapping("/admin/student")
public class StudentController {
    @Autowired 
	private StudentService studentService;
	
    static Integer pageSize=10;
    
  //封装request和session的取得
    public HttpSession getSession() {
    	ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();	
    	return attrs.getRequest().getSession();
    	}

    
    //返回主界面
    @RequestMapping("/totable")
	public ModelAndView toTable(StudentQuery studentQuery)throws Exception{
		HttpSession session=getSession();
		
        //把query存在Session中本意为在换页时只调整当前页数即可，如果与查询功能共用则一定要区分两个不同逻辑
		StudentQuery queryInSession=(StudentQuery)session.getAttribute("queryInSession");
		//先确保从session中取得的不为空，传入的参数studentQuery可以给queryInSesssion赋值，后面应该合并成一个统一的query，我选择queryInSession
		if(queryInSession==null) {
			queryInSession=new StudentQuery();
            queryInSession.setPageSize(pageSize);		
		}
		//根据传入的studentQuery组装queryInSession
		if(studentQuery.getNowPage()==null) {
			//虽然url内无参时所有属性为null，但因为已经注入所以整个对象不为null，只能从nowPage属性上下手了
			queryInSession.setQueryAccount(null);
			queryInSession.setQueryStudent(null);
			queryInSession.setNowPage(1);
		}else {
			//考虑反射并不能把数据还原成精准类型传入queryInSession，最后还得在Service里创建方法
			queryInSession=studentService.mergeQueries(queryInSession, studentQuery);
		}
		
		//page配置
		Page<Student> page=new Page<Student>();
		page.setPageSize(pageSize);
		page.setTotalRecords(studentService.queryTotalAmount(queryInSession));
		if(queryInSession.getNowPage()<1) {
			queryInSession.setNowPage(1);
		}
		if(queryInSession.getNowPage()>page.getTotalPages()) {
			queryInSession.setNowPage(page.getTotalPages());
		}
    	page.setNowPage(queryInSession.getNowPage());
    	page.setResults(studentService.queryStudents(queryInSession));
		
    	//ModelAndView配置
    	session.setAttribute("queryInSession", queryInSession);
		ModelAndView m=new ModelAndView();
		m.addObject("page", page);
		m.addObject("query", queryInSession);
		m.setViewName("SystemAdministor/StudentPages/StudentTable");
		return m;
	}
    
    //新增账户
    @RequestMapping("/toadd")
	public String toAdd() {
		return "SystemAdministor/StudentPages/AddStudent";
	}
    
    //修改账户
    @RequestMapping("/toupdate/{id}")
    public ModelAndView toUpdate(@PathVariable String id)throws Exception{
    	Integer i=Integer.parseInt(id);
    	Student student=studentService.queryStudent(i);
    	if(student.getRemainingHours()<0) {
    		student.setRemainingHours(0);
    	}
    	ModelAndView m=new ModelAndView();
    	m.addObject("student", student);
    	m.setViewName("SystemAdministor/StudentPages/UpdateStudent");
    	return m;
    }
    
    //执行保存
    @RequestMapping("/confirmsave")
    public ModelAndView confirmSave(Student student)throws Exception{
    	studentService.saveStudent(student);
    	ModelAndView m=new ModelAndView();
    	m.addObject("message", "操作成功！");
    	m.setViewName("SystemAdministor/StudentPages/ShowMessage");
    	return m;
    }
    
    //执行删除
    @RequestMapping("/confirmdelete")
    public ModelAndView confirmDelete(@RequestParam(value="id")String id)throws Exception{
    	Integer i=Integer.parseInt(id);
        studentService.deleteStudent(i);
        ModelAndView m=new ModelAndView();
        m.addObject("message", "删除成功！");
        m.setViewName("SystemAdministor/StudentPages/ShowMessage");
        return m;
    }
    
    //禁用（启用）账户
    @RequestMapping("/banstudent")
    public ModelAndView banStudent(@RequestParam(value="id")String id)throws Exception{
    	Integer i=Integer.parseInt(id);
    	ModelAndView m=new ModelAndView();
    	studentService.banStudent(i);
    	if(studentService.queryStudent(i).getIsBanned()) {
    		m.addObject("message", "禁用账户成功！");
    	}else {
    		m.addObject("message", "启用账户成功！");
    	}
    	m.setViewName("SystemAdministor/StudentPages/ShowMessage");
    	return m;
    }
    
    //设置全体上机时长
    @RequestMapping("/maxforall")
    public String toSetMaxForAll() {
    	return "SystemAdministor/StudentPages/MaxTimeForAll";
    }
    
    //确认全体时长修改
    @RequestMapping("/confirmallmax")
    public ModelAndView confirmSetMaxAll(@RequestParam(value="maxhours")String maxhours)throws Exception{
    	Integer newhours=Integer.parseInt(maxhours);
    	studentService.updateAllMaxTime(newhours);
    	ModelAndView m=new ModelAndView();
    	m.addObject("message", "全体上限设置成功！");
    	m.setViewName("SystemAdministor/StudentPages/ShowMessage");
    	return m;
    }
   
}

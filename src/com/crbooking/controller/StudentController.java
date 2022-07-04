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
    
  //��װrequest��session��ȡ��
    public HttpSession getSession() {
    	ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();	
    	return attrs.getRequest().getSession();
    	}

    
    //����������
    @RequestMapping("/totable")
	public ModelAndView toTable(StudentQuery studentQuery)throws Exception{
		HttpSession session=getSession();
		
        //��query����Session�б���Ϊ�ڻ�ҳʱֻ������ǰҳ�����ɣ�������ѯ���ܹ�����һ��Ҫ����������ͬ�߼�
		StudentQuery queryInSession=(StudentQuery)session.getAttribute("queryInSession");
		//��ȷ����session��ȡ�õĲ�Ϊ�գ�����Ĳ���studentQuery���Ը�queryInSesssion��ֵ������Ӧ�úϲ���һ��ͳһ��query����ѡ��queryInSession
		if(queryInSession==null) {
			queryInSession=new StudentQuery();
            queryInSession.setPageSize(pageSize);		
		}
		//���ݴ����studentQuery��װqueryInSession
		if(studentQuery.getNowPage()==null) {
			//��Ȼurl���޲�ʱ��������Ϊnull������Ϊ�Ѿ�ע��������������Ϊnull��ֻ�ܴ�nowPage������������
			queryInSession.setQueryAccount(null);
			queryInSession.setQueryStudent(null);
			queryInSession.setNowPage(1);
		}else {
			//���Ƿ��䲢���ܰ����ݻ�ԭ�ɾ�׼���ʹ���queryInSession����󻹵���Service�ﴴ������
			queryInSession=studentService.mergeQueries(queryInSession, studentQuery);
		}
		
		//page����
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
		
    	//ModelAndView����
    	session.setAttribute("queryInSession", queryInSession);
		ModelAndView m=new ModelAndView();
		m.addObject("page", page);
		m.addObject("query", queryInSession);
		m.setViewName("SystemAdministor/StudentPages/StudentTable");
		return m;
	}
    
    //�����˻�
    @RequestMapping("/toadd")
	public String toAdd() {
		return "SystemAdministor/StudentPages/AddStudent";
	}
    
    //�޸��˻�
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
    
    //ִ�б���
    @RequestMapping("/confirmsave")
    public ModelAndView confirmSave(Student student)throws Exception{
    	studentService.saveStudent(student);
    	ModelAndView m=new ModelAndView();
    	m.addObject("message", "�����ɹ���");
    	m.setViewName("SystemAdministor/StudentPages/ShowMessage");
    	return m;
    }
    
    //ִ��ɾ��
    @RequestMapping("/confirmdelete")
    public ModelAndView confirmDelete(@RequestParam(value="id")String id)throws Exception{
    	Integer i=Integer.parseInt(id);
        studentService.deleteStudent(i);
        ModelAndView m=new ModelAndView();
        m.addObject("message", "ɾ���ɹ���");
        m.setViewName("SystemAdministor/StudentPages/ShowMessage");
        return m;
    }
    
    //���ã����ã��˻�
    @RequestMapping("/banstudent")
    public ModelAndView banStudent(@RequestParam(value="id")String id)throws Exception{
    	Integer i=Integer.parseInt(id);
    	ModelAndView m=new ModelAndView();
    	studentService.banStudent(i);
    	if(studentService.queryStudent(i).getIsBanned()) {
    		m.addObject("message", "�����˻��ɹ���");
    	}else {
    		m.addObject("message", "�����˻��ɹ���");
    	}
    	m.setViewName("SystemAdministor/StudentPages/ShowMessage");
    	return m;
    }
    
    //����ȫ���ϻ�ʱ��
    @RequestMapping("/maxforall")
    public String toSetMaxForAll() {
    	return "SystemAdministor/StudentPages/MaxTimeForAll";
    }
    
    //ȷ��ȫ��ʱ���޸�
    @RequestMapping("/confirmallmax")
    public ModelAndView confirmSetMaxAll(@RequestParam(value="maxhours")String maxhours)throws Exception{
    	Integer newhours=Integer.parseInt(maxhours);
    	studentService.updateAllMaxTime(newhours);
    	ModelAndView m=new ModelAndView();
    	m.addObject("message", "ȫ���������óɹ���");
    	m.setViewName("SystemAdministor/StudentPages/ShowMessage");
    	return m;
    }
   
}

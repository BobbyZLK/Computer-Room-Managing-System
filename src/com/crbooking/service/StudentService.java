package com.crbooking.service;

import java.util.*;

import com.crbooking.bean.Student;
import com.crbooking.bean.query.StudentQuery;

public interface StudentService {
	Integer saveStudent(Student student)throws Exception;
	Integer updateRemainTime(Integer studentId,Integer newhours)throws Exception;
	Integer banStudent(Integer id)throws Exception;
	Boolean updateAllMaxTime(Integer newhours)throws Exception;
	Integer minusRemainHours(Integer id,Long difference)throws Exception;
	StudentQuery mergeQueries(StudentQuery accepted,StudentQuery insert);
	
	Boolean deleteStudent(Integer id)throws Exception;
	
	List<Student> queryStudents(StudentQuery studentQuery)throws Exception;
	Integer queryTotalAmount(StudentQuery studentQuery)throws Exception;
	List<Student> queryAllStudents()throws Exception;
	Student queryStudent(Integer id)throws Exception;
	Student queryStudent(String accountName)throws Exception;
	String queryAccountName(Integer id)throws Exception;
	
	Boolean judgeStudentOccupied(Student student,Integer lastingHours)throws Exception;
	String judgeStudentOccupiedReason(Student student,Integer lastingHours)throws Exception;
    List<Student>queryStudentsAvailable(Integer joinRecordByLastingHours)throws Exception;
}

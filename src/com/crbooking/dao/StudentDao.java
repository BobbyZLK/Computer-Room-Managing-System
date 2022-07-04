package com.crbooking.dao;

import java.util.List;
import com.crbooking.bean.Student;
import com.crbooking.bean.query.StudentQuery;

public interface StudentDao {
Integer saveStudent(Student student)throws Exception;
Boolean updateStudents(List<Student>students)throws Exception;
Boolean deleteStudent(Integer id)throws Exception;
List<Student> queryStudents(StudentQuery studentQuery) throws Exception;
Integer queryTotalNumber(StudentQuery studentQuery)throws Exception;
}

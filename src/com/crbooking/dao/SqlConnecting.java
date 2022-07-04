package com.crbooking.dao;
import java.sql.*;
public class SqlConnecting {
	static final String JDBC_DRIVER= "com.mysql.cj.jdbc.Driver"; 
	static final String DB_URL = "jdbc:mysql://localhost:3306/crbooking?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8";
	
	static final String USER = "root";
    static final String PASS = "123456";
    static {
    try {
    	
    	Class.forName(JDBC_DRIVER);//动态获取类型并加载之，方便工厂模式的newInstance()批量实例化，还能便于更换从.xml文件取得的类型，实现解耦
    }catch(Exception e) {
    	e.printStackTrace();
    }
    
}
    public static Connection getConnection() throws SQLException{
		return DriverManager.getConnection(DB_URL, USER, PASS);
	}

}
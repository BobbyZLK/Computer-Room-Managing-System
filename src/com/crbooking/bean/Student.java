package com.crbooking.bean;

import java.time.LocalDate;
import java.util.Objects;


public class Student extends Entity{
      private String accountName;
      private String accountKey;
      private String studentName;
      private String studentSex;
      private Boolean isBanned;
      private Integer remainingHours;
      private Integer maxHours;
      private LocalDate logInDate;
      private String referringKey;
      
      //用户的账号名是唯一的
      @Override
      public boolean equals(Object o) {
    	  if(o==this) return true;
    	  if(o==null || o.getClass()!=getClass()) return false;
    	  
    	  Student student=(Student)o;
    	  return accountName.equals(student.getAccountName());
      }
      
      @Override
      public int hashCode() {
    	  return Objects.hash(accountName);
      }
      
      
      public void setAccountName(String string) {
    	  accountName=string;
      }
      public String getAccountName() {
    	  return accountName;
      }
     
      public void setStudentName(String s) {
    	  studentName=s;
      }
      public String getStudentName() {
    	  return studentName;
      }
      
      public void setIsBanned(Boolean b) {
    	  isBanned=b;
      }
      public Boolean getIsBanned() {
    	  return isBanned;
      }
	public Integer getRemainingHours() {
		return remainingHours;
	}
	public void setRemainingHours(Integer remainingHours) {
		this.remainingHours = remainingHours;
	}
	public String getAccountKey() {
		return accountKey;
	}
	public void setAccountKey(String accountKey) {
		this.accountKey = accountKey;
	}
	public String getStudentSex() {
		return studentSex;
	}
	public void setStudentSex(String studentSex) {
		this.studentSex = studentSex;
	}
	public String getReferringKey() {
		return referringKey;
	}
	public void setReferringKey(String referringKey) {
		this.referringKey = referringKey;
	}
	public Integer getMaxHours() {
		return maxHours;
	}
	public void setMaxHours(Integer maxHours) {
		this.maxHours = maxHours;
	}
	public LocalDate getLogInDate() {
		return logInDate;
	}
	public void setLogInDate(LocalDate logInDate) {
		this.logInDate = logInDate;
	}
     
      
}

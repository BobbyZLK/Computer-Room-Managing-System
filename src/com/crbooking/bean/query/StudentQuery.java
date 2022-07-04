package com.crbooking.bean.query;

import java.time.LocalDate;

public class StudentQuery extends Query{
    private String accountName;
    private String accountKey;
    private String studentName;
    private String studentSex;
    private Boolean isBanned;
    private Integer remainingHours;
    private Integer maxHours;
    private LocalDate logInDate;
    private String referringKey;
    private String queryAccount;
    private String queryStudent;
    
    private Integer nowPage;
    private Integer pageSize;

    
    //用于多表查询
    private Integer joinRecordByLastingHours;
    public void setAccountName(String s) {
  	  accountName=s;
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
	public String getQueryAccount() {
		return queryAccount;
	}
	public void setQueryAccount(String queryAccount) {
		this.queryAccount = queryAccount;
	}
	public String getQueryStudent() {
		return queryStudent;
	}
	public void setQueryStudent(String queryStudent) {
		this.queryStudent = queryStudent;
	}
	public Integer getNowPage() {
		return nowPage;
	}
	public void setNowPage(Integer nowPage) {
		this.nowPage = nowPage;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getJoinRecordByLastingHours() {
		return joinRecordByLastingHours;
	}
	public void setJoinRecordByLastingHours(Integer joinRecordByLastingHours) {
		this.joinRecordByLastingHours = joinRecordByLastingHours;
	}
	
}

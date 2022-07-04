package com.crbooking.controller;


import java.util.List;


public class Page<T> {

	//结果集
	private List<T> results;
	//当前页数
	private Integer nowPage;
	//总记录数（为了解耦）
	private Integer totalRecords;
	//每页规模（即使不输出也要用于计算）
	private Integer pageSize;

	//首先设定成：如果没有缺失了单页记录数和总记录数就不允许新建页面,且观察到泛型只需在定义类时指出不必在构造器重复（反正在运行中抹去）
	public Page(Integer totalRecords,Integer pageSize){
		this.totalRecords=totalRecords;
		this.pageSize=pageSize;
	}
	
	//总页数
	public Integer getTotalPages() {
		return (totalRecords-1)/pageSize+1;
	}
	
	
	
	public List<T> getResults() {
		return results;
	}
	public void setResults(List<T> results) {
		this.results = results;
	}
	
	public Integer getNowPage() {
		return nowPage;
	}
	public void setNowPage(Integer nowPage) {
		this.nowPage = nowPage;
	}
	public Integer getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
}

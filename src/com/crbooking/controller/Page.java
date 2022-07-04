package com.crbooking.controller;


import java.util.List;


public class Page<T> {

	//�����
	private List<T> results;
	//��ǰҳ��
	private Integer nowPage;
	//�ܼ�¼����Ϊ�˽��
	private Integer totalRecords;
	//ÿҳ��ģ����ʹ�����ҲҪ���ڼ��㣩
	private Integer pageSize;

	//�����趨�ɣ����û��ȱʧ�˵�ҳ��¼�����ܼ�¼���Ͳ������½�ҳ��,�ҹ۲쵽����ֻ���ڶ�����ʱָ�������ڹ������ظ���������������Ĩȥ��
	public Page(Integer totalRecords,Integer pageSize){
		this.totalRecords=totalRecords;
		this.pageSize=pageSize;
	}
	
	//��ҳ��
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

package com.crbooking.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

public class TimepackForReservation {
//��΢����һ�£�һ����ԤԼ��ʱ�䣬һ���ǳ���ʱ���������������ʱ�䣩����ʵ���������ʱ�䣨��ʼ����������ǰ���գ�
private LocalDateTime start;
private Long lastingHours;

//���ڽ��մ�ҳ�洫�ص��޸�
private Integer day;
private Integer startHour;

//����ͬ��ϲ�
public TimepackForReservation merge(TimepackForReservation anotherTime) {
	if(anotherTime.getDay()!=null) {
		this.start.withDayOfMonth(anotherTime.getDay());
	}
	if(anotherTime.getStartHour()!=null) {
		this.start.withHour(anotherTime.getStartHour());
	}
	if(anotherTime.lastingHours!=null) {
		this.lastingHours=anotherTime.getLastingHours();
	}
	return this;
}

//����ȡ�õ����������
public Integer getMaxDay() {
	LocalDateTime clone=start.with(TemporalAdjusters.lastDayOfMonth());
	return clone.getDayOfMonth();
}
public LocalDateTime getNow() {
	//�����ð�������Ūһ������ʱ���
	LocalDateTime now=LocalDateTime.now();
	ZoneId zone=ZoneId.of("Asia/Shanghai");
	return now.atZone(zone).toLocalDateTime();
}




public LocalDateTime getStart() {
	return start;
}
public void setStart(LocalDateTime start) {
	this.start = start;
}


public Long getLastingHours() {
	return lastingHours;
}


public void setLastingHours(Long lastingHours) {
	this.lastingHours = lastingHours;
}


public Integer getDay() {
	return day;
}


public void setDay(Integer day) {
	this.day = day;
}


public Integer getStartHour() {
	return startHour;
}


public void setStartHour(Integer startHour) {
	this.startHour = startHour;
}



}

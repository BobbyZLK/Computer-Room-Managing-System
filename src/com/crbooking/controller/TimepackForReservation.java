package com.crbooking.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

public class TimepackForReservation {
//稍微精简一下，一个是预约的时间，一个是持续时长（用来计算结束时间），其实最好是三个时间（开始、结束、当前参照）
private LocalDateTime start;
private Long lastingHours;

//用于接收从页面传回的修改
private Integer day;
private Integer startHour;

//用于同类合并
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

//用于取得当月最大天数
public Integer getMaxDay() {
	LocalDateTime clone=start.with(TemporalAdjusters.lastDayOfMonth());
	return clone.getDayOfMonth();
}
public LocalDateTime getNow() {
	//别引用包了重新弄一个北京时间吧
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

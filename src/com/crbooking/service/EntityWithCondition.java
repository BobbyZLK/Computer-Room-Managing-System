package com.crbooking.service;

import com.crbooking.bean.Entity;

public class EntityWithCondition<T extends Entity> {
T entity;
String condition;

public EntityWithCondition(T entity,String condition) {
	this.entity=entity;
	this.condition=condition;
}


public T getEntity() {
	return entity;
}


public void setEntity(T entity) {
	this.entity = entity;
}
public String getCondition() {
	return condition;
}
public void setCondition(String condition) {
	this.condition = condition;
}

}

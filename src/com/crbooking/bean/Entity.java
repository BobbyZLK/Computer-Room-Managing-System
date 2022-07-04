package com.crbooking.bean;

public abstract class Entity {
protected Integer id;

public Integer getId() {
	return id;
}

public void setId(Integer id) {
	this.id = id;
}

@Override
public abstract boolean equals(Object o);

@Override
public abstract int hashCode();
}

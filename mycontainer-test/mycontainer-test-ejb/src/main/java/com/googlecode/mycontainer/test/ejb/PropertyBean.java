package com.googlecode.mycontainer.test.ejb;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PropertyBean {

	private String key;

	private String value;

	public PropertyBean() {
	}

	public PropertyBean(String key, String value) {
		this.key = key;
		this.value = value;
	}

	@Id
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}

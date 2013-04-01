package com.googlecode.mycontainer.gae.test;

import java.io.Serializable;

public class Message implements Serializable {

	private static final long serialVersionUID = 5970158120006634438L;

	private Long id;

	private String text;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}

package com.googlecode.mycontainer.commons.servlet.json;

import java.io.Serializable;
import java.util.Map;

public class TestCustomer implements Serializable, TestCustomerInterface {

	private static final long serialVersionUID = 359587946748610700L;

	private String name;

	private int age;

	private TestCustomer father;

	private Map<String, Object> content;

	public TestCustomer() {

	}

	public TestCustomer(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public void setAllBlocked(String name, int age, TestCustomer father) {
		setAll(name, age, father);
	}

	public void setAll(String name, int age, TestCustomer father) {
		this.name = name;
		this.age = age;
		this.father = father;
	}

	public TestCustomer getFather() {
		return father;
	}

	public void setFather(TestCustomer father) {
		this.father = father;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Map<String, Object> getContent() {
		return content;
	}

	@SuppressWarnings("unchecked")
	public void setContent(Map<String, ?> content) {
		this.content = (Map<String, Object>) content;
	}

}

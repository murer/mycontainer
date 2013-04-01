package com.googlecode.mycontainer.commons.servlet.json;

import java.util.Map;

import com.googlecode.mycontainer.annotation.Allow;

public interface TestCustomerInterface {

	public abstract void setAllBlocked(String name, int age, TestCustomer father);

	@Allow("view")
	public abstract void setAll(String name, int age, TestCustomer father);

	public abstract TestCustomer getFather();

	public abstract void setFather(TestCustomer father);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract int getAge();

	public abstract void setAge(int age);

	public abstract Map<String, Object> getContent();

	public abstract void setContent(Map<String, ?> content);

}
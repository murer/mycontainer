package com.googlecode.mycontainer.commons.json;

import flexjson.JSON;

public class TestJsonPhone {

	private Long id;

	private String number;

	private TestJsonCustomer customer;

	private TestJsonPhone mainPhone;

	@JSON(include = false)
	public TestJsonCustomer getCustomer() {
		return customer;
	}

	public void setCustomer(TestJsonCustomer customer) {
		this.customer = customer;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestJsonPhone other = (TestJsonPhone) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		return true;
	}

	public TestJsonPhone getMainPhone() {
		return mainPhone;
	}

	public void setMainPhone(TestJsonPhone mainPhone) {
		this.mainPhone = mainPhone;
	}

}

package com.googlecode.mycontainer.test.web;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

import com.googlecode.mycontainer.test.CustomerService;

@ManagedBean(name = "testMB")
public class TestManagedBean {

	@EJB
	private CustomerService customerService;

	public String getAllCustomers() {
		return customerService.findAll().toString();
	}

	public String getVersion() {
		return "v-e-r-s-i-o-n";
	}

	public String concat(String a, String b) {
		return "" + a + "-" + b;
	}
}

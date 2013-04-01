package com.googlecode.mycontainer.test.ejb;

import java.text.ParseException;

import javax.ejb.Local;
import javax.ejb.Stateless;

import com.googlecode.mycontainer.test.Sum2Service;

@Stateless
@Local(Sum2Service.class)
public class Sum2ServiceBean {

	public Integer divide(Integer a, Integer b) {
		return null;
	}

	public Integer sum(Integer a, Integer b) {
		return null;
	}

	public Integer sum(String a, String b) throws ParseException {
		return null;
	}

}

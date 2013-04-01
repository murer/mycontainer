package com.googlecode.mycontainer.test.ejb;

import java.text.ParseException;

import javax.ejb.Stateless;

import com.googlecode.mycontainer.test.Sum3Service;
import com.googlecode.mycontainer.test.Sum4Service;

@Stateless
public class Sum3ServiceBean implements Sum3Service, Sum4Service {

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

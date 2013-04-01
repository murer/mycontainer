package com.googlecode.mycontainer.commons.reflect;

import com.googlecode.mycontainer.annotation.Allow;

public interface TestService {

	public Integer sum(Integer a, Integer b);

	@Allow("web")
	public Integer sumAllowed(Integer a, Integer b);
}

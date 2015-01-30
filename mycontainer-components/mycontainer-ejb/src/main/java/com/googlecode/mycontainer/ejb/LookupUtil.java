package com.googlecode.mycontainer.ejb;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.NamingException;

import com.googlecode.mycontainer.kernel.naming.MyNameParser;

public class LookupUtil {

	public static Object lookupEJB(EJB ejb, Class<?> type, Context context) throws NamingException {
		String beanName;
		if (ejb.mappedName().trim().length() > 0) {
			beanName = ejb.mappedName().trim();
		} else if (ejb.beanInterface() == Object.class) {
			beanName = MyNameParser.parseClassName("ejb", type);
		} else {
			beanName = MyNameParser.parseClassName("ejb", ejb.beanInterface());
		}
		return context.lookup(beanName);
	}

	public static Object lookupResource(Resource resource, Class<?> type, Context context) throws NamingException {
		String beanName;
		if (resource.mappedName().trim().length() > 0) {
			beanName = resource.mappedName().trim();
		} else if (resource.type() == Object.class) {
			beanName = MyNameParser.parseClassName("resource", type);
		} else {
			beanName = MyNameParser.parseClassName("resource", resource.type());
		}

		return context.lookup(beanName);
	}

}

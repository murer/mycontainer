package com.googlecode.mycontainer.ejb;

import java.lang.reflect.Field;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.NamingException;

import com.googlecode.mycontainer.kernel.naming.MyNameParser;
import com.googlecode.mycontainer.kernel.reflect.ReflectUtil;

public class InjectionUtil {

	public static void inject(Object instance, Context context) throws NamingException {
		injectEjbs(instance, context);
		injectResource(instance, context);
	}

	public static void injectEjbs(Object instance, Context context) throws NamingException {
		Field fields[] = instance.getClass().getDeclaredFields();

		for (Field field : fields) {
			if (field.isAnnotationPresent(EJB.class)) {
				EJB ejb = field.getAnnotation(EJB.class);

				String beanName;
				if (ejb.mappedName().trim().length() > 0) {
					beanName = ejb.mappedName().trim();
				} else if (ejb.beanInterface() == Object.class) {
					beanName = MyNameParser.parseClassName("ejb", field.getType());
				} else {
					beanName = MyNameParser.parseClassName("ejb", ejb.beanInterface());
				}

				ReflectUtil.setField(field, instance, context.lookup(beanName));
			}
		}
	}

	public static void injectResource(Object instance, Context context) throws NamingException {
		Field fields[] = instance.getClass().getDeclaredFields();

		for (Field field : fields) {
			if (field.isAnnotationPresent(Resource.class)) {
				Resource resource = field.getAnnotation(Resource.class);

				String beanName;
				if (resource.mappedName().trim().length() > 0) {
					beanName = resource.mappedName().trim();
				} else if (resource.type() == Object.class) {
					beanName = MyNameParser.parseClassName("resource", field.getType());
				} else {
					beanName = MyNameParser.parseClassName("resource", resource.type());
				}

				ReflectUtil.setField(field, instance, context.lookup(beanName));
			}
		}
	}

}

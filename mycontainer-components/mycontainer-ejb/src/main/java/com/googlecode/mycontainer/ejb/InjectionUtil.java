package com.googlecode.mycontainer.ejb;

import java.lang.reflect.Field;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.NamingException;

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

				Class<?> type = field.getType();
				Object ejbInstance = LookupUtil.lookupEJB(ejb, type, context);
				ReflectUtil.setField(field, instance, ejbInstance);
			}
		}
	}

	public static void injectResource(Object instance, Context context) throws NamingException {
		Field fields[] = instance.getClass().getDeclaredFields();

		for (Field field : fields) {
			if (field.isAnnotationPresent(Resource.class)) {
				Resource resource = field.getAnnotation(Resource.class);
				Class<?> type = field.getType();

				Object resourceInstance = LookupUtil.lookupResource(resource, type, context);
				ReflectUtil.setField(field, instance, resourceInstance);
			}
		}
	}

}

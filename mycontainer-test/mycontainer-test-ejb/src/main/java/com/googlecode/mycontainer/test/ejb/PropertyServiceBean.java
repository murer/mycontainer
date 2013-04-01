package com.googlecode.mycontainer.test.ejb;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.googlecode.mycontainer.test.PropertyService;

@Stateless
public class PropertyServiceBean implements Serializable, PropertyService {

	private static final long serialVersionUID = -8375518556306509370L;

	@PersistenceContext(unitName = "test-pu")
	private EntityManager em;

	public String getProperty(String key) {
		PropertyBean prop = em.find(PropertyBean.class, key);
		if (prop == null) {
			return null;
		}
		return prop.getValue();
	}

	public void setProperty(String key, String value) {
		PropertyBean prop = new PropertyBean(key, value);
		em.persist(prop);
	}

}

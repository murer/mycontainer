package com.googlecode.mycontainer.ejb;

public interface EJBCallback {

	public void ejbPreConstruct();

	public void ejbPostConstruct();

	public void ejbPreDestroy();

	public void ejbPostDestroy();

}

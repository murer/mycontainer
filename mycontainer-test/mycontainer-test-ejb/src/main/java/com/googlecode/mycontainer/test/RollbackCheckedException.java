package com.googlecode.mycontainer.test;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class RollbackCheckedException extends Exception	 {

	private static final long serialVersionUID = -594339532044286676L;

}
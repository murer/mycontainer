package com.googlecode.mycontainer.darkproxy;

import com.googlecode.mycontainer.util.Util;

public class DarkProxyId {

	private static final Object MUTEX = new Object();

	private static long currentId = 0L;

	public static Long nextId() {
		return createId();
	}

	private static long createId() {
		synchronized (MUTEX) {
			if (currentId <= 0L) {
				currentId = System.currentTimeMillis();
			}
			while (true) {
				long now = System.currentTimeMillis();
				if (now > currentId) {
					currentId = now;
					return currentId;
				}
				Util.sleep(0L, 333333);
			}
		}
	}

	public static void main(String[] args) {
		System.out.println(nextId());
		System.out.println(nextId());
		System.out.println(nextId());
		System.out.println(nextId());
		System.out.println(nextId());
		System.out.println(nextId());
		System.out.println(nextId());
		System.out.println(nextId());

	}

}

package com.googlecode.mycontainer.commons.lang;

public class ThreadUtil {

	public static void sleep(long time) {
		if (time == 0l) {
			Thread.yield();
		} else if (time > 0) {
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				throw new RuntimeException("error while sleeping: " + time, e);
			}
		}
	}

}

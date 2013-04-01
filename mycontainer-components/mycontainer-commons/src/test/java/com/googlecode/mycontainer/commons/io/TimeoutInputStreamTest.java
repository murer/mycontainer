package com.googlecode.mycontainer.commons.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import org.junit.Test;

import com.googlecode.mycontainer.commons.io.TimeoutInputStream;


public class TimeoutInputStreamTest {

	@Test
	public void testOk() throws Exception {
		ByteArrayInputStream bin = new ByteArrayInputStream(
				"this is my test".getBytes());
		TimeoutInputStream in = new TimeoutInputStream(bin, 500l);
		DataInputStream data = new DataInputStream(in);
		byte[] buffer = new byte["this is my test".length()];
		data.readFully(buffer);
		assertEquals("this is my test", new String(buffer));
		try {
			data.read();
			fail("IllegalStateException expected");
		} catch (IllegalStateException e) {
			assertEquals("timeout", e.getMessage());
		}
		data.close();
	}

	@Test
	public void testTimeout() throws Exception {
		try {
			MockInputStream mock = new MockInputStream();
			TimeoutInputStream in = new TimeoutInputStream(mock, 500l);
			DataInputStream data = new DataInputStream(in);
			byte[] buffer = new byte["this is my test".length()];
			data.readFully(buffer);
			fail("RuntimeException expected");
		} catch (RuntimeException e) {
			assertEquals("timeout", e.getMessage());
		}
	}

}

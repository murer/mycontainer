package com.googlecode.mycontainer.commons.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class EngineResponseWrapper extends HttpServletResponseWrapper {

	public EngineResponseWrapper(HttpServletResponse response) {
		super(response);
	}

	private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	private PrintWriter writer = null;
	private ServletOutputStream out = null;

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (writer != null) {
			throw new RuntimeException("getWriter() was called");
		}
		if (out == null) {
			out = new ServletOutputStreamWrapper(buffer);
		}
		return out;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (out != null) {
			throw new RuntimeException("getOutputStream() was called");
		}
		if (writer == null) {
			String characterEncoding = getResponse().getCharacterEncoding();
			OutputStreamWriter wrt = new OutputStreamWriter(buffer,
					characterEncoding);
			writer = new PrintWriter(wrt);
		}
		return writer;
	}

	public byte[] getBuffer() {
		try {
			if (writer != null) {
				writer.flush();
			}
			if (out != null) {
				out.flush();
			}
			return buffer.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setContentLength(int len) {
		// We cant pass length to the original response :)
	}

}

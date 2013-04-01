package com.googlecode.mycontainer.commons.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.net.SocketFactory;

import com.googlecode.mycontainer.commons.io.IOUtil;
import com.googlecode.mycontainer.commons.lang.StringUtil;
import com.googlecode.mycontainer.commons.regex.RegexUtil;

public class HttpRawProtocol implements Closeable {

	public static enum State {
		CREATED, READY, SENDING_HEADERS, UPLOADING, READING_HEADERS, DOWNLOADING, CLOSED;

		public State check(State... states) {
			for (State s : states) {
				if (this.equals(s)) {
					return this;
				}
			}
			throw new RuntimeException("exp: " + Arrays.toString(states)
					+ ", but was: " + this);

		}
	}

	private static final Pattern PATTERN_RESPONSE_LINE = Pattern
			.compile("^([^\\s]+) ([^\\s]+) ((.*))$");

	private static final Pattern PATTERN_HEADER = Pattern
			.compile("^([^\\s:]+)[\\s:]+((.*))$");

	private State state = State.CREATED;
	private Socket socket;
	private SocketFactory socketFactory = SocketFactory.getDefault();
	private OutputStream outputStream = null;
	private byte[] lineFeed = "\n".getBytes();
	private String protocolVersion;
	private Integer code;

	private String codeMessage;

	private PushbackInputStream inputStream;

	private String header;

	private String[] headerValues;

	public State getState() {
		return state;
	}

	public void connect(String address, int port) {
		try {
			state.check(State.CREATED);
			socket = socketFactory.createSocket(address, port);
			state = State.READY;
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void sendRequest(String method, String uri, String protocolVersion) {
		state.check(State.READY);
		sendLine(method, ' ', uri, ' ', protocolVersion);
		state = State.SENDING_HEADERS;
	}

	private void sendLine(Object... data) {
		try {
			OutputStream out = getOutputStream();
			for (Object v : data) {
				String str = v.toString();
				out.write(str.getBytes());
			}
			out.write(lineFeed);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private OutputStream getOutputStream() {
		if (outputStream == null) {
			try {
				outputStream = new BufferedOutputStream(
						socket.getOutputStream());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return outputStream;
	}

	public void sendHeader(String header, String... values) {
		state.check(State.SENDING_HEADERS);
		StringBuilder value = StringUtil.join(null, ",", values);
		sendLine(header, ": ", value);
	}

	public void sendHeaderFinished() {
		state.check(State.SENDING_HEADERS);
		sendLine();
		state = State.UPLOADING;
	}

	public String readProtocolVersion() {
		state.check(State.UPLOADING, State.READING_HEADERS);
		flush();
		state = State.READING_HEADERS;
		StringBuilder line = readLine();
		if (line == null) {
			return null;
		}
		List<String> groups = RegexUtil.groups(PATTERN_RESPONSE_LINE, line);
		this.protocolVersion = groups.get(1).trim();
		this.code = Integer.parseInt(groups.get(2).trim());
		this.codeMessage = "";
		if (groups.size() > 3) {
			this.codeMessage = groups.get(3).trim();
		}
		return this.protocolVersion;
	}

	private void flush() {
		try {
			getOutputStream().flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected StringBuilder readLine() {
		PushbackInputStream in = getInputStream();
		StringBuilder ret = new StringBuilder();
		try {
			while (true) {
				int read = in.read();
				if (read < 0) {
					throw new RuntimeException("input closed");
				}
				if (read == '\r') {
					continue;
				}
				if (read == '\n') {
					break;
				}
				ret.append((char) read);
			}
			return ret;
		} catch (SocketTimeoutException e) {
			try {
				in.unread(ret.toString().getBytes());
				return null;
			} catch (IOException e1) {
				throw new RuntimeException(e);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public PushbackInputStream getInputStream() {
		if (this.inputStream == null) {
			try {
				this.inputStream = new PushbackInputStream(
						new BufferedInputStream(socket.getInputStream()));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return this.inputStream;
	}

	public Integer readCode() {
		state.check(State.READING_HEADERS);
		return code;
	}

	public String readCodeMessage() {
		state.check(State.READING_HEADERS);
		return codeMessage;
	}

	public String readHeader() {
		state.check(State.READING_HEADERS);
		StringBuilder line = readLine();
		if (line == null) {
			return null;
		}
		if (line.length() == 0) {
			state = State.DOWNLOADING;
			return null;
		}
		List<String> groups = RegexUtil.groups(PATTERN_HEADER, line);
		this.header = groups.get(1);
		String value = groups.get(2);
		this.headerValues = value.split(",");
		for (int i = 0; i < this.headerValues.length; i++) {
			this.headerValues[i] = this.headerValues[i].trim();
		}
		return this.header;
	}

	public String[] readHeaderValues() {
		state.check(State.READING_HEADERS);
		return this.headerValues;
	}

	public void sendBytes(byte[] bytes) {
		try {
			state.check(State.UPLOADING);
			getOutputStream().write(bytes);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		IOUtil.close(socket);
		state = State.CLOSED;
	}

	public void setSoTimeout(int l) {
		try {
			state.check(State.READY, State.SENDING_HEADERS, State.UPLOADING,
					State.READING_HEADERS, State.DOWNLOADING);
			socket.setSoTimeout(l);
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
	}

}

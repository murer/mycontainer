package com.googlecode.mycontainer.darkproxy;

public class DarkProxyConn {

	private Long id;
	private DarkProxyRequest request;
	private DarkProxyResponse response;

	public Long getId() {
		return id;
	}

	public DarkProxyConn setId(Long id) {
		this.id = id;
		return this;
	}

	public DarkProxyRequest getRequest() {
		return request;
	}

	public DarkProxyConn setRequest(DarkProxyRequest request) {
		this.request = request;
		return this;
	}

	public DarkProxyResponse getResponse() {
		return response;
	}

	public DarkProxyConn setResponse(DarkProxyResponse response) {
		this.response = response;
		return this;
	}

}

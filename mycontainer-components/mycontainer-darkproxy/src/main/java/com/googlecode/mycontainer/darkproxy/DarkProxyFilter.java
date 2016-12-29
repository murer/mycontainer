package com.googlecode.mycontainer.darkproxy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.mycontainer.util.Util;

public class DarkProxyFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(DarkProxyFilter.class);

	private DarkProxy proxy;

	public void init(FilterConfig cfg) throws ServletException {
		if (proxy == null) {
			throw new RuntimeException("wrong");
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		filter((HttpServletRequest) request, (HttpServletResponse) response);
	}

	public void filter(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String uri = DarkProxyMeta.uri(request);
		if (uri.startsWith("/_darkproxy/")) {
			LOG.info("DarkProxy: {} {}?{}",
					new Object[] { request.getMethod(), request.getRequestURI(), request.getQueryString() });
			DarkProxyMeta.filter(proxy, request, response);
			return;
		}

		DarkProxyRequest req = DarkProxyRequest.parse(request, proxy.getDest());
		proxy.register(req);
		LOG.info("Request: {} {} {}", new Object[] { Long.toHexString(req.getId()), req.getMethod(), req.getUri() });
		req.waitFor(proxy.getTimeout());
		LOG.info("Proxing: {} {} {}", new Object[] { Long.toHexString(req.getId()), req.getMethod(), req.getUri() });
		req.reload(proxy.getDest());
		DarkProxyResponse resp = new DarkProxyResponse();
		resp.setId(req.getId());
		forward(req, resp);
		proxy.register(resp);
		LOG.info("Response: {} {} {}: {}",
				new Object[] { Long.toHexString(req.getId()), req.getMethod(), req.getUri(), resp.getCode() });
		resp.waitFor(proxy.getTimeout());
		resp.reload(proxy.getDest(), request);
		proxy.remove(req.getId());
		LOG.info("Done: {} {} {}: {}",
				new Object[] { Long.toHexString(req.getId()), req.getMethod(), req.getUri(), resp.getCode() });
		resp.writeTo(proxy.getDest(), response);
	}

	private void forward(DarkProxyRequest req, DarkProxyResponse resp) {
		HttpRequestBase hcreq = toHCRequest(req);
		CloseableHttpResponse hcresp = DarkProxyHttp.me().execute(hcreq);
		try {
			fromHCResp(hcresp, resp);
		} finally {
			Util.close(hcresp);
		}
	}

	public void destroy() {
		DarkProxyHttp.destroy();
	}

	public void setProxy(DarkProxy proxy) {
		this.proxy = proxy;
	}

	private void fromHCResp(CloseableHttpResponse conn, DarkProxyResponse ret) {
		InputStream in = null;
		try {
			ret.setCode(conn.getStatusLine().getStatusCode());
			ret.setReason(conn.getStatusLine().getReasonPhrase());
			fromHCRespHeaders(conn, ret);
			HttpEntity entity = conn.getEntity();
			if (entity != null) {
				in = entity.getContent();
				ret.writeBody(proxy.getDest(), in);
			}
			ret.writeMeta(proxy.getDest());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			Util.close(in);
		}
	}

	public String createUrl(DarkProxyRequest req) {
		return String.format("%s://%s:%s%s?%s", req.getSchema(), req.getHost(), req.getPort(), req.getUri(),
				req.getQuery());
	}

	private void fromHCRespHeaders(CloseableHttpResponse conn, DarkProxyResponse ret) {
		Header[] headers = conn.getAllHeaders();
		for (Header header : headers) {
			ret.getHeaders().add(header.getName(), header.getValue());
		}
	}

	private HttpRequestBase toHCRequest(DarkProxyRequest req) {
		try {
			HttpRequestBase ret = createHCRequest(req.getMethod());
			String url = createUrl(req);
			ret.setURI(new URI(url));
			toHCRequestHeaders(req, ret);
			if (ret instanceof HttpEntityEnclosingRequestBase) {
				toHCRequestBody(req, (HttpEntityEnclosingRequestBase) ret);
			}
			return ret;
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private void toHCRequestHeaders(DarkProxyRequest req, HttpRequestBase ret) {
		for (Entry<String, List<String>> entry : req.getHeaders().getHeaders().entrySet()) {
			if ("Content-Length".equals(entry.getKey())) {
				continue;
			}
			if ("Content-Type".equals(entry.getKey())) {
				continue;
			}
			for (String value : entry.getValue()) {
				ret.addHeader(entry.getKey(), value);
			}
		}
	}

	private void toHCRequestBody(DarkProxyRequest req, HttpEntityEnclosingRequestBase ret) {
		File file = req.getBodyFile(proxy.getDest());
		FileEntity entity = new FileEntity(file);
		String contentType = req.getHeaders().first("Content-Type");
		if (contentType != null) {
			ContentType ct = ContentType.parse(contentType);
			String charset = ct.getParameter("charset");
			contentType = ct.getMimeType();
			if (charset != null) {
				entity.setContentEncoding(charset);
				contentType = new StringBuilder().append(contentType).append("; charset=").append(charset).toString();
			}
			entity.setContentType(contentType);
		}
		ret.setEntity(entity);
	}

	public HttpRequestBase createHCRequest(String method) {
		if ("GET".equals(method)) {
			return new HttpGet();
		} else if ("HEAD".equals(method)) {
			return new HttpHead();
		} else if ("DELETE".equals(method)) {
			return new HttpDelete();
		} else if ("PUT".equals(method)) {
			return new HttpPut();
		} else if ("POST".equals(method)) {
			return new HttpPost();
		}
		throw new RuntimeException("unknown: " + method);
	}

}

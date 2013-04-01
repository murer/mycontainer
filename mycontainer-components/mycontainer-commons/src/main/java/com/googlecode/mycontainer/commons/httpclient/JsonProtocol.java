package com.googlecode.mycontainer.commons.httpclient;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.mycontainer.commons.json.JsonHandler;

public class JsonProtocol implements Serializable {

	private static final long serialVersionUID = 6187472539919353191L;

	private String json;

	private String callback;

	public Object parse() {
		return JsonHandler.instance().parse(json);
	}

	public <T> T parse(Class<T> clazz) {
		return JsonHandler.instance().parse(json, clazz);
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public String formatJson() {
		Object obj = parse();
		return JsonHandler.instance().format(obj);
	}

	public String formatJson(Class<?> clazz) {
		Object obj = parse(clazz);
		return JsonHandler.instance().format(obj);
	}

	public void parse(String content) {
		content = content.trim();
		content = content.split(";$")[0].trim();
		Pattern pattern = Pattern.compile("((^.*)(\\())(.*)([\\);]$)");
		Matcher matcher = pattern.matcher(content);
		if (matcher.matches()) {
			setCallback(matcher.group(2).trim());
			setJson(matcher.group(4).trim());
		} else {
			setJson(content);
		}
	}

	public void setJson(String json) {
		this.json = json;
	}

	public String getJson() {
		return json;
	}

}

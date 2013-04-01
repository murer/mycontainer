package com.googlecode.mycontainer.commons.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.mycontainer.commons.io.IOUtil;
import com.googlecode.mycontainer.commons.regex.RegexUtil;


public class PropertyConfig {

	private static final Logger LOG = LoggerFactory
			.getLogger(PropertyConfig.class);

	private static final PropertyConfig INSTANCE = new PropertyConfig();

	public static Properties load(URL url, Properties props) {
		if (props == null) {
			props = new Properties();
		}
		props.putAll(load(url));
		return props;
	}

	public static Properties load(URL url) {
		if (url == null) {
			return new Properties();
		}
		InputStream in = null;
		try {
			in = url.openStream();
			Properties ret = new Properties();
			ret.load(in);
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LOG.error("Error closing", e);
				}
			}
		}
	}

	public static PropertyConfig instance() {
		return INSTANCE;
	}

	public Properties getConfig(Class<?> clazz) {
		return getConfig(clazz.getName());
	}

	public Properties getConfig(String region) {
		List<String> matches = RegexUtil.match("(.*)\\.(([^\\.]*)$)", region);
		String pack = matches.get(1);
		String name = matches.get(2);
		name += ".properties";
		String path = pack.replaceAll("\\.", "/") + "/" + name;
		Properties props = new Properties();

		setEnvs(props);

		URL url = getClass().getClassLoader().getResource(path);
		load(url, props);
		url = getClass().getClassLoader().getResource(name);
		load(url, props);

		props.putAll(System.getProperties());

		return props;
	}

	private Properties setEnvs(Properties props) {
		if (props == null) {
			props = new Properties();
		}
		Set<Entry<String, String>> envs = System.getenv().entrySet();
		for (Entry<String, String> entry : envs) {
			props.put("env." + entry.getKey(), entry.getValue());
		}
		return props;
	}

	public static Properties load(byte[] buffer) {
		ByteArrayInputStream in = new ByteArrayInputStream(buffer);
		try {
			return load(in);
		} finally {
			IOUtil.close(in);
		}
	}

	private static Properties load(InputStream in) {
		try {
			Properties properties = new Properties();
			properties.load(in);
			return properties;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

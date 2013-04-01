package com.googlecode.mycontainer.jsfprovider;

import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.mycontainer.cpscanner.ClasspathScanner;
import com.googlecode.mycontainer.kernel.reflect.ReflectUtil;
import com.sun.faces.config.JavaClassScanningAnnotationScanner;

public class MyContainerAnnotationProvider extends JavaClassScanningAnnotationScanner {

	private static final Logger LOG = LoggerFactory.getLogger(MyContainerAnnotationProvider.class);

	public MyContainerAnnotationProvider(ServletContext sc) {
		super(sc);
	}

	@Override
	public Map<Class<? extends Annotation>, Set<Class<?>>> getAnnotatedClasses(Set<URI> urls) {
		try {
			List<URL> resources = ReflectUtil.locationURL("META-INF/faces-config.xml");
			ClassSetListener listener = new ClassSetListener();
			for (URL url : resources) {
				if (url.getProtocol().equals("file")) {
					String urlStr = url.toString();
					LOG.info("Scanning for JSF annotations: " + urlStr);
					String baseStr = urlStr.replaceAll("META-INF/faces-config\\.xml$", "");
					URL base = new URL(baseStr);
					ClasspathScanner scanner = new ClasspathScanner();
					scanner.addListener(listener);
					scanner.scan(base);
				}
			}
			Map<Class<? extends Annotation>, Set<Class<?>>> ret = processClassList(listener.getClasses());
			Map<Class<? extends Annotation>, Set<Class<?>>> more = super.getAnnotatedClasses(urls);
			ret = new HashMap<Class<? extends Annotation>, Set<Class<?>>>(ret);
			merge(ret, more);
			if (LOG.isDebugEnabled()) {
				LOG.debug("JSF Annotations: " + ret);
			}
			return ret;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private void merge(Map<Class<? extends Annotation>, Set<Class<?>>> to, Map<Class<? extends Annotation>, Set<Class<?>>> from) {
		Set<Entry<Class<? extends Annotation>, Set<Class<?>>>> set = from.entrySet();
		for (Entry<Class<? extends Annotation>, Set<Class<?>>> entry : set) {
			Class<? extends Annotation> key = entry.getKey();
			Set<Class<?>> value = entry.getValue();
			Set<Class<?>> toSet = to.get(key);
			if (toSet == null) {
				toSet = new HashSet<Class<?>>();
				to.put(key, toSet);
			}
			toSet.addAll(value);
		}

	}

	public static void main(String[] args) throws Exception {
		System.out.println("jar:file:/home/t_61161/.m2/repository/org/primefaces/primefaces/3.4.2/primefaces-3.4.2.jar!/META-INF/faces-config.xml".replaceAll(
		        "!/META-INF/faces-config.xml$", ""));
		System.out.println(new URL("jar:file:/home/t_61161/.m2/repository/org/primefaces/primefaces/3.4.2/primefaces-3.4.2.jar"));
	}

}

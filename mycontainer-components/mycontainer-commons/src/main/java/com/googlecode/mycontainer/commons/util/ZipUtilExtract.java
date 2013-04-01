package com.googlecode.mycontainer.commons.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.mycontainer.commons.io.IOUtil;

public class ZipUtilExtract implements ZipProcessor {

	private static final Logger LOG = LoggerFactory
			.getLogger(ZipUtilExtract.class);

	private File target;

	private byte[] buffer = new byte[1024 * 1024];

	private int reunzip = 0;

	private final List<String> reunzipMatches = new ArrayList<String>();

	public ZipUtilExtract() {
		reunzipMatches.add("^.*\\.zip$");
		reunzipMatches.add("^.*\\-ejb.*SNAPSHOT\\.jar$");
		reunzipMatches.add("^.*\\-ejb\\.jar$");
		reunzipMatches.add("^.*\\.war$");
		reunzipMatches.add("^.*\\.ear$");
		reunzipMatches.add("^.*\\.rar$");
	}

	public List<String> getReunzipMatches() {
		return reunzipMatches;
	}

	public ZipUtilExtract target(File target) {
		if (!target.exists()) {
			if (!target.mkdirs()) {
				throw new RuntimeException("it can not be created: " + target);
			}
		}
		if (!target.isDirectory()) {
			throw new RuntimeException("it should be a directory: " + target);
		}
		this.target = target;
		return this;
	}

	public void process(ZipEntry entry, ZipInputStream in) {
		String name = entry.getName();
		if (name == null) {
			throw new RuntimeException("name is required: " + entry);
		}

		boolean isDirectory = entry.isDirectory();

		if (isDirectory) {
			unzipDirectory(name);
		} else {
			unzipFile(name, in);
		}
	}

	protected File unzipFile(String name, ZipInputStream in) {
		File f = new File(target, name);
		if (reunzip > 0 && reunzipMatches(name)) {
			int reunzip = this.reunzip - 1;
			LOG.info("Reunzip: " + f + " (" + reunzip + ")");
			ZipUtilExtract.withTarget(f).reunzip(reunzip - 1)
					.unzip(new ZipInputStream(in));
			return f;
		}
		FileOutputStream out = null;
		try {
			f.getParentFile().mkdirs();
			out = new FileOutputStream(f);
			IOUtil.copyAll(in, out, buffer);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtil.close(out);
		}
		return f;
	}

	private ZipUtilExtract unzip(ZipInputStream in) {
		return ZipUtil.process(in, this);
	}

	private boolean reunzipMatches(String name) {
		for (String check : reunzipMatches) {
			if (name.matches(check)) {
				return true;
			}
		}
		return false;
	}

	protected File unzipDirectory(String name) {
		File create = new File(target, name);
		if (create.exists() && create.isDirectory()) {
			return create;
		}
		if (!create.mkdirs()) {
			throw new RuntimeException("it can not be created: " + create);
		}
		return create;
	}

	public static ZipUtilExtract withTarget(File target) {
		return new ZipUtilExtract().target(target);
	}

	public ZipUtilExtract unzip(URL url) {
		LOG.info("Unzip: " + url + " (" + reunzip + ")");
		return ZipUtil.process(url, this);
	}

	public ZipUtilExtract reunzip(int reunzip) {
		this.reunzip = reunzip;
		return this;
	}

	public static void main(String[] args) {
		File zip = new File(args[0]);
		File target = new File(args[1]);
		int reunzip = Integer.parseInt(args[2]);
		boolean doit = "true".equals(args[3]);

		System.out.println("unzip: " + zip);
		System.out.println("to: " + target);
		System.out.println("reunzip: " + reunzip);
		System.out.println("doit: " + doit);
		if (!doit) {
			return;
		}

		ZipUtilExtract.withTarget(target).reunzip(reunzip).unzip(zip);
	}

	private ZipUtilExtract unzip(File zip) {
		try {
			return unzip(zip.toURI().toURL());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

}

package com.googlecode.mycontainer.maven.plugin;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.googlecode.mycontainer.maven.plugin.util.FileCrawler;

/**
 * @goal us-ascii
 * @aggregator
 * @requiresProject false
 */
public class MycontainerUsAsciiMojo extends AbstractMojo {

	private static class Entry {
		private final File file;
		private final String result;
		private final String name;

		public Entry(File file, String result) {
			this.file = file;
			this.result = result;
			this.name = file.getName();
		}

		@Override
		public String toString() {
			return name;
		}

	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		PluginUtil.configureLogger(getLog());

		final List<Entry> entries = new ArrayList<Entry>();

		FileCrawler crawler = new FileCrawler() {

			@Override
			protected void found(File file) {
				String result = check(file);
				if (result != null) {
					getLog().error("non us-ascii file found: " + file + " (" + result + ")");
					entries.add(new Entry(file, result));
				}
			}
		};

		crawler.crawl(new File("."));
		if (!entries.isEmpty()) {
			throw new MojoFailureException("non us-ascii files found: " + entries.size());
		}
	}

	private String check(File file) {
		if (file.isDirectory()) {
			return null;
		}

		InputStream in = null;
		try {
			in = new FileInputStream(file);
			return check(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			close(in);
		}
	}

	private String check(InputStream in) throws IOException {
		byte[] buffer = new byte[1024 * 100];
		while (true) {
			int read = in.read(buffer);
			if (read < 0) {
				return null;
			}
			String ret = check(buffer, 0, read);
			if (ret != null) {
				return ret;
			}
		}
	}

	private String check(byte[] buffer, int offset, int limit) {
		for (int i = 0; i < limit; i++) {
			byte b = buffer[offset + i];
			int num = 0xFF & b;
			if (num != 9 && num != 10 && num != 13 && (num < 20 || num > 127)) {
				return "non-us-acii, byte: " + " 0x" + hex(num) + " (" + num + ")";
			}
		}
		return null;
	}

	private String hex(int num) {
		String str = Integer.toHexString(num).toUpperCase();
		if (str.length() == 1) {
			str = "0" + str;
		}
		return str;
	}

	private void close(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (Exception e) {
				getLog().error("error closing", e);
			}
		}
	}

}

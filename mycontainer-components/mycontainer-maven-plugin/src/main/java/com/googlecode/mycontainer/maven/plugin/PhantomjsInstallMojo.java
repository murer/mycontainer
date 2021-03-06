package com.googlecode.mycontainer.maven.plugin;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import de.schlichtherle.truezip.file.TFile;

/**
 * @goal phantomjs-install
 * @aggregator
 * @requiresProject false
 */
public class PhantomjsInstallMojo extends AbstractMojo {

	public static class Spec {
		private String plataform;
		private String arch;
		private String pack;

		public Spec(String plataform, String arch, String pack) {
			this.plataform = plataform;
			this.arch = arch;
			this.pack = pack;
		}

		public String getName() {
			return arch == null ? plataform : "" + plataform + "-" + arch;
		}

	}

	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * @parameter expression="${mycontainer.phantomjs.version}"
	 *            default-value="2.1.1"
	 * @required
	 */
	private String version;

	/**
	 * @parameter expression="${mycontainer.phantomjs.baseUrl}"
	 *            default-value="https://bitbucket.org/ariya/phantomjs/downloads/"
	 * @required
	 */
	private String baseUrl;

	/**
	 * @parameter expression="${mycontainer.phantomjs.dest}"
	 *            default-value="${project.build.directory}/phantomjs"
	 * @required
	 */
	private File dest;

	private Map<String, Spec> specs = new HashMap<String, Spec>();

	public PhantomjsInstallMojo() {
		addSpec("linux", "i686", "tar.bz2");
		addSpec("linux", "x86_64", "tar.bz2");
		addSpec("macosx", null, "zip");
		addSpec("windows", null, "zip");
	}

	private void addSpec(String plataform, String arch, String pack) {
		Spec spec = new Spec(plataform, arch, pack);
		String name = spec.getName();
		specs.put(name, spec);
	}

	public void execute() throws MojoExecutionException {

		if (!dest.exists() && !dest.mkdirs()) {
			throw new MojoExecutionException("unable to create directory: " + dest);
		}

		File file = getFile();
		if (!file.exists()) {
			Spec spec = getSpec();
			download(spec);
			unpack(spec);
			file = getFile();
		}
		if (!file.exists()) {
			throw new MojoExecutionException("phantomjs was not installed");
		}
		getLog().info("Phantomjs: " + file);
		project.getProperties().put("mycontainer.phatomjs.executable", file.getAbsolutePath());
	}

	private void unpack(Spec spec) throws MojoExecutionException {
		try {
			TFile archive = getTFile(spec);
			String executable = archive.getName();
			File target = new File(dest, executable);
			getLog().info("Unpacking " + archive);
			archive.cp(target);
			target.setExecutable(true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private TFile getTFile(Spec spec) throws MojoExecutionException {
		File packFile = new File(dest, "phantomjs." + spec.pack);
		TFile archive = new TFile(packFile, "phantomjs-" + version + "-" + spec.getName() + "/bin/phantomjs");
		if (!archive.exists()) {
			archive = new TFile(packFile, "phantomjs-" + version + "-" + spec.getName() + "/bin/phantomjs.exe");
			if (!archive.exists()) {
				archive = new TFile(packFile, "phantomjs-" + version + "-" + spec.getName() + "/phantomjs.exe");
				if (!archive.exists()) {
					archive = new TFile(packFile, "phantomjs-" + version + "-" + spec.getName() + "/phantomjs");
					if (!archive.exists()) {
						throw new MojoExecutionException("phantomjs executable not found: " + packFile);
					}
				}
			}
		}
		return archive;
	}

	@SuppressWarnings("resource")
	private void download(Spec spec) {
		String url = "" + baseUrl + "phantomjs-" + version + "-" + spec.getName() + "." + spec.pack;
		File packFile = new File(dest, "phantomjs." + spec.pack);
		FileChannel out = null;
		try {
			getLog().info("Downloading: " + url);
			out = new FileOutputStream(packFile).getChannel();
			readUrl(url, out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			close(out);
		}
	}

	private void readUrl(String url, FileChannel out) throws IOException {
		HttpURLConnection yc = null;
		ReadableByteChannel channel = null;
		try {
			URL dest = new URL(url);
			yc = (HttpURLConnection) dest.openConnection();
			yc.setInstanceFollowRedirects(false);
			yc.setUseCaches(false);
			int responseCode = yc.getResponseCode();
			if (responseCode >= 300 && responseCode < 400) {
				url = yc.getHeaderField("Location");
				getLog().info("Following: " + url);
				readUrl(url, out);
				return;
			}
			InputStream inputStream = yc.getInputStream();
			channel = Channels.newChannel(inputStream);
			out.transferFrom(channel, 0, Long.MAX_VALUE);
		} finally {
			close(yc);
			close(channel);
		}
	}

	private Spec getSpec() {
		String platform = System.getProperty("os.name").toLowerCase();
		String arch = System.getProperty("os.arch").toLowerCase();
		String name = null;
		if (platform.contains("win")) {
			name = "windows";
		} else if (platform.contains("mac")) {
			name = "macosx";
		} else if (platform.contains("nux")) {
			name = "linux-" + (arch.contains("64") ? "x86_64" : "i686");
		} else {
			throw new IllegalArgumentException("unknown platform: " + platform + " " + arch);
		}
		Spec ret = specs.get(name);
		if (ret == null) {
			throw new IllegalArgumentException("unknown spec: " + name);
		}
		return ret;
	}

	private File getFile() {
		File ret = new File(dest, "phantomjs");
		if (!ret.exists()) {
			ret = new File(dest, "phantomjs.exe");
		}
		return ret;
	}

	private void close(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (Exception e) {
				getLog().error("Error closing", e);
			}
		}
	}

	private void close(HttpURLConnection conn) {
		if (conn != null) {
			try {
				conn.disconnect();
			} catch (Exception e) {
				getLog().error("Error closing", e);
			}
		}
	}
}
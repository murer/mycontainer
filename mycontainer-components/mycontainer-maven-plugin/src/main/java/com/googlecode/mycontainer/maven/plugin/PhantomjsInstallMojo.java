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
		private String path;
		private String executable;

		public Spec(String plataform, String arch, String pack, String path, String executable) {
			this.plataform = plataform;
			this.arch = arch;
			this.pack = pack;
			this.path = path;
			this.executable = executable;
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
	 *            default-value="1.9.2"
	 * @required
	 */
	private String version;

	/**
	 * @parameter expression="${mycontainer.phantomjs.baseUrl}"
	 *            default-value="https://phantomjs.googlecode.com/files/"
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
		addSpec("linux", "i686", "tar.bz2", "bin/phantomjs", "phantomjs");
		addSpec("linux", "x86_64", "tar.bz2", "bin/phantomjs", "phantomjs");
		addSpec("macosx", null, "zip", "bin/phantomjs", "phantomjs");
		addSpec("windows", null, "zip", "phantomjs.exe", "phantomjs.exe");
	}

	private void addSpec(String plataform, String arch, String pack, String path, String executable) {
		Spec spec = new Spec(plataform, arch, pack, path, executable);
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
			executable(spec);
			file = getFile();
		}
		if (!file.exists()) {
			throw new MojoExecutionException("phantomjs was not installed");
		}
		getLog().info("Phantomjs: " + file);
		project.getProperties().put("mycontainer.phatomjs.executable", file.getAbsolutePath());
	}

	private void executable(Spec spec) {
		File executable = new File(dest, spec.executable);
		executable.setExecutable(true);
	}

	private void unpack(Spec spec) {
		try {
			File packFile = new File(dest, "phantomjs." + spec.pack);
			TFile archive = new TFile(packFile, "phantomjs-" + version + "-" + spec.getName() + "/" + spec.path);
			getLog().debug("Unpacking: " + archive);
			archive.cp(new File(dest, spec.executable));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("resource")
	private void download(Spec spec) {
		String url = "" + baseUrl + "phantomjs-" + version + "-" + spec.getName() + "." + spec.pack;
		File packFile = new File(dest, "phantomjs." + spec.pack);
		ReadableByteChannel channel = null;
		FileChannel out = null;
		HttpURLConnection conn = null;
		try {
			getLog().info("Downloading: " + url);
			out = new FileOutputStream(packFile).getChannel();
			conn = (HttpURLConnection) fetchURL(url);
			InputStream inputStream = conn.getInputStream();
			channel = Channels.newChannel(inputStream);
			out.transferFrom(channel, 0, Long.MAX_VALUE);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			close(channel);
			close(out);
			close(conn);
		}
	}
	
	private HttpURLConnection fetchURL( String url ) throws IOException {
	    URL dest = new URL(url);
	    HttpURLConnection yc =  (HttpURLConnection) dest.openConnection();
	    yc.setInstanceFollowRedirects( false );
	    yc.setUseCaches(false);
	    int responseCode = yc.getResponseCode();
	    if ( responseCode >= 300 && responseCode < 400 ) { // brute force check, far too wide
	    	url = yc.getHeaderField( "Location");
	    	getLog().info("Following: " + url);
	        return fetchURL( url );
	    }
	    return yc;
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
		if(conn != null){
			try {
				conn.disconnect();
			} catch (Exception e) {
				getLog().error("Error closing", e);
			}
		}
	}
}
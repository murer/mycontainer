package com.googlecode.mycontainer.maven.plugin;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import com.googlecode.mycontainer.maven.plugin.phantomjs.PhantomJSArchive;
import com.googlecode.mycontainer.maven.plugin.phantomjs.PhantomJSArchiveBuilder;

import de.schlichtherle.truezip.file.TFile;

/**
 * @goal phantomjs-install
 * @aggregator
 * @requiresProject false
 */
public class PhantomjsInstallMojo extends AbstractMojo {

	/**
	 * @parameter expression="${mycontainer.phantomjs.version}"
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

	public void execute() throws MojoExecutionException {

		if (!dest.exists() && !dest.mkdirs()) {
			throw new MojoExecutionException("unable to create directory: "
					+ dest);
		}

		PhantomJSArchive phantomJSFile = new PhantomJSArchiveBuilder(version)
				.build();

		File extractTo = new File(dest, phantomJSFile.getExecutable());
		if (extractTo.exists()) {
			getLog().info("pahntomjs: " + extractTo);
			return;
		}
		StringBuilder url = new StringBuilder();
		url.append(baseUrl);
		url.append(phantomJSFile.getArchiveName());
		FileOutputStream fos = null;
		ReadableByteChannel rbc = null;
		try {
			URL downloadLocation = new URL(url.toString());

			getLog().info("Downloading phantomjs binaries from " + url);
			rbc = Channels.newChannel(downloadLocation.openStream());
			File outputFile = new File(dest, phantomJSFile.getArchiveName());
			fos = new FileOutputStream(outputFile);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			TFile archive = new TFile(dest, phantomJSFile.getPathToExecutable());

			getLog().info(
					"Extracting " + archive.getAbsolutePath() + " to "
							+ extractTo.getAbsolutePath());
			extractTo.getParentFile().mkdirs();
			archive.cp(extractTo);
			extractTo.setExecutable(true);
		} catch (MalformedURLException e) {
			throw new MojoExecutionException(
					"Unable to download phantomjs binary from " + url, e);
		} catch (IOException e) {
			throw new MojoExecutionException(
					"Unable to download phantomjs binary from " + url, e);
		} finally {
			close(rbc);
			close(fos);
		}
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
}
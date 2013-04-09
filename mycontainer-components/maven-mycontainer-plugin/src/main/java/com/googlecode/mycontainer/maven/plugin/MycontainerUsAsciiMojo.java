package com.googlecode.mycontainer.maven.plugin;

import java.io.File;

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

	public void execute() throws MojoExecutionException, MojoFailureException {
		PluginUtil.configureLogger(getLog());

		FileCrawler crawler = new FileCrawler() {

			@Override
			protected void found(File file) {
				getLog().info("f: " + file);
			}
		};

		crawler.crawl(new File("."));
	}

}

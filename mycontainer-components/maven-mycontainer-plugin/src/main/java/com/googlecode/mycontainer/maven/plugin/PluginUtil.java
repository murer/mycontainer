package com.googlecode.mycontainer.maven.plugin;

import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.maven.plugin.logging.Log;

public class PluginUtil {

	public static void configureLogger(Log log) {
		if (Logger.getRootLogger().getAllAppenders().hasMoreElements()) {
			return;
		}
		URL props = PluginUtil.class.getClassLoader().getResource("log4j.properties");
		if (props == null) {
			props = PluginUtil.class.getResource("log4j.properties");
		}
		log.info("Configuring log4j using: " + props);
		PropertyConfigurator.configure(props);
	}

}

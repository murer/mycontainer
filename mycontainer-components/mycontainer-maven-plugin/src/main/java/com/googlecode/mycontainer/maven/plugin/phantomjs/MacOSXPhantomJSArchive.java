package com.googlecode.mycontainer.maven.plugin.phantomjs;

public class MacOSXPhantomJSArchive extends PhantomJSArchive {

        public MacOSXPhantomJSArchive(String version) {
                super(version);
        }

        @Override
        protected String getExtension() {
                return "zip";
        }

        @Override
        protected String getPlatform() {
                return "macosx";
        }

        @Override
        public String getExecutable() {
                return "bin/phantomjs";
        }
}
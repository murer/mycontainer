package com.googlecode.mycontainer.commons.util;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public interface ZipProcessor {

	void process(ZipEntry entry, ZipInputStream in);

}

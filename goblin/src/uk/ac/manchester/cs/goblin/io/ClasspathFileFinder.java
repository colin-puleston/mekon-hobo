package uk.ac.manchester.cs.goblin.io;

import java.io.*;
import java.net.*;

/**
 * @author Colin Puleston
 */
class ClasspathFileFinder {

	static File findFile(String path) {

		URL url = getClassLoader().getResource(path);

		if (url == null) {

			throw new RuntimeException("Cannot find file on classpath: " + path);
		}

		File file = urlToFile(url);

		if (file.isDirectory()) {

			throw new RuntimeException("File-path represents a directory: " + path);
		}

		return file;
	}

	static private File urlToFile(URL url) {

		try {

			return new File(URLDecoder.decode(url.getFile(), "UTF-8"));
		}
		catch (UnsupportedEncodingException e) {

			throw new Error(e);
		}
	}

	static private ClassLoader getClassLoader() {

		return Thread.currentThread().getContextClassLoader();
	}
}
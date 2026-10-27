/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.ac.manchester.cs.mekon_util.config;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Responsible for finding resources (files or directories) with
 * paths specified relative either to a specific base-directory or
 * to some location on the class-path.
 *
 * @author Colin Puleston
 */
public class KConfigResourceFinder {

	/**
	 * Finder for locating files relative to some location on the
	 * class-path.
	 */
	static public final KConfigResourceFinder FILES
							= new KConfigResourceFinder(
									new ClassPathFinder(false));

	/**
	 * Finder for locating directories relative to some location on
	 * the class-path.
	 */
	static public final KConfigResourceFinder DIRS
							= new KConfigResourceFinder(
									new ClassPathFinder(true));

	/**
	 * Finder for locating JAR files locatated on the class-path
	 * that contain specified resources.
	 */
	static public final KConfigResourceFinder JARS
							= new KConfigResourceFinder(
									new ContainerJarFinder());

	static private abstract class Finder {

		abstract File get(String path);

		abstract List<File> getAll(String path);

		abstract File lookFor(String path);
	}

	static private abstract class DirectResourceFinder extends Finder {

		private boolean expectDir;

		DirectResourceFinder(boolean expectDir) {

			this.expectDir = expectDir;
		}

		File get(String path) {

			File file = lookForFile(path);

			if (file == null || !file.exists()) {

				throw new KSystemConfigException("Cannot find resource: " + path);
			}

			if (!requiredResourceType(file)) {

				throw new KSystemConfigException(
								"Resource is not a "
								+ (expectDir ? "directory" : "file")
								+ ": "
								+ file);
			}

			return file;
		}

		List<File> getAll(String path) {

			File file = lookFor(path);

			return file != null ? Collections.singletonList(file) : Collections.emptyList();
		}

		File lookFor(String path) {

			File file = lookForFile(path);

			return file != null && requiredResource(file) ? file : null;
		}

		abstract File lookForFile(String path);

		private boolean requiredResource(File file) {

			return file.exists() && requiredResourceType(file);
		}

		private boolean requiredResourceType(File file) {

			return file.isDirectory() == expectDir;
		}
	}

	static private class BaseDirFinder extends DirectResourceFinder {

		private File baseDir;

		BaseDirFinder(File baseDir, boolean expectDir) {

			super(expectDir);

			this.baseDir = baseDir;
		}

		File lookForFile(String path) {

			return new File(baseDir, path);
		}
	}

	static private class ClassPathFinder extends DirectResourceFinder {

		ClassPathFinder(boolean expectDir) {

			super(expectDir);
		}

		File lookForFile(String path) {

			return URLToFileConverter.convert(getURLOrNull(path));
		}
	}

	static private class ContainerJarFinder extends Finder {

		File get(String path) {

			File file = lookFor(path);

			if (file == null) {

				throw new KSystemConfigException("Cannot find resource: " + path);
			}

			return file;
		}

		List<File> getAll(String path) {

			List<File> files = new ArrayList<File>();

			for (URL containedURL : getAllURLs(path)) {

				files.add(getContainerJar(containedURL));
			}

			return files;
		}

		File lookFor(String path) {

			URL containedURL = getURLOrNull(path);

			return containedURL != null ? getContainerJar(containedURL) : null;
		}

		private File getContainerJar(URL containedURL) {

			try {

				JarURLConnection con = (JarURLConnection)containedURL.openConnection();

				return new File(con.getJarFileURL().getFile());
			}
			catch (IOException e) {

				throw new Error(e);
			}
		}
	}

	static private URL getURLOrNull(String path) {

		return getClassLoader().getResource(path);
	}

	static private List<URL> getAllURLs(String path) {

		try {

			return Collections.list(getClassLoader().getResources(path));
		}
		catch (IOException e) {

			throw new Error(e);
		}
	}

	static private ClassLoader getClassLoader() {

		return Thread.currentThread().getContextClassLoader();
	}

	private Finder finder;

	/**
	 * Constructs finder for locating resources with paths relative
	 * to the current directory.
	 *
	 * @param expectDir True if resource should be a directory
	 */
	public KConfigResourceFinder(boolean expectDir) {

		this(new File("."), expectDir);
	}

	/**
	 * Constructs finder for locating resources with paths relative
	 * to the specified base-directory.
	 *
	 * @param baseDir Base-directory for required resources
	 * @param expectDir True if resource should be a directory
	 */
	public KConfigResourceFinder(File baseDir, boolean expectDir) {

		this(new BaseDirFinder(baseDir, expectDir));
	}

	/**
	 * Tests whether the specified resource can be located and is
	 * of the correct type.
	 *
	 * @param path Path to required resource
	 * @return True if resource of correct type can be located
	 */
	public boolean resourceExists(String path) {

		return finder.lookFor(path) != null;
	}

	/**
	 * Provides the specified resource.
	 *
	 * @param path Path to required resource
	 * @return Required resource
	 * throws KSystemConfigException if resource cannot be located
	 * or is of the wrong type
	 */
	public File getResource(String path) {

		return finder.get(path);
	}

	/**
	 * Provides the specified resource(s) if any exist and are of
	 * the correct type. If this method is being accessed via the
	 * {@link #JARS} constant then there may be multiple such instances,
	 * otherwise there will always be at most a single instance.
	 *
	 * @param path Path to required resource(s)
	 * @return Required resource(s), or empty enumeration if none of
	 * required type found
	 */
	public List<File> getResources(String path) {

		return finder.getAll(path);
	}

	/**
	 * Provides the specified resource if it exists and is of
	 * the correct type.
	 *
	 * @param path Path to required resource
	 * @return Required resource, or null if not found or of
	 * the wrong type
	 */
	public File lookForResource(String path) {

		return finder.lookFor(path);
	}

	private KConfigResourceFinder(Finder finder) {

		this.finder = finder;
	}
}

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
						= new KConfigResourceFinder(null, false);

	/**
	 * Finder for locating directories relative to some location on
	 * the class-path.
	 */
	static public final KConfigResourceFinder DIRS
						= new KConfigResourceFinder(null, true);

	private FileFinder fileFinder;
	private boolean expectDir;

	private abstract class FileFinder {

		abstract File lookFor(String path);
	}

	private class BaseDirFileFinder extends FileFinder {

		private File baseDir;

		BaseDirFileFinder(File baseDir) {

			this.baseDir = baseDir;
		}

		File lookFor(String path) {

			return new File(baseDir, path);
		}
	}

	private class ClassPathFileFinder extends FileFinder {

		File lookFor(String path) {

			return URLToFileConverter.convert(getURLOrNull(path));
		}

		private URL getURLOrNull(String path) {

			return getClassLoader().getResource(path);
		}

		private ClassLoader getClassLoader() {

			return Thread.currentThread().getContextClassLoader();
		}
	}

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

		fileFinder = createFileFinder(baseDir);

		this.expectDir = expectDir;
	}

	/**
	 * Tests whether the specified resource can be located and is
	 * of the correct type.
	 *
	 * @param path Path to required resource
	 * @return True if resource of correct type can be located
	 */
	public boolean resourceExists(String path) {

		return lookForResource(path) != null;
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

		File file = fileFinder.lookFor(path);

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

	/**
	 * Provides the specified resource if it exists and is of
	 * the correct type.
	 *
	 * @param path Path to required resource
	 * @return Required resource, or null if not found or of
	 * the wrong type
	 */
	public File lookForResource(String path) {

		File file = fileFinder.lookFor(path);

		return file != null && requiredResource(file) ? file : null;
	}

	private FileFinder createFileFinder(File baseDir) {

		return baseDir != null ? new BaseDirFileFinder(baseDir) : new ClassPathFileFinder();
	}

	private boolean requiredResource(File file) {

		return file.exists() && requiredResourceType(file);
	}

	private boolean requiredResourceType(File file) {

		return file.isDirectory() == expectDir;
	}
}

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

package uk.ac.manchester.cs.mekon.util;

import java.io.*;

import uk.ac.manchester.cs.mekon.config.*;

/**
 * XXX
 *
 * @author Colin Puleston
 */
public class KFileStore {

	private File directory = new File(".");

	private String fileNamePrefix;
	private String fileNameSuffix;

	private class Filter implements FileFilter {

		public boolean accept(File file) {

			String name = file.getName();

			return name.startsWith(fileNamePrefix)
					&& name.endsWith(fileNameSuffix);
		}
	}

	/**
	 * XXX
	 */
	public KFileStore(String fileNamePrefix, String fileNameSuffix) {

		this.fileNamePrefix = fileNamePrefix;
		this.fileNameSuffix = fileNameSuffix;
	}

	/**
	 * XXX
	 */
	public void setDirectory(File directory) {

		this.directory = directory;

		if (!directory.exists()) {

			createDirectory();
		}
	}

	/**
	 * XXX
	 */
	public File removeFile(int index) {

		File file = getFile(index);

		removeFile(file);

		return file;
	}

	/**
	 * XXX
	 */
	public void clear() {

		for (File file : getAllFiles()) {

			removeFile(file);
		}
	}

	/**
	 * XXX
	 */
	public File getDirectory() {

		return directory;
	}

	/**
	 * XXX
	 */
	public File getFile(int index) {

		return new File(directory, getFileName(index));
	}

	/**
	 * XXX
	 */
	public File[] getAllFiles() {

		File[] files = directory.listFiles(new Filter());

		if (files == null) {

			throw new KSystemConfigException(
						"Cannot find store directory: "
						+ directory);
		}

		return files;
	}

	/**
	 * XXX
	 */
	public int getIndex(File file) {

		String name = file.getName();

		int start = fileNamePrefix.length();
		int end = name.length() - fileNameSuffix.length();

		return Integer.parseInt(name.substring(start, end));
	}

	private void createDirectory() {

		if (!directory.mkdirs()) {

			throw new KSystemConfigException(
						"Cannot create store directory: "
						+ directory);
		}
	}

	private void removeFile(File file) {

		if (!file.delete()) {

			throw new KSystemConfigException(
						"Cannot delete store file: "
						+ file);
		}
	}

	private String getFileName(int index) {

		return fileNamePrefix + index + fileNameSuffix;
	}
}
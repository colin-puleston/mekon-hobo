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

package uk.ac.manchester.cs.mekon.model;

import java.io.*;

import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.serial.*;

/**
 * @author Colin Puleston
 */
class InstanceFileStore {

	static private final String STORE_FILE_NAME_PREFIX = "MEKON-INSTANCE-";
	static private final String STORE_FILE_NAME_SUFFIX = ".xml";

	private CModel model;
	private File directory = new File(".");

	private class StoreFileFilter implements FileFilter {

		public boolean accept(File file) {

			String name = file.getName();

			return name.startsWith(STORE_FILE_NAME_PREFIX)
					&& name.endsWith(STORE_FILE_NAME_SUFFIX);
		}
	}

	InstanceFileStore(CModel model) {

		this.model = model;
	}

	void setDirectory(File directory) {

		this.directory = directory;

		if (!directory.exists()) {

			directory.mkdirs();
		}
	}

	void loadAll(InstanceLoader loader) {

		for (File file : findAllStoreFiles()) {

			load(loader, file);
		}
	}

	void clear() {

		for (File file : findAllStoreFiles()) {

			file.delete();
		}
	}

	void write(IFrame instance, CIdentity identity, int index) {

		new IInstanceRenderer(getFile(index)).render(instance, identity);
	}

	IFrame read(int index) {

		return createParser(getFile(index)).parseInstance();
	}

	void remove(int index) {

		getFile(index).delete();
	}

	private void load(InstanceLoader loader, File file) {

		IInstanceToNNetworkParser parser = createToNetworkParser(file);

		CIdentity id = parser.parseIdentity();
		NNode instance = parser.parseInstance();

		loader.load(instance, id, extractIndex(file));
	}

	private File[] findAllStoreFiles() {

		return directory.listFiles(new StoreFileFilter());
	}

	private File getFile(int index) {

		return new File(directory, getFileName(index));
	}

	private String getFileName(int index) {

		return STORE_FILE_NAME_PREFIX + index + STORE_FILE_NAME_SUFFIX;
	}

	private int extractIndex(File file) {

		String name = file.getName();

		int start = STORE_FILE_NAME_PREFIX.length();
		int end = name.length() - STORE_FILE_NAME_SUFFIX.length();

		return Integer.parseInt(name.substring(start, end));
	}

	private IInstanceParser createParser(File file) {

		return new IInstanceParser(model, file);
	}

	private IInstanceToNNetworkParser createToNetworkParser(File file) {

		return new IInstanceToNNetworkParser(model, file);
	}
}

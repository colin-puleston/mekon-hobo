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

package uk.ac.manchester.cs.mekon.store.disk;

import java.io.*;

import uk.ac.manchester.cs.mekon_util.*;

/**
 * @author Colin Puleston
 */
class StoreDirectory implements IDiskStoreNames {

	private File directory;

	private KFileStore profiles;
	private KFileStore instances;

	StoreDirectory(File directory) {

		this.directory = directory;

		profiles = createFileStore(PROFILE_FILE_PREFIX);
		instances = createFileStore(INSTANCE_FILE_PREFIX);
	}

	void remove(int index) {

		profiles.removeFile(index);
		instances.removeFile(index);
	}

	void clear() {

		profiles.clear();
		instances.clear();
	}

	boolean contains(int index) {

		return profiles.getFile(index).exists();
	}

	File getDirectory() {

		return directory;
	}

	File getProfileFile(int index) {

		return profiles.getFile(index);
	}

	File getInstanceFile(int index) {

		return instances.getFile(index);
	}

	File[] getAllProfileFiles() {

		return profiles.getAllFiles();
	}

	int getProfileFileIndex(File profileFile) {

		return profiles.getIndex(profileFile);
	}

	private KFileStore createFileStore(String filePrefix) {

		KFileStore fileStore = new KFileStore(filePrefix, STORE_FILE_SUFFIX);

		fileStore.setDirectory(directory);

		return fileStore;
	}
}

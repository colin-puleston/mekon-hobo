/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.mekon.user.storedoctor;

import java.io.*;

import uk.ac.manchester.cs.mekon.store.disk.*;
import uk.ac.manchester.cs.mekon_util.*;

/**
 * @author Colin Puleston
 */
class StoreFiles implements IDiskStoreNames {

	private KFileStore profileFiles;
	private KFileStore instanceFiles;

	StoreFiles(File storeDir) {

		profileFiles = getFileStore(storeDir, PROFILE_FILE_PREFIX);
		instanceFiles = getFileStore(storeDir, INSTANCE_FILE_PREFIX);
	}

	File[] getAllProfileFiles() {

		return profileFiles.getAllFiles();
	}

	String getInstanceName(File profileFile) {

		return IDiskStoreUtil.readInstanceIdentity(profileFile).getLabel();
	}

	File getInstanceFile(File profileFile) {

		return instanceFiles.getFile(profileFiles.getIndex(profileFile));
	}

	private KFileStore getFileStore(File storeDir, String filePrefix) {

		KFileStore store = new KFileStore(filePrefix, STORE_FILE_SUFFIX);

		store.setDirectory(storeDir);

		return store;
	}
}

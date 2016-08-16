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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * @author Colin Puleston
 */
class FileStore {

	static private final String DEFAULT_STORE_DIR_NAME = "mekon-store";
	static private final String PROFILE_FILE_PREFIX = "PROFILE-";
	static private final String INSTANCE_FILE_PREFIX = "INSTANCE-";
	static private final String FILE_SUFFIX = ".xml";

	static File getDefaultNamedDirectory(File parentDir) {

		return new File(parentDir, DEFAULT_STORE_DIR_NAME);
	}

	private IDiskStore iStore;

	private KFileStore profiles = new KFileStore(
										PROFILE_FILE_PREFIX,
										FILE_SUFFIX);

	private KFileStore instances = new KFileStore(
										INSTANCE_FILE_PREFIX,
										FILE_SUFFIX);

	private Serialiser serialiser;

	FileStore(CModel model, IDiskStore iStore, File directory) {

		this.iStore = iStore;

		serialiser = new Serialiser(model);

		if (directory == null) {

			directory = new File(DEFAULT_STORE_DIR_NAME);
		}

		profiles.setDirectory(directory);
		instances.setDirectory(directory);
	}

	void write(IFrame instance, CIdentity identity, int index) {

		CFrame type = instance.getType();
		InstanceProfile profile = new InstanceProfile(identity, type);

		File pFile = profiles.getFile(index);
		File iFile = instances.getFile(index);

		serialiser.renderProfile(profile, pFile);
		serialiser.renderInstance(instance, iFile);
	}

	IFrame read(int index, boolean freeInstance) {

		File iFile = instances.getFile(index);

		return serialiser.parseInstance(iFile, freeInstance);
	}

	CFrame readType(int index) {

		File pFile = profiles.getFile(index);

		return serialiser.parseProfile(pFile).getType();
	}

	void remove(int index) {

		profiles.removeFile(index);
		instances.removeFile(index);
	}

	void clear() {

		profiles.clear();
		instances.clear();
	}

	void reloadAll() {

		for (File pFile : profiles.getAllFiles()) {

			InstanceProfile p = serialiser.parseProfile(pFile);

			iStore.reload(p, profiles.getIndex(pFile));
		}
	}
}

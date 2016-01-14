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

package uk.ac.manchester.cs.mekon.store;

import java.io.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.motor.*;
import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * @author Colin Puleston
 */
class InstanceFileStore {

	static private final String PROFILE_FILE_PREFIX = "PROFILE-";
	static private final String INSTANCE_FILE_PREFIX = "INSTANCE-";
	static private final String FILE_SUFFIX = ".xml";

	private KFileStore profiles = new KFileStore(
										PROFILE_FILE_PREFIX,
										FILE_SUFFIX);

	private KFileStore instances = new KFileStore(
										INSTANCE_FILE_PREFIX,
										FILE_SUFFIX);

	private InstanceSerialiser serialiser;

	InstanceFileStore(CModel model) {

		serialiser = new InstanceSerialiser(model);
	}

	void setDirectory(File directory) {

		profiles.setDirectory(directory);
		instances.setDirectory(directory);
	}

	void loadAll(InstanceLoader loader) {

		for (File profileFile : profiles.getAllFiles()) {

			load(loader, profileFile);
		}
	}

	void write(IFrame instance, CIdentity identity, int index) {

		renderProfile(identity, instance.getType(), index);
		renderInstance(instance, index);
	}

	IFrame read(int index) {

		return parseInstance(index, false);
	}

	void remove(int index) {

		profiles.removeFile(index);
		instances.removeFile(index);
	}

	void clear() {

		profiles.clear();
		instances.clear();
	}

	private void load(InstanceLoader loader, File profileFile) {

		InstanceProfile profile = serialiser.parseProfile(profileFile);

		CIdentity id = profile.getIdentity();
		CFrame type = profile.getType();
		int index = profiles.getIndex(profileFile);

		loader.addToStore(id, index);

		IMatcher matcher = loader.getMatcher(type);

		if (matcher.rebuildOnStartup()) {

			matcher.add(parseInstance(index, true), id);
		}
	}

	private void renderProfile(CIdentity identity, CFrame type, int index) {

		File file = profiles.getFile(index);

		serialiser.renderProfile(new InstanceProfile(identity, type), file);
	}

	private void renderInstance(IFrame instance, int index) {

		File file = instances.getFile(index);

		serialiser.renderInstance(instance, file);
	}

	private IFrame parseInstance(int index, boolean freeInstance) {

		File file = instances.getFile(index);

		return serialiser.parseInstance(file, freeInstance);
	}
}

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
import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class StoreStructure {

	private CModel model;

	private File mainDirectory;

	private Set<String> subDirNames = new HashSet<String>();
	private Map<CFrame, String> rootTypesToSubDirNames = new HashMap<CFrame, String>();

	StoreStructure(CModel model, File mainDirectory) {

		this.model = model;
		this.mainDirectory = mainDirectory;
	}

	void addSubDirectory(String name, Collection<CIdentity> rootTypes) {

		subDirNames.add(name);

		for (CIdentity rootType : rootTypes) {

			rootTypesToSubDirNames.put(getFrame(rootType), name);
		}
	}

	File getMainDirectory() {

		return mainDirectory;
	}

	Set<String> getSubDirectoryNames() {

		return subDirNames;
	}

	String getSubDirectoryNameOrNull(CFrame type) {

		for (CFrame rootType : rootTypesToSubDirNames.keySet()) {

			if (rootType.subsumes(type)) {

				return rootTypesToSubDirNames.get(rootType);
			}
		}

		return null;
	}

	File getSubDirectory(String name) {

		return new File(mainDirectory, name);
	}

	private CFrame getFrame(CIdentity identity) {

		return model.getFrames().get(identity);
	}
}

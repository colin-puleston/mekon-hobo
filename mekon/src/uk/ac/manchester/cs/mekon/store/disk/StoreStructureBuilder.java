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
class StoreStructureBuilder {

	static private File getDefaultNamedMainDirectory(File parentDir) {

		return new File(parentDir, IDiskStoreNames.DEFAULT_STORE_DIR_NAME);
	}

	private File mainDirectory = getDefaultNamedMainDirectory(new File("."));
	private List<SubStore> subStores = new ArrayList<SubStore>();

	private class SubStore {

		private String name;
		private boolean splitByFunction;
		private Collection<CIdentity> rootTypes;

		SubStore(
			String name,
			boolean splitByFunction,
			Collection<CIdentity> rootTypes) {

			this.name = name;
			this.splitByFunction = splitByFunction;
			this.rootTypes = rootTypes;
		}

		void addToStructure(StoreStructure structure) {

			structure.addSubStore(name, splitByFunction, rootTypes);
		}
	}

	void setMainDirectory(File mainDirectory) {

		this.mainDirectory = mainDirectory;
	}

	void setDefaultNamedMainDirectory(File parentDir) {

		mainDirectory = getDefaultNamedMainDirectory(parentDir);
	}

	void addSubStore(
			String name,
			boolean splitByFunction,
			Collection<CIdentity> rootTypes) {

		subStores.add(new SubStore(name, splitByFunction, rootTypes));
	}

	File getMainDirectory() {

		return mainDirectory;
	}

	StoreStructure build(CModel model) {

		StoreStructure structure = new StoreStructure(model, mainDirectory);

		for (SubStore subStore : subStores) {

			subStore.addToStructure(structure);
		}

		return structure;
	}
}

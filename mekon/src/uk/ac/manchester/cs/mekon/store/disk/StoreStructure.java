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

	static final String DEFAULT_STORE_DIR_NAME = "mekon-store";

	static private final String QUERY_SUBDIR_NAME_SUFFIX = "-queries";

	static String getQueriesSubDirName(String subStoreName) {

		return subStoreName + QUERY_SUBDIR_NAME_SUFFIX;
	}

	private CModel model;

	private File mainDirectory;

	private List<SubStore> subStores = new ArrayList<SubStore>();
	private Set<String> subStoreNames = new HashSet<String>();

	private class SubStore {

		private String name;
		private List<CFrame> rootTypes = new ArrayList<CFrame>();
		private boolean splitByFunction;

		SubStore(
			String name,
			boolean splitByFunction,
			Collection<CIdentity> rootTypeIds) {

			this.name = name;
			this.splitByFunction = splitByFunction;

			for (CIdentity rootTypeId : rootTypeIds) {

				rootTypes.add(getFrameType(rootTypeId));
			}

			subStoreNames.add(name);

			if (splitByFunction) {

				subStoreNames.add(getQueriesSubDirName());
			}
		}

		boolean handlesType(CFrame type) {

			for (CFrame rootType : rootTypes) {

				if (rootType.subsumes(type)) {

					return true;
				}
			}

			return false;
		}

		String getSubDirName(IFrameFunction function) {

			return splitByFunction && function.query() ? getQueriesSubDirName() : name;
		}

		private String getQueriesSubDirName() {

			return StoreNames.queriesSubDirName(name);
		}
	}

	StoreStructure(CModel model, File mainDirectory) {

		this.model = model;
		this.mainDirectory = mainDirectory;
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

	Set<String> getSubStoreNames() {

		return subStoreNames;
	}

	String lookForSubStoreName(CFrame type, IFrameFunction function) {

		for (SubStore subStore : subStores) {

			if (subStore.handlesType(type)) {

				return subStore.getSubDirName(function);
			}
		}

		return null;
	}

	File getSubDirectory(String name) {

		return new File(mainDirectory, name);
	}

	private CFrame getFrameType(CIdentity identity) {

		return model.getFrames().get(identity);
	}
}

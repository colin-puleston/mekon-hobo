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

import org.junit.After;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
public class IDiskStoreTest {

	final TestCModel model = new TestCModel();
	final TestCFrames frames = model.cFrames;
	final TestInstances instances = model.createTestInstances();

	private IDiskStore store;
	private File storeDir;

	@After
	public void clearUp() {

		if (store != null) {

			store.clear();
			deleteStructure(storeDir);
		}
	}

	IDiskStore createStore() {

		return createStore(createStructure(new StoreStructureBuilder()));
	}

	IDiskStore createStore(StoreStructure structure) {

		store = new IDiskStore(model.model, structure);
		storeDir = structure.getMainDirectory();

		store.initialisePostRegistration();

		return store;
	}

	StoreStructure createStructure(StoreStructureBuilder builder) {

		return builder.build(model.model);
	}

	private void deleteStructure(File file) {

		if (file.isDirectory()) {

			deleteNestedStructure(file);
		}

		file.delete();
	}

	private void deleteNestedStructure(File dir) {

		for (File file : dir.listFiles()) {

			deleteStructure(file);
		}
	}
}

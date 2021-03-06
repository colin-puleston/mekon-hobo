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

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * @author Colin Puleston
 */
public class IDiskStoreStructureTest implements IDiskStoreNames {

	static private final File TEST_DIR = new File("test-store");
	static private final File DEFAULT_DIR = new File(DEFAULT_STORE_DIR_NAME);
	static private final File DEFAULT_NAMED_DIR = new File(TEST_DIR, DEFAULT_STORE_DIR_NAME);

	static private final String SUBSTORE_A_NAME = "substore-A";
	static private final String SUBSTORE_B_NAME = "substore-B";

	private CModel model;
	private IDiskStore store;

	private File storeDir;

	private StoreStructure structure;
	private StoreStructureBuilder structureBuilder = new StoreStructureBuilder();

	private CFrame typeA;
	private CFrame typeB;

	private class StoreFileCounter {

		int profiles = 0;
		int instances = 0;

		boolean log = false;

		StoreFileCounter(File dir) {

			for (String fileName : dir.list()) {

				if (fileName.startsWith(StoreDirectory.PROFILE_FILE_PREFIX)) {

					profiles++;
				}
				else if (fileName.startsWith(StoreDirectory.INSTANCE_FILE_PREFIX)) {

					instances++;
				}
				else if (fileName.equals(LogFile.FILE_NAME)) {

					log = true;
				}
			}
		}
	}

	@Before
	public void setUp() {

		TestCModel testModel = new TestCModel();
		TestCFrames cFrames = testModel.serverCFrames;

		model = testModel.serverModel;

		typeA = cFrames.create("Type-A");
		typeB = cFrames.create("Type-B");
	}

	@After
	public void clearUp() {

		if (store != null) {

			store.clear();
			deleteStructure(storeDir);
		}

		TEST_DIR.delete();
	}

	@Test
	public void test_defaultStructure() {

		initialiseStore();

		testMainStoreDirectory(DEFAULT_DIR, 4);
	}

	@Test
	public void test_nonDefaultStoreDirectory() {

		structureBuilder.setMainDirectory(TEST_DIR);
		initialiseStore();

		testMainStoreDirectory(TEST_DIR, 4);
	}

	@Test
	public void test_defaultNamedNonDefaultStoreDirectory() {

		structureBuilder.setDefaultNamedMainDirectory(TEST_DIR);
		initialiseStore();

		testMainStoreDirectory(DEFAULT_NAMED_DIR, 4);
	}

	@Test
	public void test_nonSplitSubStoreStructure() {

		createSubStoreStructure(false);
		initialiseStore();

		testNonSplitSubStoreStructure();
	}

	@Test
	public void test_splitSubStoreStructure() {

		createSubStoreStructure(true);
		initialiseStore();

		testSplitSubStoreStructure();
	}

	@Test
	public void test_subStoreStructureUpdates() {

		initialiseStore();

		testMainStoreDirectory(DEFAULT_DIR, 4);

		startNewStructure();
		createSubStoreStructure(false);
		initialiseStore();

		testNonSplitSubStoreStructure();

		startNewStructure();
		createSubStoreStructure(true);
		initialiseStore();

		testSplitSubStoreStructure();

		startNewStructure();
		createSubStoreStructure(false);
		initialiseStore();

		testNonSplitSubStoreStructure();

		startNewStructure();
		initialiseStore();

		testMainStoreDirectory(DEFAULT_DIR, 4);
	}

	protected IStore getStore() {

		return store;
	}

	private void startNewStructure() {

		structureBuilder = new StoreStructureBuilder();
	}

	private void createSubStoreStructure(boolean splitByFunction) {

		addSubStore(SUBSTORE_A_NAME, splitByFunction, typeA);
		addSubStore(SUBSTORE_B_NAME, splitByFunction, typeB);
	}

	private void addSubStore(String name, boolean splitByFunction, CFrame rootType) {

		structureBuilder.addSubStore(name, splitByFunction, getIdAsList(rootType));
	}

	private void initialiseStore() {

		structure = structureBuilder.build(model);
		store = new IDiskStore(model, structure);
		storeDir = structure.getMainDirectory();

		store.initialisePostRegistration();
		store.clear();

		addInstance(typeA, "A-ASSERT", IFrameFunction.ASSERTION);
		addInstance(typeA, "A-QUERY", IFrameFunction.QUERY);
		addInstance(typeB, "B-ASSERT", IFrameFunction.ASSERTION);
		addInstance(typeB, "B-QUERY", IFrameFunction.QUERY);
	}

	private void addInstance(CFrame type, String instanceId, IFrameFunction function) {

		store.add(FramesTestUtils.createIFrame(type, function), new CIdentity(instanceId));
	}

	private void testNonSplitSubStoreStructure() {

		testMainStoreDirectory(DEFAULT_DIR, 0);
		testSubStoreDirectory(SUBSTORE_A_NAME, 2);
		testSubStoreDirectory(SUBSTORE_B_NAME, 2);
	}

	private void testSplitSubStoreStructure() {

		testMainStoreDirectory(DEFAULT_DIR, 0);
		testSubStoreDirectory(SUBSTORE_A_NAME, 1);
		testSubStoreDirectory(SUBSTORE_B_NAME, 1);
		testQueriesSubStoreDirectory(SUBSTORE_A_NAME, 1);
		testQueriesSubStoreDirectory(SUBSTORE_B_NAME, 1);
	}

	private void testMainStoreDirectory(File expectDir, int expectInstances) {

		assertEquals(getCanonicalPath(expectDir), getCanonicalPath(storeDir));
		testStoreFiles(storeDir, expectInstances, true);
	}

	private void testSubStoreDirectory(String subStoreName, int expectInstances) {

		testStoreFiles(structure.getSubDirectory(subStoreName), expectInstances, false);
	}

	private void testQueriesSubStoreDirectory(String subStoreName, int expectInstances) {

		testSubStoreDirectory(IDiskStoreNames.queriesSubDirName(subStoreName), expectInstances);
	}

	private void testStoreFiles(File dir, int expectInstances, boolean expectLog) {

		StoreFileCounter fileCounter = new StoreFileCounter(dir);

		assertEquals(expectInstances, fileCounter.profiles);
		assertEquals(expectInstances, fileCounter.instances);
		assertEquals(fileCounter.log, expectLog);
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

	private String getCanonicalPath(File dir) {

		try {

			return dir.getCanonicalPath();
		}
		catch (IOException e) {

			throw new Error(e);
		}
	}

	private List<CIdentity> getIdAsList(CFrame type) {

		return Collections.singletonList(type.getIdentity());
	}
}

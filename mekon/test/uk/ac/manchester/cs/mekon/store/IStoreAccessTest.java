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

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.motor.*;
import uk.ac.manchester.cs.mekon.test_util.*;

/**
 * @author Colin Puleston
 */
public abstract class IStoreAccessTest {

	static private final CIdentity FIRST_ID = new CIdentity("First");
	static private final CIdentity SECOND_ID = new CIdentity("Second");

	private IStore store;
	private TestInstances instances;

	private IFrame first = null;
	private IFrame second = null;

	@Before
	public void setUp() {

		initialiseAll();
		store.clear();
	}

	@Test
	public void test_storeAndRemove() {

		testStore();
		testRemove();
	}

	@Test
	public void test_storeAndRetrieve() {

		testStore();
		testRetrieve();
	}

	@Test
	public void test_storeReloadAndRetrieve() {

		if (canTestReload()) {

			testStore();
			initialiseAll();
			testRetrieve();
		}
	}

	protected abstract IStore createStore();

	protected abstract TestInstances resolveCurrentTestInstances();

	protected abstract boolean canTestReload();

	private void initialiseAll() {

		store = createStore();
		instances = resolveCurrentTestInstances();
	}

	private void testStore() {

		first = createAndStoreInstance(FIRST_ID);
		second = createAndStoreInstance(SECOND_ID);

		testStoredIds(FIRST_ID, SECOND_ID);
	}

	private void testRemove() {

		testStoredIds(FIRST_ID, SECOND_ID);
		store.remove(FIRST_ID);
		testStoredIds(SECOND_ID);
		store.remove(SECOND_ID);
		testStoredIds();
	}

	private void testRetrieve() {

		testRetrieve(first, FIRST_ID);
		testRetrieve(second, SECOND_ID);

		store.remove(FIRST_ID);
		testStoredIds(SECOND_ID);

		store.remove(SECOND_ID);
		testStoredIds();
	}

	private IFrame createAndStoreInstance(CIdentity id) {

		IFrame instance = createInstance(id);

		store.add(instance, id);

		return instance;
	}

	private IFrame createInstance(CIdentity id) {

		instances.setTypesPrefix(id.getIdentifier() + "Type");

		return instances.getBasic();
	}

	private void testStoredIds(CIdentity... expectedIds) {

		testListContents(store.getAllIdentities(), Arrays.asList(expectedIds));
	}

	private void testRetrieve(IFrame original, CIdentity id) {

		assertTrue(store.contains(id));

		IRegenInstance regen = store.get(id);
		IFrame retrieved = regen.getRootFrame();

		assertTrue(store.contains(id));

		assertEquals(original.getType().getIdentity(), regen.getRootTypeId());
		assertEquals(IRegenStatus.FULLY_VALID, regen.getStatus());

		assertTrue(retrieved.equalsStructure(original));
		assertFalse(retrieved == original);
	}

	private <E>void testListContents(List<? extends E> got, List<? extends E> expected) {

		MekonTestUtils.testListContents(got, expected);
	}
}

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

import java.util.*;

import org.junit.Test;
import static org.junit.Assert.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.motor.*;

/**
 * @author Colin Puleston
 */
public class IDiskStoreAccessTest extends IStoreAccessTest {

	static private final CIdentity FIRST_ID = new CIdentity("First");
	static private final CIdentity SECOND_ID = new CIdentity("Second");

	private TestCModel model;
	private IDiskStore store;

	private IFrame first = null;
	private IFrame second = null;

	private class TestMatcher implements IMatcher {

		final IFrame instance;
		final IFrame query;

		private CFrame type;

		public void initialise(IMatcherConfig config) {
		}

		public boolean handlesType(CFrame type) {

			return type == this.type;
		}

		public void add(IFrame instance, CIdentity identity) {

			assertTrue(instance.equalsStructure(this.instance));
		}

		public void remove(CIdentity identity) {

			assertTrue(identity.equals(type.getIdentity()));
		}

		public IMatches match(IFrame query) {

			assertTrue(query.equalsStructure(this.query));

			return new IUnrankedMatches(getInstanceIdentityAsList());
		}

		public boolean matches(IFrame query, IFrame instance) {

			assertTrue(query.equalsStructure(this.query));
			assertTrue(instance.equalsStructure(this.instance));

			return true;
		}

		public void stop() {
		}

		TestMatcher(String typeName) {

			type = model.serverCFrames.create(typeName);
			instance = type.instantiate();
			query = type.instantiateQuery();

			store.addMatcher(this);
		}

		void addMatcherInstanceToStore() {

			store.add(instance, getInstanceIdentity());
		}

		void removeMatcherInstanceFromStore() {

			store.remove(getInstanceIdentity());
		}

		void checkQueriesToStoreDirectedToMatcher() {

			List<CIdentity> matchIds = getInstanceIdentityAsList();

			assertTrue(store.match(query).getAllMatches().equals(matchIds));
			assertTrue(store.matches(query, instance));
		}

		private List<CIdentity> getInstanceIdentityAsList() {

			return Collections.singletonList(getInstanceIdentity());
		}

		private CIdentity getInstanceIdentity() {

			return type.getIdentity();
		}
	}

	@Test
	public void test_matching() {

		TestMatcher matcherA = new TestMatcher("A");
		TestMatcher matcherB = new TestMatcher("B");

		matcherA.addMatcherInstanceToStore();
		matcherB.addMatcherInstanceToStore();

		matcherA.checkQueriesToStoreDirectedToMatcher();
		matcherB.checkQueriesToStoreDirectedToMatcher();

		matcherA.removeMatcherInstanceFromStore();
		matcherB.removeMatcherInstanceFromStore();
	}

	protected TestCModel createTestModel() {

		model = new TestCModel();

		return model;
	}

	protected IStore createStore() {

		store = new IDiskStore(model.serverModel);

		store.initialisePostRegistration();

		return store;
	}

	protected IStore resetStore() {

		return createStore();
	}
}

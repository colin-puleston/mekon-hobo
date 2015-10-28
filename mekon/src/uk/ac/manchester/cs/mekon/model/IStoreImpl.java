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

package uk.ac.manchester.cs.mekon.model;

import java.io.*;
import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.mechanism.core.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * @author Colin Puleston
 */
class IStoreImpl implements IStore {

	static private class InstanceIndexes extends KIndexes<CIdentity> {

		protected KRuntimeException createException(String message) {

			return new KSystemConfigException(message);
		}
	}

	private ZFreeInstantiator freeInstantiator;

	private InstanceFileStore fileStore;
	private Set<IMatcher> matchers = new HashSet<IMatcher>();

	private List<CIdentity> identities = new ArrayList<CIdentity>();
	private InstanceIndexes indexes = new InstanceIndexes();

	private class FileStoreInstanceLoader extends InstanceLoader {

		void load(IFrame instance, CIdentity identity, int index) {

			identities.add(identity);
			indexes.assignIndex(identity, index);

			checkAddToMatcher(instance, identity);
		}
	}

	public synchronized IFrame add(IFrame instance, CIdentity identity) {

		instance = deriveFreeInstantiation(instance);

		IFrame previous = checkRemove(identity);
		int index = indexes.assignIndex(identity);

		identities.add(identity);

		fileStore.write(instance, identity, index);
		checkAddToMatcher(instance, identity);

		return previous;
	}

	public synchronized boolean remove(CIdentity identity) {

		return checkRemove(identity) != null;
	}

	public synchronized void clear() {

		for (CIdentity identity : getAllIdentities()) {

			remove(identity);
		}
	}

	public synchronized boolean contains(CIdentity identity) {

		return indexes.hasIndex(identity);
	}

	public synchronized IFrame get(CIdentity identity) {

		return fileStore.read(indexes.getIndex(identity));
	}

	public synchronized List<CIdentity> getAllIdentities() {

		return new ArrayList<CIdentity>(identities);
	}

	public synchronized IMatches match(IFrame query) {

		query = deriveFreeInstantiation(query);

		return getMatcher(query).match(query);
	}

	public synchronized boolean matches(IFrame query, IFrame instance) {

		query = deriveFreeInstantiation(query);
		instance = deriveFreeInstantiation(instance);

		IMatcher matcher = getMatcher(query);

		if (matcher != getMatcher(instance)) {

			return false;
		}

		return matcher.matches(query, instance);
	}

	IStoreImpl(CModel model) {

		freeInstantiator = new ZFreeInstantiatorImpl(model);
		fileStore = new InstanceFileStore(model);
	}

	void setStoreDirectory(File storeDirectory) {

		fileStore.setDirectory(storeDirectory);
	}

	void checkLoad() {

		fileStore.loadAll(new FileStoreInstanceLoader());
	}

	void addMatcher(IMatcher matcher) {

		matchers.add(matcher);
	}

	void clearFileStore() {

		fileStore.clear();
	}

	private IFrame checkRemove(CIdentity identity) {

		IFrame removed = null;

		if (indexes.hasIndex(identity)) {

			int index = indexes.freeIndex(identity);

			identities.remove(identity);

			checkRemoveFromMatcher(fileStore.read(index), identity);
			fileStore.remove(index);
		}

		return removed;
	}

	private void checkAddToMatcher(IFrame instance, CIdentity identity) {

		getMatcher(instance).add(instance, identity);
	}

	private void checkRemoveFromMatcher(IFrame instance, CIdentity identity) {

		getMatcher(instance).remove(identity);
	}

	private IMatcher getMatcher(IFrame frame) {

		for (IMatcher matcher : matchers) {

			if (matcher.handlesType(frame.getType())) {

				return matcher;
			}
		}

		return InertIMatcher.get();
	}

	private IFrame deriveFreeInstantiation(IFrame instance) {

		return freeInstantiator.deriveInstantiation(instance);
	}
}

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
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * Represents an instance-store associated with a MEKON Frames
 * Model (FM). Both instances and queries are represented via
 * instance-level frames. The frames representing instances must be
 * of category {@link IFrameCategory#ASSERTION} rather than
 * {@link IFrameCategory#QUERY}, whereas those representing queries
 * can be either, since assertion frames can also be interpreted as
 * queries.
 *
 * @author Colin Puleston
 */
public class IStore {

	private InstanceFileStore fileStore;
	private IFreeInstantiator freeInstantiator;
	private Set<IMatcher> matchers = new HashSet<IMatcher>();

	private List<CIdentity> identities = new ArrayList<CIdentity>();
	private InstanceIndexes indexes = new InstanceIndexes();

	private class InstanceIndexes extends KIndexes<CIdentity> {

		protected KRuntimeException createException(String message) {

			return new KSystemConfigException(message);
		}
	}

	private class FileStoreInstanceLoader extends InstanceLoader {

		void load(IFrame instance, CIdentity identity, int index) {

			identities.add(identity);
			indexes.assignIndex(identity, index);

			checkAddToMatcher(instance, identity);
		}
	}

	/**
	 * Adds an instance to the store, possibly replacing an existing
	 * instance with the same identity.
	 *
	 * @param instance Representation of instance to be stored
	 * @param identity Unique identity for instance
	 * @return Existing instance that was replaced, or null if not
	 * applicable
	 * @throws KAccessException if instance frame is not of category
	 * {@link IFrameCategory#ASSERTION}
	 */
	public synchronized IFrame add(IFrame instance, CIdentity identity) {

		instance = deriveFreeInstantiation(instance);

		IFrame previous = checkRemove(identity);
		int index = indexes.assignIndex(identity);

		identities.add(identity);

		fileStore.write(instance, identity, index);
		checkAddToMatcher(instance, identity);

		return previous;
	}

	/**
	 * Removes an instance from the store.
	 *
	 * @param identity Unique identity of instance
	 * @return True if instance removed, false if instance with
	 * specified identity not present
	 */
	public synchronized boolean remove(CIdentity identity) {

		return checkRemove(identity) != null;
	}

	/**
	 * Removes all instances from the store.
	 */
	public synchronized void clear() {

		for (CIdentity identity : getAllIdentities()) {

			remove(identity);
		}
	}

	/**
	 * Checks whether store contains a particular instance.
	 *
	 * @param identity Unique identity of instance to check for
	 * @return True if store contains required instance
	 */
	public synchronized boolean contains(CIdentity identity) {

		return indexes.hasIndex(identity);
	}

	/**
	 * Retrieves an instance from the store.
	 *
	 * @param identity Unique identity of instance
	 * @return Instance-level frame representing required instance,
	 * or null if instance with specified identity not present
	 */
	public synchronized IFrame get(CIdentity identity) {

		return fileStore.read(indexes.getIndex(identity));
	}

	/**
	 * Provides unique identities of all instances in store,
	 * ordered by the time/date they were added.
	 *
	 * @return Unique identities of all instances, oldest entries
	 * first
	 */
	public synchronized List<CIdentity> getAllIdentities() {

		return new ArrayList<CIdentity>(identities);
	}

	/**
	 * Finds all instances that are matched by the supplied query.
	 *
	 * @param query Representation of query
	 * @return Results of query execution
	 */
	public synchronized IMatches match(IFrame query) {

		query = deriveFreeInstantiation(query);

		return getMatcher(query).match(query);
	}

	/**
	 * Tests whether the supplied instance is matched by the supplied
	 * query.
	 *
	 * @param query Representation of query
	 * @param instance Representation of instance
	 * @return True if instance matched by query
	 */
	public synchronized boolean matches(IFrame query, IFrame instance) {

		query = deriveFreeInstantiation(query);
		instance = deriveFreeInstantiation(instance);

		IMatcher matcher = getMatcher(query);

		if (matcher != getMatcher(instance)) {

			return false;
		}

		return matcher.matches(query, instance);
	}

	IStore(CModel model) {

		fileStore = new InstanceFileStore(model);
		freeInstantiator = new IFreeInstantiatorImpl(model);
	}

	void setStoreDirectory(File storeDirectory) {

		fileStore.setDirectory(storeDirectory);
	}

	void checkLoad() {

		fileStore.loadAll(new FileStoreInstanceLoader());
	}

	void clearFileStore() {

		fileStore.clear();
	}

	void addMatcher(IMatcher matcher) {

		matchers.add(matcher);
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

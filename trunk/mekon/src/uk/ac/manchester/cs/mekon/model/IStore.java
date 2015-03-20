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

import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * Represents an instance-store associated with a MEKON Frames
 * Model (FM). Both instances and queries are represented via
 * instance-level frames. The frames representing instances must
 * be of category {@link IFrameCategory#ASSERTION} rather than
 * {@link IFrameCategory#QUERY}, whereas those representing
 * queries can be either, since assertion frames can also be
 * interpreted as queries.
 *
 * @author Colin Puleston
 */
public class IStore {

	static private final String STORE_FILE_NAME = "mekon-istore.xml";

	private CModel model;
	private File storeDirectory = null;

	private Set<IMatcher> matchers = new HashSet<IMatcher>();

	private List<CIdentity> identities = new ArrayList<CIdentity>();
	private Map<CIdentity, IFrame> instances = new HashMap<CIdentity, IFrame>();
	private Map<CIdentity, String> labels = new HashMap<CIdentity, String>();

	private class IStoreParserLocal extends IStoreParser {

		protected void addInstance(IFrame instance, CIdentity identity) {

			addInternal(instance, identity);
		}

		IStoreParserLocal() {

			super(model);
		}
	}

	/**
	 * Checks whether store contains a particular instance.
	 *
	 * @param identity Unique identity of instance to check for
	 * @return True if store contains required instance
	 */
	public boolean contains(CIdentity identity) {

		return instances.containsKey(identity);
	}

	/**
	 * Adds an instance to the store, possibly replacing an
	 * existing instance with the same identity.
	 *
	 * @param instance Representation of instance to be stored
	 * @param identity Unique identity for instance
	 * @return Existing instance that was replaced, or null if
	 * not applicable
	 * @throws KAccessException if instance frame is not of
	 * category {@link IFrameCategory#ASSERTION}
	 */
	public IFrame add(IFrame instance, CIdentity identity) {

		IFrame previous = checkRemoveInternal(identity);

		addInternal(instance.copy(), identity);
		writeToFile();

		return previous;
	}

	/**
	 * Removes an instance from the store.
	 *
	 * @param identity Unique identity of instance
	 * @return True if instance removed, false if instance with
	 * specified identity not present
	 */
	public boolean remove(CIdentity identity) {

		if (checkRemoveInternal(identity) != null) {

			writeToFile();

			return true;
		}

		return false;
	}

	/**
	 * Removes all instances from the store.
	 */
	public void clear() {

		for (CIdentity identity : identities) {

			remove(identity);
		}
	}

	/**
	 * Retrieves an instance from the store.
	 *
	 * @param identity Unique identity of instance
	 * @return Instance-level frame representing required instance,
	 * or null if instance with specified identity not present
	 */
	public IFrame get(CIdentity identity) {

		return instances.get(identity);
	}

	/**
	 * Provides unique identities of all instances in store,
	 * ordered by the time/date they were added.
	 *
	 * @return Unique identities of all instances, oldest entries
	 * first
	 */
	public List<CIdentity> getAllIdentities() {

		return new ArrayList<CIdentity>(identities);
	}

	/**
	 * Finds all instances that are matched by the supplied query.
	 *
	 * @param query Representation of query
	 * @return Results of query execution
	 */
	public IMatches match(IFrame query) {

		IMatches matches = getMatcher(query).match(query);

		matches.resolveLabels(labels);

		return matches;
	}

	/**
	 * Tests whether the supplied instance is matched by the supplied
	 * query.
	 *
	 * @param query Representation of query
	 * @param instance Representation of instance
	 * @return True if instance matched by query
	 */
	public boolean matches(IFrame query, IFrame instance) {

		IMatcher matcher = getMatcher(query);

		return matcher == getMatcher(instance)
				&& matcher.matches(query, instance);
	}

	IStore(CModel model) {

		this.model = model;
	}

	void setStoreDirectory(File storeDirectory) {

		this.storeDirectory = storeDirectory;
	}

	void checkLoad() {

		File file = getStoreFile();

		if (file.exists()) {

			new IStoreParserLocal().parse(file);
		}
	}

	void checkRemoveStoreFile() {

		File file = getStoreFile();

		if (file.exists()) {

			file.delete();
		}
	}

	void addMatcher(IMatcher matcher) {

		matchers.add(matcher);
	}

	void addInternal(IFrame instance, CIdentity identity) {

		identities.add(identity);
		instances.put(identity, instance);
		labels.put(identity, identity.getLabel());

		checkAddToMatcher(instance, identity);
	}

	private IFrame checkRemoveInternal(CIdentity identity) {

		IFrame removed = instances.remove(identity);

		if (removed != null) {

			identities.remove(identity);
			labels.remove(identity);

			checkRemoveFromMatcher(removed, identity);
		}

		return removed;
	}

	private void checkAddToMatcher(IFrame instance, CIdentity identity) {

		getMatcher(instance).add(instance, identity);
	}

	private void checkRemoveFromMatcher(IFrame instance, CIdentity identity) {

		getMatcher(instance).remove(identity);
	}

	private IMatcher getMatcher(IFrame iFrame) {

		return getMatcher(iFrame.getType());
	}

	private IMatcher getMatcher(CFrame type) {

		for (IMatcher matcher : matchers) {

			if (matcher.handlesType(type)) {

				return matcher;
			}
		}

		return InertIMatcher.get();
	}

	private void writeToFile() {

		new IStoreRenderer(getStoreFile()).render(this);
	}

	private File getStoreFile() {

		if (storeDirectory == null) {

			return new File(STORE_FILE_NAME);
		}

		return new File(storeDirectory, STORE_FILE_NAME);
	}
}

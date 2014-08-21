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

import java.util.*;

import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.store.*;

/**
 * Represents an instance-store associated with a MEKON Frames
 * Model (FM). Both instances and queries are represented via
 * instance-level frames. The frames representing instances must
 * be of category {@link IFrameCategory#CONCRETE} rather than
 * {@link IFrameCategory#QUERY}, whereas those representing
 * queries can be either, since concrete frames can also be
 * interpreted as queries.
 *
 * @author Colin Puleston
 */
public class IStore {

	private Set<IMatcher> matchers = new HashSet<IMatcher>();

	private List<CIdentity> identities = new ArrayList<CIdentity>();
	private Map<CIdentity, IFrame> instances = new HashMap<CIdentity, IFrame>();

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
	 * if not applicable
	 * @throws KAccessException if instance frame is not of
	 * category {@link IFrameCategory#CONCRETE}
	 */
	public IFrame add(IFrame instance, CIdentity identity) {

		IFrame previous = instances.remove(identity);

		if (previous == null) {

			identities.add(identity);
		}
		else {

			checkRemoveFromMatcher(previous, identity);
		}

		instances.put(identity, instance);
		checkAddToMatcher(instance, identity);

		renderFile();

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

		IFrame instance = instances.remove(identity);

		if (instance != null) {

			identities.remove(identity);
			checkRemoveFromMatcher(instance, identity);

			renderFile();

			return true;
		}

		return false;
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
	 * Finds all instances that match the supplied query.
	 *
	 * @param query Representation of query
	 * @return Unique identities of all matching instances
	 */
	public List<CIdentity> match(IFrame query) {

		IMatcher matcher = lookForMatcher(query);

		if (matcher == null) {

			return Collections.<CIdentity>emptyList();
		}

		return matcher.match(query);
	}

	void addMatcher(IMatcher matcher) {

		matchers.add(matcher);
	}

	private void renderFile() {

		new IStoreRenderer().render(this);
	}

	private void checkAddToMatcher(IFrame instance, CIdentity identity) {

		IMatcher matcher = lookForMatcher(instance);

		if (matcher != null) {

			matcher.add(instance, identity);
		}
	}

	private void checkRemoveFromMatcher(IFrame instance, CIdentity identity) {

		IMatcher matcher = lookForMatcher(instance);

		if (matcher != null) {

			matcher.remove(identity);
		}
	}

	private IMatcher lookForMatcher(IFrame iFrame) {

		return lookForMatcher(iFrame.getType());
	}

	private IMatcher lookForMatcher(CFrame type) {

		for (IMatcher matcher : matchers) {

			if (matcher.handlesType(type)) {

				return matcher;
			}
		}

		return null;
	}
}

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

/**
 * Represents an instance-store associated with a MEKON Frames
 * Model (FM). Both instances and queries are represented via
 * instance-level frames. The frames representing instances must
 * be of concrete-instance rather than query-instance type (see
 * {@link IFrame#queryInstance}), whereas those representing
 * queries can be of either type, since concrete-instances can
 * also be interpreted as queries.
 *
 * @author Colin Puleston
 */
public class IStore {

	private Set<IMatcher> matchers = new HashSet<IMatcher>();
	private Map<CIdentity, IFrame> instances = new HashMap<CIdentity, IFrame>();

	/**
	 * Adds an instance to the store.
	 *
	 * @param instance Representation of instance to be stored
	 * @param identity Unique identity for instance
	 * @return True if instance added, false if instance with
	 * specified identity already present
	 * @throws KAccessException if instance frame represents a
	 * query-instance
	 */
	public boolean add(IFrame instance, CIdentity identity) {

		if (instances.containsKey(identity)) {

			return false;
		}

		instances.put(identity, instance);

		IMatcher matcher = lookForMatcher(instance);

		if (matcher != null) {

			matcher.add(instance, identity);
		}

		return true;
	}

	/**
	 * Removes an instance from the store.
	 *
	 * @param type Type of instance to be removed
	 * @param identity Unique identity of instance
	 * @return True if instance removed, false if instance with
	 * specified identity not present
	 */
	public boolean remove(CFrame type, CIdentity identity) {

		if (instances.remove(identity) == null) {

			return false;
		}

		IMatcher matcher = lookForMatcher(type);

		if (matcher != null) {

			matcher.remove(identity);
		}

		return true;
	}

	/**
	 * Retrieves an instance from the store.
	 *
	 * @param type Type of instance to be retreived
	 * @param identity Unique identity of instance
	 * @return Instance-level frame representing required instance,
	 * or null if instance with specified identity not present
	 */
	public IFrame get(CFrame type, CIdentity identity) {

		return instances.get(identity);
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

	IStore() {
	}

	void addMatcher(IMatcher matcher) {

		matchers.add(matcher);
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

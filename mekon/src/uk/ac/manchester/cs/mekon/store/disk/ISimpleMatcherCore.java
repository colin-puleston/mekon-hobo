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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.motor.*;
import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * Provides the core mechanisms for implementations of the
 * reasoning mechanisms defined by {@link IMatcher} in which the
 * matching is done directly on network representations of
 * queries and instances. The matching acts recursively through
 * the networks, taking into account subsumption relationships
 * between {@link CFrame} representations of the node-types, if
 * such representations are available.
 *
 * @author Colin Puleston
 */
public abstract class ISimpleMatcherCore<I> {

	private Map<CFrame, InstanceGroup> instanceGroups
					= new HashMap<CFrame, InstanceGroup>();

	private class InstanceGroup {

		private CFrame rootFrameType;
		private Map<CIdentity, I> instances = new HashMap<CIdentity, I>();

		InstanceGroup(CFrame rootFrameType) {

			this.rootFrameType = rootFrameType;
		}

		void add(I instance, CIdentity identity) {

			instances.put(identity, instance);
		}

		boolean checkRemove(CIdentity identity) {

			return instances.remove(identity) != null;
		}

		void collectMatches(I query, List<CIdentity> matches) {

			if (getType(query).subsumes(rootFrameType)) {

				for (Map.Entry<CIdentity, I> entry : instances.entrySet()) {

					if (subsumesStructure(query, entry.getValue())) {

						matches.add(entry.getKey());
					}
				}
			}
		}
	}

	/**
	 * Adds the specified instance to the matcher.
	 *
	 * @param instance Instance to be added
	 * @param identity Unique identity for instance
	 */
	public void add(I instance, CIdentity identity) {

		CFrame rootFrameType = getType(instance);
		InstanceGroup group = instanceGroups.get(rootFrameType);

		if (group == null) {

			group = new InstanceGroup(rootFrameType);
			instanceGroups.put(rootFrameType, group);
		}

		group.add(instance, identity);
	}

	/**
	 * Removes the specified instance from the matcher.
	 *
	 * @param identity Unique identity of instance to be removed
	 */
	public void remove(CIdentity identity) {

		for (InstanceGroup group : instanceGroups.values()) {

			if (group.checkRemove(identity)) {

				break;
			}
		}
	}

	/**
	 * Finds all instances that match the specified query.
	 *
	 * @param query Query to be matched
	 * @return Unique identities of all matching instances
	 */
	public IMatches match(I query) {

		List<CIdentity> matches = new ArrayList<CIdentity>();

		for (InstanceGroup group : instanceGroups.values()) {

			group.collectMatches(query, matches);
		}

		return new IUnrankedMatches(matches);
	}

	/**
	 * Tests whether the specified query is matched by the specified
	 * instance.
	 *
	 * @param query Query to be matched
	 * @param instance Instance to test for matching
	 * @return True if query matched by instance
	 */
	public boolean matches(I query, I instance) {

		return subsumesStructure(query, instance);
	}

	/**
	 * Provides the instance, or query, type in the form of a
	 * concept-level frame, if available.
	 *
	 * @param instance Instance or query whose type is required
	 * @return Required type, or null if not available
	 */
	protected abstract CFrame getTypeOrNull(I instance);

	/**
	 * Tests whether the structure of the specified query subsumes
	 * that of the specified instance.
	 *
	 * @param query Query whose structure is to be tested
	 * @param instance Instance whose structure is to be tested
	 * @return True if structure of query subsumes that of instance
	 */
	protected abstract boolean subsumesStructure(I query, I instance);

	private CFrame getType(I instance) {

		CFrame type = getTypeOrNull(instance);

		if (type == null) {

			throw new KSystemConfigException(
						"Cannot handle instance/query that "
						+ "cannot provide a CFrame representation "
						+ "of the root-entity type: " + instance);
		}

		return type;
	}
}

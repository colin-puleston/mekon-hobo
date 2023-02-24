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

package uk.ac.manchester.cs.mekon.network;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.motor.*;
import uk.ac.manchester.cs.mekon.store.disk.*;

import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * Provides an implementation of the reasoning mechanisms defined
 * by {@link IMatcher} in which the matching is done directly on
 * the intermediate node/link network-based representations of
 * queries and instances. The matching acts recursively through the
 * networks, taking into account subsumption relationships between
 * the {@link CFrame} representations of the node-types, where
 * available.
 *
 * @author Colin Puleston
 */
public class NDirectMatcher extends NMatcher {

	private Map<CFrame, InstanceGroup> instanceGroups
					= new HashMap<CFrame, InstanceGroup>();

	private class InstanceGroup {

		private CFrame rootFrameType;
		private Map<CIdentity, NNode> instances = new HashMap<CIdentity, NNode>();

		InstanceGroup(CFrame rootFrameType) {

			this.rootFrameType = rootFrameType;
		}

		void add(NNode instance, CIdentity identity) {

			instances.put(identity, instance);
		}

		boolean checkRemove(CIdentity identity) {

			return instances.remove(identity) != null;
		}

		void collectMatches(NNode query, List<CIdentity> matches) {

			if (getType(query).subsumes(rootFrameType)) {

				for (Map.Entry<CIdentity, NNode> entry : instances.entrySet()) {

					if (matches(query, entry.getValue())) {

						matches.add(entry.getKey());
					}
				}
			}
		}
	}

	/**
	 * Returns true indicating that the matcher handles any type of
	 * instance-level frame. This method should be overriden if
	 * more specific behaviour is required.
	 *
	 * @param type Relevant frame-type
	 * @return True indicating that matcher handles specified type
	 */
	public boolean handlesType(CFrame type) {

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(NNode instance, CIdentity identity) {

		CFrame rootFrameType = getType(instance);
		InstanceGroup group = instanceGroups.get(rootFrameType);

		if (group == null) {

			group = new InstanceGroup(rootFrameType);
			instanceGroups.put(rootFrameType, group);
		}

		group.add(instance, identity);
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove(CIdentity identity) {

		for (InstanceGroup group : instanceGroups.values()) {

			if (group.checkRemove(identity)) {

				break;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IMatches match(NNode query) {

		List<CIdentity> matches = new ArrayList<CIdentity>();

		for (InstanceGroup group : instanceGroups.values()) {

			group.collectMatches(query, matches);
		}

		return new IUnrankedMatches(matches);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean matches(NNode query, NNode instance) {

		return matchesDirect(query, instance);
	}

	/**
	 * Does nothing since no clear-ups are required for this type
	 * of store.
	 */
	public void stop() {
	}

	/**
	 * Specifies that referenced instances are to be expanded.
	 *
	 * @return True since referenced instances are to be expanded
	 */
	protected boolean expandInstanceRefs() {

		return true;
	}

	private CFrame getType(NNode instance) {

		CFrame type = instance.getCFrame();

		if (type == null) {

			throw new KSystemConfigException(
						"Cannot handle instance/query that "
						+ "cannot provide a CFrame representation "
						+ "of the root-entity type: " + instance);
		}

		return type;
	}
}

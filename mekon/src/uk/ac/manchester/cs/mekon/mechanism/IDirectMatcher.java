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

package uk.ac.manchester.cs.mekon.mechanism;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.network.*;

/**
 * Provides an implementation of the reasoning mechanisms defined
 * by {@link IMatcher} in which the matching is done directly on
 * the networks of instance-level frames representing the queries
 * and instances. The matching takes into account subsumption
 * relationships between the value-types.
 *
 * @author Colin Puleston
 */
public class IDirectMatcher extends IMatcher {

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

			if (query.getCFrame().subsumes(rootFrameType)) {

				for (CIdentity identity : instances.keySet()) {

					if (query.subsumes(instances.get(identity))) {

						matches.add(identity);
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean handlesType(CFrame type) {

		return true;
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
	public void addPreProcessed(NNode instance, CIdentity identity) {

		CFrame rootFrameType = instance.getCFrame();
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
	public IMatches matchPreProcessed(NNode query) {

		List<CIdentity> matches = new ArrayList<CIdentity>();

		for (InstanceGroup group : instanceGroups.values()) {

			group.collectMatches(query, matches);
		}

		return new IMatches(matches);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean matchesPreProcessed(NNode query, NNode instance) {

		return query.subsumes(instance);
	}
}

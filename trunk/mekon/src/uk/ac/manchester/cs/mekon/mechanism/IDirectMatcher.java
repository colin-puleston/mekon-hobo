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

/**
 * Provides an implementation of the reasoning mechanisms defined
 * by {@link IMatcher} in which the matching is done directly on
 * the networks of instance-level frames representing the queries
 * and instances. The matching takes into account subsumption
 * relationships between the value-types.
 *
 * @author Colin Puleston
 */
public class IDirectMatcher implements IMatcher {

	private Map<CIdentity, IFrame> instances = new HashMap<CIdentity, IFrame>();

	private class MatchTester {

		private Set<IFrame> visited = new HashSet<IFrame>();

		boolean matches(IFrame query, IFrame instance) {

			return !visited.add(query) || frameMatches(query, instance);
		}

		private boolean frameMatches(IFrame query, IFrame instance) {

			return typeMatches(query, instance) && slotsMatch(query, instance);
		}

		private boolean slotsMatch(IFrame query, IFrame instance) {

			ISlots iSlots = instance.getSlots();

			for (ISlot qSlot : query.getSlots().asList()) {

				ISlot iSlot = iSlots.getOrNull(qSlot.getType().getIdentity());

				if (iSlot == null || !slotValuesMatch(qSlot, iSlot)) {

					return false;
				}
			}

			return true;
		}

		private boolean slotValuesMatch(ISlot qSlot, ISlot iSlot) {

			for (IValue qValue : qSlot.getValues().asList()) {

				if (!matchingSlotValue(qValue, iSlot)) {

					return false;
				}
			}

			return true;
		}

		private boolean matchingSlotValue(IValue qValue, ISlot iSlot) {

			for (IValue iValue : iSlot.getValues().asList()) {

				if (valueMatches(qValue, iValue)) {

					return true;
				}
			}

			return false;
		}

		private boolean valueMatches(IValue qValue, IValue iValue) {

			if (qValue instanceof IFrame) {

				return matches((IFrame)qValue, (IFrame)iValue);
			}

			return typeMatches(qValue, iValue);
		}

		private boolean typeMatches(IValue qValue, IValue iValue) {

			return qValue.getType().subsumes(iValue.getType());
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
	public boolean add(IFrame instance, CIdentity identity) {

		if (instances.containsKey(identity)) {

			return false;
		}

		instances.put(identity, instance);

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean remove(CIdentity identity) {

		return instances.remove(identity) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IMatches match(IFrame query) {

		List<CIdentity> matches = new ArrayList<CIdentity>();

		for (CIdentity id : instances.keySet()) {

			if (matches(query, instances.get(id))) {

				matches.add(id);
			}
		}

		return new IMatches(matches);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean matches(IFrame query, IFrame instance) {

		return new MatchTester().matches(query, instance);
	}
}

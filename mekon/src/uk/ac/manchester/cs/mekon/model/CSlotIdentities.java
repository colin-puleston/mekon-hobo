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

/**
 * @author Colin Puleston
 */
class CSlotIdentities {

	private Map<String, IdentifierGroup> identifierGroups = new HashMap<String, IdentifierGroup>();

	private class IdentityGroup {

		final CIdentity slotId;

		private CFrame rootContainer;

		IdentityGroup(CFrame container, CIdentity slotId) {

			this.slotId = slotId;

			rootContainer = container;
		}

		boolean checkUpdate(CFrame container) {

			if (rootContainer.subsumes(container)) {

				return true;
			}

			if (container.subsumes(rootContainer)) {

				rootContainer = container;

				return true;
			}

			return false;
		}
	}

	private class IdentifierGroup {

		private List<IdentityGroup> identityGroups = new ArrayList<IdentityGroup>();

		CIdentity resolveIdentity(CFrame container, CIdentity slotId) {

			return resolveIdentityGroup(container, slotId).slotId;
		}

		private IdentityGroup resolveIdentityGroup(CFrame container, CIdentity slotId) {

			for (IdentityGroup group : identityGroups) {

				if (group.checkUpdate(container)) {

					return group;
				}
			}

			return addIdentityGroup(container, slotId);
		}

		private IdentityGroup addIdentityGroup(CFrame container, CIdentity slotId) {

			IdentityGroup group = new IdentityGroup(container, slotId);

			identityGroups.add(group);

			return group;
		}
	}

	CIdentity resolve(CFrame container, CIdentity slotId) {

		String slotIdfier = slotId.getIdentifier();
		IdentifierGroup identifierGroup = identifierGroups.get(slotIdfier);

		if (identifierGroup == null) {

			identifierGroup = new IdentifierGroup();
			identifierGroups.put(slotIdfier, identifierGroup);
		}

		return identifierGroup.resolveIdentity(container, slotId);
	}
}

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
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.model.zlink.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * @author Colin Puleston
 */
class InstanceRefIntegrityManager {

	private IDiskStore store;
	private IEditor iEditor;

	private KSetMap<CIdentity, CIdentity> referencingIds = new KSetMap<CIdentity, CIdentity>();

	InstanceRefIntegrityManager(IDiskStore store) {

		this.store = store;

		iEditor = ZCModelAccessor.get().getIEditor(store.getModel());
	}

	void onAddedInstance(IFrame instance, CIdentity identity) {

		referencingIds.removeFromAll(identity);

		addInstanceRefs(identity, instance.getAllReferenceIds());
	}

	void onReloadedInstance(CIdentity identity, InstanceProfile profile) {

		addInstanceRefs(identity, profile.getReferenceIds());
	}

	void onRemovedInstance(CIdentity identity) {

		for (CIdentity refingId : referencingIds.getSet(identity)) {

			removeReferenceId(refingId, identity);
		}

		referencingIds.removeAll(identity);
		referencingIds.removeFromAll(identity);
	}

	private void addInstanceRefs(CIdentity identity, List<CIdentity> referenceIds) {

		for (CIdentity refedId : referenceIds) {

			referencingIds.add(refedId, identity);
		}
	}

	private void removeReferenceId(CIdentity refingId, CIdentity refedId) {

		IFrame refingInstance = store.getOrNull(refingId, false);

		if (refingInstance != null) {

			removeAllReferenceId(refingInstance, refedId);
			store.update(refingInstance, refingId);
		}
	}

	private void removeAllReferenceId(IFrame frame, CIdentity refId) {

		for (ISlot slot : frame.getSlots().asList()) {

			if (slot.getValueType() instanceof CFrame) {

				for (IValue value : slot.getValues().asList()) {

					removeAllReferenceId(slot, (IFrame)value, refId);
				}
			}
		}
	}

	private void removeAllReferenceId(ISlot slot, IFrame valueFrame, CIdentity refId) {

		if (valueFrame.getCategory().reference()) {

			if (valueFrame.getReferenceId().equals(refId)) {

				iEditor.getSlotValuesEditor(slot).remove(valueFrame);
			}
		}
		else {

			removeAllReferenceId(valueFrame, refId);
		}
	}
}
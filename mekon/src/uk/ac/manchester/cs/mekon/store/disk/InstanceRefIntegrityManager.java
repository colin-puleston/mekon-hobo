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
import uk.ac.manchester.cs.mekon_util.*;

/**
 * @author Colin Puleston
 */
class InstanceRefIntegrityManager {

	private IDiskStore store;
	private IEditor iEditor;

	private List<CIdentity> currentStoredIds;
	private KSetMap<CIdentity, CIdentity> referencingIds = new KSetMap<CIdentity, CIdentity>();

	private ObsoleteRefValueRemover obsoleteRefValueRemover = new ObsoleteRefValueRemover();

	private abstract class RefValueRemover {

		void removeAllFrom(IFrame frame) {

			for (ISlot slot : frame.getSlots().activesAsList()) {

				if (slot.getValueType() instanceof CFrame) {

					for (IValue value : slot.getValues().asList()) {

						removeAllFrom(slot, (IFrame)value);
					}
				}
			}
		}

		void onRemoved(ISlotValuesEditor valuesEd, IFrame valueFrame) {
		}

		abstract boolean requiresRemoval(CIdentity testId);

		private void removeAllFrom(ISlot slot, IFrame valueFrame) {

			if (valueFrame.getCategory().reference()) {

				if (requiresRemoval(valueFrame.getReferenceId())) {

					ISlotValuesEditor valuesEd = iEditor.getSlotValuesEditor(slot);

					valuesEd.remove(valueFrame);
					onRemoved(valuesEd, valueFrame);
				}
			}
			else {

				removeAllFrom(valueFrame);
			}
		}
	}

	private class StoredRefValueRemover extends RefValueRemover {

		private CIdentity refedId;

		StoredRefValueRemover(CIdentity refedId) {

			this.refedId = refedId;
		}

		void forRefingId(CIdentity refingId) {

			IFrame refingInstance = store.checkRegen(refingId, false);

			if (refingInstance != null) {

				removeAllFrom(refingInstance);

				store.update(refingInstance, refingId);
			}
		}

		boolean requiresRemoval(CIdentity testId) {

			return testId.equals(refedId);
		}
	}

	private class StoredRefValueReplacer extends StoredRefValueRemover {

		private CIdentity newRefedId;

		StoredRefValueReplacer(CIdentity oldRefedId, CIdentity newRefedId) {

			super(oldRefedId);

			this.newRefedId = newRefedId;
		}

		void onRemoved(ISlotValuesEditor valuesEd, IFrame valueFrame) {

			valuesEd.add(valueFrame.getType().instantiateRef(newRefedId));
		}
	}

	private class ObsoleteRefValueRemover extends RefValueRemover {

		boolean requiresRemoval(CIdentity testId) {

			return !currentStoredIds.contains(testId);
		}
	}

	InstanceRefIntegrityManager(IDiskStore store, List<CIdentity> currentStoredIds) {

		this.store = store;
		this.currentStoredIds = currentStoredIds;

		iEditor = ZCModelAccessor.get().getIEditor(store.getModel());
	}

	void onAddingInstance(IFrame instance, CIdentity identity) {

		obsoleteRefValueRemover.removeAllFrom(instance);

		addInstanceRefs(instance, identity);
	}

	void onReplacingInstance(IFrame instance, CIdentity identity) {

		referencingIds.removeFromAll(identity);

		onAddingInstance(instance, identity);
	}

	void onReloadedInstance(CIdentity identity, IInstanceProfile profile) {

		addInstanceRefs(identity, profile.getReferenceIdentites());
	}

	void onRenamedInstance(IFrame instance, CIdentity oldIdentity, CIdentity newIdentity) {

		for (CIdentity refingId : referencingIds.getSet(oldIdentity)) {

			new StoredRefValueReplacer(oldIdentity, newIdentity).forRefingId(refingId);

			referencingIds.add(newIdentity, refingId);
		}

		removeInstanceRefs(oldIdentity);
		addInstanceRefs(instance, newIdentity);
	}

	void onRemovedInstance(CIdentity identity) {

		for (CIdentity refingId : referencingIds.getSet(identity)) {

			new StoredRefValueRemover(identity).forRefingId(refingId);
		}

		removeInstanceRefs(identity);
	}

	private void addInstanceRefs(IFrame instance, CIdentity identity) {

		addInstanceRefs(identity, instance.getAllReferenceIds());
	}

	private void addInstanceRefs(CIdentity refingId, List<CIdentity> referencedIds) {

		for (CIdentity refedId : referencedIds) {

			referencingIds.add(refedId, refingId);
		}
	}

	private void removeInstanceRefs(CIdentity identity) {

		referencingIds.removeAll(identity);
		referencingIds.removeFromAll(identity);
	}
}
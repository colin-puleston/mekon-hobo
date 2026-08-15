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

	private KSetMap<CIdentity, CIdentity> referencingIds = new KSetMap<CIdentity, CIdentity>();

	private class RefValueRemover {

		private CIdentity refedId;

		RefValueRemover(CIdentity refedId) {

			this.refedId = refedId;
		}

		void forRefingId(CIdentity refingId) {

			IFrame refingInstance = store.regenOrNull(refingId, false);

			if (refingInstance != null) {

				removeAllFrom(refingInstance);

				store.update(refingInstance, refingId);
			}
		}

		void onRemoved(ISlotValuesEditor valuesEd, IFrame valueFrame) {
		}

		private void removeAllFrom(IFrame frame) {

			for (ISlot slot : frame.getSlots().activesAsList()) {

				if (slot.getValueType() instanceof CFrame) {

					for (IValue value : slot.getValues().asList()) {

						removeAllFrom(slot, (IFrame)value);
					}
				}
			}
		}

		private void removeAllFrom(ISlot slot, IFrame valueFrame) {

			if (valueFrame.getCategory().reference()) {

				if (valueFrame.getReferenceId().equals(refedId)) {

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

	private class RefValueReplacer extends RefValueRemover {

		private CIdentity newRefedId;

		RefValueReplacer(CIdentity refedId, CIdentity newRefedId) {

			super(refedId);

			this.newRefedId = newRefedId;
		}

		void onRemoved(ISlotValuesEditor valuesEd, IFrame valueFrame) {

			valuesEd.add(valueFrame.getType().instantiateRef(newRefedId));
		}
	}

	InstanceRefIntegrityManager(IDiskStore store) {

		this.store = store;

		iEditor = ZCModelAccessor.get().getIEditor(store.getModel());
	}

	void onAddedInstance(IFrame instance, CIdentity identity) {

		referencingIds.removeFromAll(identity);

		addInstanceRefs(instance, identity);
	}

	void onReloadedInstance(CIdentity identity, IInstanceProfile profile) {

		addInstanceRefs(identity, profile.getReferenceIdentites());
	}

	void onRenamedInstance(IFrame instance, CIdentity identity, CIdentity newIdentity) {

		for (CIdentity refingId : referencingIds.getSet(identity)) {

			new RefValueReplacer(identity, newIdentity).forRefingId(refingId);

			referencingIds.add(newIdentity, refingId);
		}

		removeInstanceRefs(identity);
		addInstanceRefs(instance, newIdentity);
	}

	void onRemovedInstance(CIdentity identity) {

		for (CIdentity refingId : referencingIds.getSet(identity)) {

			new RefValueRemover(identity).forRefingId(refingId);
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
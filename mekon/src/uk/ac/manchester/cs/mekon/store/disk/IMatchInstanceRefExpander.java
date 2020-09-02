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

/**
 * Responsible for editing {@link IFrame}/{@link ISlot}
 * networks representing specific stored instances, by adding
 * expansions of any external instances referenced from within
 * the networks. Such expanded networks are intended, where
 * applicable, to be used in the instance matching process as
 * provided by relevant implementations of {@link IMatcher}.
 * <p>
 * An expansion of a value-frame representing an instance
 * reference will not replace the original value-frame, but will
 * add an additional value-frame to the same slot representing
 * the root of the network representing the expanded instance.
 * <p>
 * Any instance references within the expansions will also be
 * expanded, other than those that will result in an
 * "instance-type cycle", which is defined as occuring if the
 * type the root-frame of a referenced instance either subsumes
 * or is subsumed by the type of the root-frame of any directly
 * or indirectly referencing instances.
 * <p>
 * NOTE: It is assumed that the instances that are to be expanded
 * will be the "free-instance" copies of the originals upon which
 * the matchers will operate (see {@link IFreeCopier}), and hence
 * will always be suitably editable.
 *
 * @author Colin Puleston
 */
public class IMatchInstanceRefExpander {

	private IDiskStore store;
	private Deque<CFrame> expandingTypes = new ArrayDeque<CFrame>();

	/**
	 * Constructor.
	 *
	 * @param store Instance store containing both the instances to
	 * be expanded and any referenced instances
	 */
	public IMatchInstanceRefExpander(IStore store) {

		this.store = toDiskStore(store);
	}

	/**
	 * Performs the recusive reference-expansion process on the
	 * supplied instance.
	 *
	 * @param instance Instance whose references are to be expanded
	 */
	public void expandAll(IFrame instance) {

		expandingTypes.push(instance.getType());
		expandAllFromSlots(instance);
		expandingTypes.pop();
	}

	private IDiskStore toDiskStore(IStore store) {

		if (store instanceof IDiskStore) {

			return (IDiskStore)store;
		}

		return StoreRegister.get(store.getModel());
	}

	private void expandAllFromSlots(IFrame frame) {

		for (ISlot slot : frame.getSlots().asList()) {

			expandAllFromValues(slot);
		}
	}

	private void expandAllFromValues(ISlot slot) {

		if (slot.getValueType() instanceof CFrame) {

			for (IValue value : slot.getValues().asList()) {

				expandAllFromValueFrame(slot, (IFrame)value);
			}
		}
	}

	private void expandAllFromValueFrame(ISlot slot, IFrame value) {

		if (value.getCategory().reference()) {

			checkExpand(slot, value.getReferenceId());
		}
		else {

			expandAllFromSlots(value);
		}
	}

	private void checkExpand(ISlot slot, CIdentity refId) {

		IFrame refed = store.regenOrNull(refId, true);

		if (refed != null && canExpand(refed)) {

			slot.getValuesEditor().add(refed);
			expandAll(refed);
		}
	}

	private boolean canExpand(IFrame refed) {

		return !refed.leadsToCycle() && !refCausesTypeCycle(refed.getType());
	}

	private boolean refCausesTypeCycle(CFrame refType) {

		for (CFrame expandingType : expandingTypes) {

			if (expandingType.subsumes(refType)
				|| refType.subsumes(expandingType)) {

				return true;
			}
		}

		return false;
	}
}

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
import uk.ac.manchester.cs.mekon.model.regen.*;
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
 * "instance-type cycle", which is defined as occuring if the type
 * of a value-frame is equal to that of the root-frame of the
 * initial instance, or is subsumed by the value-type of any slot
 * lying on the path from the initial root-frame to the
 * value-frame in question.
 * <p>
 * NOTE: It is assumed that the instances that are to be exapnded
 * will be the "free-instance" copies of the originals upon which
 * the matchers will operate (see {@link IFreeCopier}), and hence
 * will always be suitably editable.
 *
 * @author Colin Puleston
 */
public class IMatchInstanceRefExpander {

	private IStore store;
	private Deque<CFrame> expandingTypes = new ArrayDeque<CFrame>();

	/**
	 * Constructor.
	 *
	 * @param store Instance store containing both the instances to
	 * be expanded and any referenced instances
	 */
	public IMatchInstanceRefExpander(IStore store) {

		this.store = store;
	}

	/**
	 * Performs the recusive reference-expansion process on the
	 * supplied instance.
	 *
	 * @param instance Instance whose references are to be expanded
	 */
	public void expandAll(IFrame instance) {

		expandAll(instance.getType(), instance);
	}

	private void expandAll(CFrame type, IFrame instance) {

		expandingTypes.push(type);
		expandAllFromSlots(instance);
		expandingTypes.pop();
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

		IFrame refed = getFromStoreOrNull(refId);

		if (refed != null) {

			CFrame refType = (CFrame)slot.getValueType();

			if (canExpand(refType, refed)) {

				slot.getValuesEditor().add(refed);

				expandAll(refType, refed);
			}
		}
	}

	private IFrame getFromStoreOrNull(CIdentity instanceRef) {

		IRegenInstance regen = store.get(instanceRef);

		if (regen != null && regen.getStatus() != IRegenStatus.FULLY_INVALID) {

			return regen.getRootFrame();
		}

		return null;
	}

	private boolean canExpand(CFrame refType, IFrame refed) {

		return !refed.leadsToCycle() && !refCausesTypeCycle(refType);
	}

	private boolean refCausesTypeCycle(CFrame refType) {

		for (CFrame expandingType : expandingTypes) {

			if (expandingType.subsumes(refType)) {

				return true;
			}
		}

		return false;
	}
}

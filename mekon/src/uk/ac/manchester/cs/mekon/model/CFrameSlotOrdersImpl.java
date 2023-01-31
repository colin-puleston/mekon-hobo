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

import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * @author Colin Puleston
 */
class CFrameSlotOrdersImpl implements CFrameSlotOrders {

	private CIdentifieds<CFrame> allFrames;

	private Map<CIdentity, List<CIdentity>> orderedSlotsByFrame
						= new HashMap<CIdentity, List<CIdentity>>();

	public void add(CIdentity frameId, List<CIdentity> orderedSlotIds) {

		orderedSlotsByFrame.put(frameId, orderedSlotIds);
	}

	CFrameSlotOrdersImpl(CIdentifieds<CFrame> allFrames) {

		this.allFrames = allFrames;
	}

	void setAll() {

		for (CIdentity frameId : orderedSlotsByFrame.keySet()) {

			set(frameId);
		}
	}

	private void set(CIdentity frameId) {

		CAtomicFrame frame = findFrame(frameId);

		CSlots slots = frame.getSlots();
		CSlots reorderdSlots = new CSlots();

		for (CIdentity slotId : orderedSlotsByFrame.get(frameId)) {

			reorderdSlots.add(findSlot(frameId, slotId, slots));
		}

		for (CSlot slot : slots.asList()) {

			if (!reorderdSlots.contains(slot)) {

				reorderdSlots.add(slot);
			}
		}

		frame.setReorderedSlots(reorderdSlots);
	}

	private CAtomicFrame findFrame(CIdentity frameId) {

		CFrame frame = allFrames.getOrNull(frameId);

		if (frame != null) {

			return frame.asAtomicFrame();
		}

		throw createNotFoundException("Frame", frameId.getIdentifier());
	}

	private CSlot findSlot(CIdentity frameId, CIdentity slotId, CSlots slots) {

		CSlot slot = slots.getOrNull(slotId);

		if (slot != null) {

			return slot;
		}

		throw createNotFoundException("Frame-slot", createFrameSlotRef(frameId, slotId));
	}

	private String createFrameSlotRef(CIdentity frameId, CIdentity slotId) {

		return frameId.getIdentifier() + "-->" + slotId.getIdentifier();
	}

	private KSystemConfigException createNotFoundException(String desc, String ref) {

		return new KSystemConfigException(
						"CFrame Slot-Order Config Error: "
						+ desc + " not found: "
						+ "\"" + ref + "\"");
	}
}
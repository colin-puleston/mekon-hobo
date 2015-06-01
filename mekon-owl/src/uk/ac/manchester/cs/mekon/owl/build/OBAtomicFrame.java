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

package uk.ac.manchester.cs.mekon.owl.build;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * @author Colin Puleston
 */
class OBAtomicFrame extends OBFrame {

	private OWLEntity sourceEntity;
	private boolean hidden;
	private IReasoner iReasoner;
	private SortedSet<OBAtomicFrame> superFrames = new TreeSet<OBAtomicFrame>();
	private SortedSet<OBAtomicFrame> subFrames = new TreeSet<OBAtomicFrame>();
	private SortedSet<OBSlot> slots = new TreeSet<OBSlot>();
	private CFrame cFrame = null;

	OBAtomicFrame(
		OWLEntity sourceEntity,
		String label,
		boolean hidden,
		IReasoner iReasoner) {

		super(sourceEntity, label);

		this.sourceEntity = sourceEntity;
		this.iReasoner = iReasoner;
		this.hidden = hidden;
	}

	void addSubFrame(OBAtomicFrame subFrame) {

		subFrame.superFrames.add(this);
		subFrames.add(subFrame);
	}

	void addSlot(OBSlot slot) {

		slots.add(slot);
	}

	boolean canBeSlotValueType() {

		return !hidden || !leafFrame();
	}

	boolean canBeFixedSlotValue(
				CValue<?> cValue,
				boolean slotOnExtension,
				boolean valueStructureAllowed) {

		return !hidden
				&& (slotOnExtension || leafFrame())
				&& cValue instanceof MFrame;
	}

	boolean valueStructureAllowedIfSlotValueType() {

		return anySlots(new HashSet<OBAtomicFrame>(), false)
				|| anySlotsViaLinks(new HashSet<OBAtomicFrame>(), true);
	}

	OWLEntity getSourceEntity() {

		return sourceEntity;
	}

	CFrame ensureCStructure(CBuilder builder, OBAnnotations annotations) {

		if (cFrame == null) {

			cFrame = createCFrame(builder);

			ensureCSubStructure(builder, annotations);
			ensureCSlotStructure(builder, annotations);

			annotations.checkAdd(builder, cFrame, sourceEntity);
		}

		return cFrame;
	}

	OBSlot findTopLevelSlot(OBSlot current) {

		for (OBAtomicFrame sup : superFrames) {

			return sup.findTopLevelSlotViaSuper(current);
		}

		return current;
	}

	private CFrame createCFrame(CBuilder builder) {

		CFrame frame = builder.resolveFrame(getIdentity(), hidden);

		if (iReasoner != null) {

			builder.setIReasoner(frame, iReasoner);
		}

		return frame;
	}

	private void ensureCSubStructure(CBuilder builder, OBAnnotations annotations) {

		for (OBAtomicFrame subFrame : subFrames) {

			CFrame cSubFrame = subFrame.ensureCStructure(builder, annotations);

			builder.getFrameEditor(cSubFrame).addSuper(cFrame);
		}
	}

	private void ensureCSlotStructure(
					CBuilder builder,
					OBAnnotations annotations) {

		for (OBSlot slot : slots) {

			OBSlot topSlot = findTopLevelSlot(slot);

			slot.ensureCStructure(builder, cFrame, topSlot, annotations);
		}
	}

	private boolean anySlots(Set<OBAtomicFrame> visited, boolean lookUp) {

		return !slots.isEmpty() || anySlotsViaLinks(visited, lookUp);
	}

	private boolean anySlotsViaLinks(Set<OBAtomicFrame> visited, boolean lookUp) {

		for (OBAtomicFrame linked : lookUp ? superFrames : subFrames) {

			if (visited.add(linked) && linked.anySlots(visited, lookUp)) {

				return true;
			}
		}

		return false;
	}

	private OBSlot findTopLevelSlotViaSuper(OBSlot current) {

		return findTopLevelSlot(checkUpdateTopLevelSlot(current));
	}

	private OBSlot checkUpdateTopLevelSlot(OBSlot current) {

		CIdentity id = current.getIdentity();

		for (OBSlot slot : slots) {

			if (slot.getIdentity().equals(id)) {

				return slot;
			}
		}

		return current;
	}

	private boolean leafFrame() {

		return subFrames.isEmpty();
	}
}

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

package uk.ac.manchester.cs.mekon.owl.sanctions;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * @author Colin Puleston
 */
class OSFrame extends OIdentified {

	private OWLClass concept;
	private boolean hidden;
	private IReasoner iReasoner;
	private SortedSet<OSFrame> superFrames = new TreeSet<OSFrame>();
	private SortedSet<OSFrame> subFrames = new TreeSet<OSFrame>();
	private SortedSet<OSSlot> slots = new TreeSet<OSSlot>();
	private CFrame cFrame = null;

	OSFrame(
		OWLClass concept,
		String label,
		boolean hidden,
		IReasoner iReasoner) {

		super(concept, label);

		this.concept = concept;
		this.iReasoner = iReasoner;
		this.hidden = hidden;
	}

	void addSubFrame(OSFrame subFrame) {

		subFrame.superFrames.add(this);
		subFrames.add(subFrame);
	}

	void addSlot(OSSlot slot) {

		slots.add(slot);
	}

	OWLClass getConcept() {

		return concept;
	}

	boolean hidden() {

		return hidden;
	}

	CFrame ensureCFrame(CBuilder builder, OSEntityAnnotations annotations) {

		if (cFrame == null) {

			cFrame = createCFrame(builder);

			addCSubFrames(builder, annotations);
			addCSlotsAndValues(builder, annotations);

			annotations.addAnnotations(builder, cFrame, concept);
		}

		return cFrame;
	}

	boolean leafFrame() {

		return subFrames.isEmpty();
	}

	boolean slotsInHierarchy() {

		return slotsInHierarchy(new HashSet<OSFrame>());
	}

	private CFrame createCFrame(CBuilder builder) {

		CFrame frame = builder.resolveFrame(getIdentity(), hidden);

		if (iReasoner != null) {

			builder.setIReasoner(frame, iReasoner);
		}

		return frame;
	}

	private void addCSubFrames(
					CBuilder builder,
					OSEntityAnnotations annotations) {

		for (OSFrame subFrame : subFrames) {

			CFrame cSubFrame = subFrame.ensureCFrame(builder, annotations);

			builder.getFrameEditor(cSubFrame).addSuper(cFrame);
		}
	}

	private void addCSlotsAndValues(
					CBuilder builder,
					OSEntityAnnotations annotations) {

		for (OSSlot slot : slots) {

			OSSlot topSlot = findTopLevelSlot(slot);

			slot.checkAddCSlotAndValues(builder, cFrame, topSlot, annotations);
		}
	}

	private boolean slotsInHierarchy(Set<OSFrame> visited) {

		return !slots.isEmpty() || slotsInSubHierarchies(visited);
	}

	private boolean slotsInSubHierarchies(Set<OSFrame> visited) {

		for (OSFrame sub : subFrames) {

			if (!visited.contains(sub)) {

				if (sub.slotsInHierarchy(visited)) {

					return true;
				}

				visited.add(sub);
			}
		}

		return false;
	}

	private OSSlot findTopLevelSlot(OSSlot current) {

		for (OSFrame sup : superFrames) {

			return sup.findTopLevelSlotViaSuper(current);
		}

		return current;
	}

	private OSSlot findTopLevelSlotViaSuper(OSSlot current) {

		return findTopLevelSlot(checkUpdateTopLevelSlot(current));
	}

	private OSSlot checkUpdateTopLevelSlot(OSSlot current) {

		CIdentity id = current.getIdentity();

		for (OSSlot slot : slots) {

			if (slot.getIdentity().equals(id)) {

				return slot;
			}
		}

		return current;
	}
}

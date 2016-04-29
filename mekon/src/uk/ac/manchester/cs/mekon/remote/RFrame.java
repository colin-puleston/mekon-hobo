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

package uk.ac.manchester.cs.mekon.remote;

import java.util.*;

/**
 * Represents a frame in the remote frames-based representation.
 *
 * @author Colin Puleston
 */
public class RFrame {

	private RConceptValue concept;
	private List<RSlot> slots = new ArrayList<RSlot>();

	/**
	 */
	public String toString() {

		return getClass().getSimpleName() + "(" + concept.toInnerString() + ")";
	}

	/**
	 * Provides the concept associated with the frame.
	 *
	 * @return Concept associated with frame
	 */
	public RConceptValue getConcept() {

		return concept;
	}

	/**
	 * Provides all slots associated with the frame.
	 *
	 * @return All slots associated with frame
	 */
	public List<RSlot> getSlots() {

		return new ArrayList<RSlot>(slots);
	}

	RFrame(RConceptValue concept) {

		this.concept = concept;
	}

	RFrameSpec toSpec() {

		RFrameSpec spec = new RFrameSpec();

		spec.setConcept(concept.toSpec());

		for (RSlot slot : slots) {

			spec.addSlot(slot.toSpec());
		}

		return spec;
	}

	void addSlot(RSlot slot) {

		slots.add(slot);
	}
}

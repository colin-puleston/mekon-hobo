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
 * Responsible for creating and serialisation of {@link RFrame}
 * objects. The parameterless constructor and relevant sets of "get"
 * and "set" methods are designed to enable JSON serialisation.
 *
 * @author Colin Puleston
 */
public class RFrameSpec {

	private RConceptSpec type;
	private List<RSlotSpec> slots = new ArrayList<RSlotSpec>();

	/**
	 * Constructor.
	 */
	public RFrameSpec() {
	}

	/**
	 * Sets value of type-spec.
	 *
	 * @param type Value to set
	 */
	public void setType(RConceptSpec type) {

		this.type = type;
	}

	/**
	 * Sets value of slot-specs.
	 *
	 * @param slots Value to set
	 */
	public void setSlots(List<RSlotSpec> slots) {

		this.slots.clear();
		this.slots.addAll(slots);
	}

	/**
	 * Gets value of type-spec.
	 *
	 * @return Relevant value
	 */
	public RConceptSpec getType() {

		return type;
	}

	/**
	 * Gets value of slot-specs.
	 *
	 * @return Relevant value
	 */
	public List<RSlotSpec> getSlots() {

		return new ArrayList<RSlotSpec>(slots);
	}

	void addSlot(RSlotSpec slot) {

		slots.add(slot);
	}

	RFrame create() {

		RFrame frame = new RFrame(type.create());

		for (RSlotSpec slot : slots) {

			frame.addSlot(slot.create());
		}

		return frame;
	}
}

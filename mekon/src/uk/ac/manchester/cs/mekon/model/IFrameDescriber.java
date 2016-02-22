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

/**
 * @author Colin Puleston
 */
class IFrameDescriber {

	static private final String TAB = "  ";
	static private final String RECURSE_LINE = "[RECURSE...]";

	private StringBuilder builder = new StringBuilder();
	private int currentIndent = 0;
	private Deque<IFrame> frameStack = new ArrayDeque<IFrame>();

	IFrameDescriber(IFrame frame) {

		if (hasStructure(frame)) {

			addLine("");
			describeStructured(frame);
		}
		else {

			add(getBasicDescription(frame));
		}
	}

	String describe() {

		return builder.toString();
	}

	private void describeStructured(IFrame frame) {

		addLine(getBasicDescription(frame));
		changeIndent(1);

		if (frameStack.contains(frame)) {

			addLine(RECURSE_LINE);
		}
		else {

			frameStack.push(frame);

			for (ISlot slot : getValuedSlots(frame)) {

				describeStructured(slot);
			}

			frameStack.pop();
		}

		changeIndent(-1);
	}

	private void describeStructured(ISlot slot) {

		addLine(slot.toString());
		changeIndent(1);

		for (IValue value : slot.getValues().asList()) {

			describeStructured(value);
		}

		changeIndent(-1);
	}

	private void describeStructured(IValue value) {

		if (value instanceof IFrame) {

			describeStructured((IFrame)value);
		}
		else {

			addLine(value.toString());
		}
	}

	private String getBasicDescription(IFrame frame) {

		return FEntityDescriber.entityToString(frame, frame.getType());
	}

	private boolean hasStructure(IFrame frame) {

		return !getValuedSlots(frame).isEmpty();
	}

	private List<ISlot> getValuedSlots(IFrame frame) {

		List<ISlot> slots = new ArrayList<ISlot>();

		for (ISlot slot : frame.getSlots().asList()) {

			if (!slot.getValues().isEmpty()) {

				slots.add(slot);
			}
		}

		return slots;
	}

	private void changeIndent(int increment) {

		currentIndent += increment;
	}

	private void addLine(String text) {

		addTabs();
		add(text);
		add("\n");
	}

	private void addTabs() {

		for (int i = 0 ; i < currentIndent ; i++) {

			add(TAB);
		}
	}

	private void add(String text) {

		builder.append(text);
	}
}

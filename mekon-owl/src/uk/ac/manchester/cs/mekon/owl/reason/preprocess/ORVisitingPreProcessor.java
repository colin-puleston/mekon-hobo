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

package uk.ac.manchester.cs.mekon.owl.reason.preprocess;

import java.util.*;

import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.reason.*;
import uk.ac.manchester.cs.mekon.owl.reason.frames.*;

/**
 * Abstract base-class for pre-processers that visit each frame
 * and slot in an intermediate instance representation in turn,
 * via a depth-first traversal starting from the root-frame.
 *
 * @author Colin Puleston
 */
public abstract class ORVisitingPreProcessor implements ORPreProcessor {

	/**
	 * {@inheritDoc}
	 */
	public void process(OModel model, ORFrame rootFrame) {

		visitAll(model, rootFrame, new HashSet<ORFrame>());
	}

	/**
	 * Visitor for frames in intermediate instance representation.
	 *
	 * @param model Relevant model
	 * @param frame Visited frame
	 */
	protected abstract void visit(OModel model, ORFrame frame);

	/**
	 * Visitor for frame--valued slots in intermediate instance
	 * representation.
	 *
	 * @param model Relevant model
	 * @param slot Visited slot
	 */
	protected abstract void visit(OModel model, ORFrameSlot slot);

	/**
	 * Visitor for number-valued slots in intermediate instance
	 * representation.
	 *
	 * @param model Relevant model
	 * @param slot Visited slot
	 */
	protected abstract void visit(OModel model, ORNumberSlot slot);

	private void visitAll(OModel model, ORFrame frame, Set<ORFrame> visited) {

		if (visited.add(frame)) {

			visit(model, frame);

			for (ORFrameSlot slot : frame.getFrameSlots()) {

				visit(model, slot);

				for (ORFrame value : slot.getValues()) {

					visitAll(model, value, visited);
				}
			}

			for (ORNumberSlot slot : frame.getNumberSlots()) {

				visit(model, slot);
			}
		}
	}
}

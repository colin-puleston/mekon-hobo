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

package uk.ac.manchester.cs.mekon.remote.client;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class RootFrameFinder {

	private Set<IFrame> predecessors = new HashSet<IFrame>();

	IFrame findFrom(IFrame start) {

		IFrame root = lookForSimpleRoot(start);

		return root != null ? root : findComplexRoot();
	}

	private IFrame lookForSimpleRoot(IFrame current) {

		if (predecessors.add(current)) {

			List<ISlot> refSlots = current.getReferencingSlots().asList();

			if (refSlots.isEmpty()) {

				return current;
			}

			for (ISlot refSlot : refSlots) {

				IFrame root = lookForSimpleRoot(refSlot.getContainer());

				if (root != null) {

					return root;
				}
			}
		}

		return null;
	}

	private IFrame findComplexRoot() {

		for (IFrame frame : predecessors) {

			if (allPredecessorsReachableFrom(frame)) {

				return frame;
			}
		}

		throw new Error("Should never happen!");
	}

	private boolean allPredecessorsReachableFrom(IFrame target) {

		for (IFrame predecessor : predecessors) {

			if (!reachableFrom(target, predecessor)) {

				return false;
			}
		}

		return true;
	}

	private boolean reachableFrom(IFrame target, IFrame start) {

		return reachableFrom(target, start, new HashSet<IFrame>());
	}

	private boolean reachableFrom(IFrame target, IFrame current, Set<IFrame> visited) {

		if (visited.add(current)) {

			if (current == target) {

				return true;
			}

			for (ISlot refSlot : current.getReferencingSlots().asList()) {

				if (reachableFrom(target, refSlot.getContainer(), visited)) {

					return true;
				}
			}
		}

		return false;
	}
}

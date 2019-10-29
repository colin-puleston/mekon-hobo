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

package uk.ac.manchester.cs.mekon.basex;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.network.*;

/**
 * @author Colin Puleston
 */
class InstanceRefExpansionTracker {

	private Deque<TypeCycleChecker> typeCycleCheckers = new ArrayDeque<TypeCycleChecker>();

	private class TypeCycleChecker {

		private CIdentity rootType;

		TypeCycleChecker(NNode rootNode) {

			this(rootNode.getType());
		}

		TypeCycleChecker(CIdentity rootType) {

			this.rootType = rootType;

			typeCycleCheckers.push(this);
		}

		boolean refCausesCycle(NLink refSource, NNode refedNode) {

			return refedNode.getType().equals(rootType);
		}
	}

	private class SubsumptionTypeCycleChecker extends TypeCycleChecker {

		private CFrame rootFrame;

		SubsumptionTypeCycleChecker(CFrame rootFrame) {

			super(rootFrame.getIdentity());

			this.rootFrame = rootFrame;
		}

		boolean refCausesCycle(NLink refSource, NNode refedNode) {

			CFrame refedFrame = getEffectiveRootFrameOrNull(refSource, refedNode);

			if (refedFrame != null) {

				return rootFrame.subsumes(refedFrame);
			}

			return super.refCausesCycle(refSource, refedNode);
		}
	}

	InstanceRefExpansionTracker(NNode topLevelRootNode) {

		addTypeCycleChecker(topLevelRootNode.getCFrame(), topLevelRootNode);
	}

	boolean startExpansion(NLink refSource, NNode refedNode) {

		if (refedNode.leadsToCycle() || refCausesTypeCycle(refSource, refedNode)) {

			return false;
		}

		addTypeCycleChecker(refSource, refedNode);

		return true;
	}

	void endExpansion() {

		typeCycleCheckers.pop();
	}

	private void addTypeCycleChecker(NLink refSource, NNode refedNode) {

		CFrame refedFrame = getEffectiveRootFrameOrNull(refSource, refedNode);

		addTypeCycleChecker(refedFrame, refedNode);
	}

	private void addTypeCycleChecker(CFrame rootFrame, NNode rootNode) {

		if (rootFrame != null) {

			new SubsumptionTypeCycleChecker(rootFrame);
		}
		else {

			new TypeCycleChecker(rootNode);
		}
	}

	private boolean refCausesTypeCycle(NLink refSource, NNode refedNode) {

		for (TypeCycleChecker checker : typeCycleCheckers) {

			if (checker.refCausesCycle(refSource, refedNode)) {

				return true;
			}
		}

		return false;
	}

	private CFrame getEffectiveRootFrameOrNull(NLink refSource, NNode refedNode) {

		CSlot slot = refSource.getCSlot();

		return slot != null ? (CFrame)slot.getValueType() : refedNode.getCFrame();
	}
}

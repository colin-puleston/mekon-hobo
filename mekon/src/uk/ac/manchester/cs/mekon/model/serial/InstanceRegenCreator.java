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

package uk.ac.manchester.cs.mekon.model.serial;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.regen.*;
import uk.ac.manchester.cs.mekon.model.regen.motor.*;
import uk.ac.manchester.cs.mekon.util.*;

class InstanceRegenCreator {

	private KListMap<ISlot, IValue> prunedValues = new KListMap<ISlot, IValue>();
	private List<List<String>> prunedPathCandidates = new ArrayList<List<String>>();
	private Set<List<String>> slotPaths = new HashSet<List<String>>();

	private abstract class PathProcessor {

		void processAll(IFrame rootFrame) {

			processFromSlots(rootFrame, new ArrayList<String>());
		}

		abstract void processSlot(ISlot slot, List<String> path);

		abstract void processValue(List<String> path);

		private void processFromFrame(IFrame frame, List<String> path) {

			processValue(path);
			processFromSlots(frame, path);
		}

		private void processFromSlots(IFrame frame, List<String> path) {

			for (ISlot slot : frame.getSlots().asList()) {

				processFromSlot(slot, path);
			}
		}

		private void processFromSlot(ISlot slot, List<String> parentPath) {

			List<String> path = extendPathWithId(parentPath, slot.getType());

			processSlot(slot, path);
			processFromValues(slot, path);
		}

		private void processFromValues(ISlot slot, List<String> path) {

			for (IValue value : slot.getValues().asList()) {

				processFromValue(value, path);
			}
		}

		private void processFromValue(IValue value, List<String> parentPath) {

			List<String> path = extendPathWithValue(parentPath, value);

			if (value instanceof IFrame) {

				processFromFrame((IFrame)value, path);
			}
			else {

				processValue(path);
			}
		}
	}

	private class PrunedPathCandidateAdder extends PathProcessor {

		void processSlot(ISlot slot, List<String> path) {

			slotPaths.add(path);
			prunedPathCandidates.add(path);

			for (IValue value : prunedValues.getList(slot)) {

				prunedPathCandidates.add(extendPathWithValue(path, value));
			}
		}

		void processValue(List<String> path) {

			prunedPathCandidates.add(path);
		}
	}

	private class PrunedPathCandidateRemover extends PathProcessor {

		void processSlot(ISlot slot, List<String> path) {

			prunedPathCandidates.remove(path);
		}

		void processValue(List<String> path) {

			prunedPathCandidates.remove(path);
		}
	}

	void addPrunedValue(ISlot slot, IValue value) {

		prunedValues.add(slot, value);
	}

	void processPrePruned(IFrame rootFrame) {

		new PrunedPathCandidateAdder().processAll(rootFrame);
	}

	IRegenInstance createValid(IFrame rootFrame) {

		IRegenInstanceBuilder builder = new IRegenInstanceBuilder();

		addPrunedPaths(rootFrame, builder);

		return builder.createValid(rootFrame);
	}

	IRegenInstance createInvalid(CIdentity rootTypeId) {

		return new IRegenInstanceBuilder().createInvalid(rootTypeId);
	}

	private void addPrunedPaths(IFrame rootFrame, IRegenInstanceBuilder builder) {

		new PrunedPathCandidateRemover().processAll(rootFrame);

		for (List<String> path : prunedPathCandidates) {

			builder.addPrunedPath(path, slotPaths.contains(path));
		}
	}

	private List<String> extendPathWithValue(List<String> path, IValue value) {

		if (value instanceof IFrame) {

			return extendPathWithId(path, ((IFrame)value).getType());
		}

		if (value instanceof CFrame) {

			return extendPathWithId(path, (CFrame)value);
		}

		return extendPathWithString(path, value.toString());
	}

	private List<String> extendPathWithId(List<String> path, CIdentified next) {

		return extendPathWithString(path, toPathString(next.getIdentity()));
	}

	private List<String> extendPathWithString(List<String> path, String value) {

		path = new ArrayList<String>(path);
		path.add(value);

		return path;
	}

	private String toPathString(CIdentity id) {

		String label = id.getLabel();

		return label.length() == 0 ? id.getIdentifier() : label;
	}
}
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
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.motor.*;
import uk.ac.manchester.cs.mekon.util.*;

class InstanceRegenCreator {

	private Map<List<String>, ISlot> slotsByPath = new HashMap<List<String>, ISlot>();
	private Map<List<String>, IValue> valuesByPath = new HashMap<List<String>, IValue>();

	private List<List<String>> prunedPathCandidates = new ArrayList<List<String>>();
	private KListMap<ISlot, IValue> prunedValuesBySlot = new KListMap<ISlot, IValue>();

	private abstract class PathProcessor {

		void processAll(IFrame rootFrame) {

			processFromSlots(rootFrame, new ArrayList<String>());
		}

		abstract void processSlot(ISlot slot, List<String> path);

		abstract void processValue(IValue value, List<String> path);

		private void processFromSlots(IFrame frame, List<String> path) {

			for (ISlot slot : frame.getSlots().asList()) {

				processFromSlot(slot, path);
			}
		}

		private void processFromSlot(ISlot slot, List<String> framePath) {

			List<String> path = extendPathWithId(framePath, slot.getType());

			processSlot(slot, path);
			processFromValues(slot, path);
		}

		private void processFromValues(ISlot slot, List<String> path) {

			for (IValue value : slot.getValues().asList()) {

				processFromValue(slot, value, path);
			}
		}

		private void processFromValue(ISlot slot, IValue value, List<String> slotPath) {

			List<String> path = extendPathWithValue(slotPath, value);

			processValue(value, path);

			if (value instanceof IFrame) {

				processFromSlots((IFrame)value, path);
			}
		}
	}

	private class PrunedPathCandidateAdder extends PathProcessor {

		void processSlot(ISlot slot, List<String> path) {

			slotsByPath.put(path, slot);
			prunedPathCandidates.add(path);

			for (IValue value : prunedValuesBySlot.getList(slot)) {

				processValue(value, extendPathWithValue(path, value));
			}
		}

		void processValue(IValue value, List<String> path) {

			valuesByPath.put(path, value);
			prunedPathCandidates.add(path);
		}
	}

	private class PrunedPathCandidateRemover extends PathProcessor {

		void processSlot(ISlot slot, List<String> path) {

			prunedPathCandidates.remove(path);
		}

		void processValue(IValue value, List<String> path) {

			prunedPathCandidates.remove(path);
		}
	}

	void addPrunedValue(ISlot slot, IValue value) {

		prunedValuesBySlot.add(slot, value);
	}

	void processPrePruned(IFrame rootFrame) {

		new PrunedPathCandidateAdder().processAll(rootFrame);
	}

	IRegenInstance createValid(IFrame rootFrame) {

		IRegenValidInstance regen = new IRegenValidInstance(rootFrame);

		new PrunedPathCandidateRemover().processAll(rootFrame);

		for (List<String> path : prunedPathCandidates) {

			addPrunedPath(regen, path);
		}

		return new IRegenValidInstance(rootFrame);
	}

	IRegenInstance createInvalid(CIdentity rootTypeId) {

		return new IRegenInvalidInstance(rootTypeId);
	}

	private void addPrunedPath(IRegenValidInstance regen, List<String> path) {

		ISlot slot = slotsByPath.get(path);
		IValue value = null;

		if (slot == null) {

			slot = slotsByPath.get(getParentPath(path));
			value = valuesByPath.get(path);
		}

		regen.addPrunedPath(slot, value, path);
	}

	private List<String> getParentPath(List<String> path) {

		return path.subList(0, path.size() - 1);
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
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

class PruningData {

	private List<IPath> prunedPaths = new ArrayList<IPath>();
	private List<IPath> prunedSlotPaths = new ArrayList<IPath>();
	private List<IPath> prunedValuePaths = new ArrayList<IPath>();

	private Set<List<String>> prunedPrunedPathCandidates = new HashSet<List<String>>();

	private abstract class PathProcessor {

		void processAll(IFrame rootFrame) {

			processFromSlots(rootFrame, new ArrayList<String>());
		}

		abstract boolean processSlot(List<String> path);

		abstract boolean processValue(List<String> path);

		private void processFromFrame(IFrame frame, List<String> path) {

			if (processValue(path)) {

				processFromSlots(frame, path);
			}
		}

		private void processFromSlots(IFrame frame, List<String> path) {

			for (ISlot slot : frame.getSlots().asList()) {

				processFromSlot(slot, extendPath(path, slot.getType()));
			}
		}

		private void processFromSlot(ISlot slot, List<String> path) {

			if (processSlot(path)) {

				processFromValues(slot, path);
			}
		}

		private void processFromValues(ISlot slot, List<String> path) {

			for (IValue value : slot.getValues().asList()) {

				processFromValue(value, path);
			}
		}

		private void processFromValue(IValue value, List<String> parentPath) {

			if (value instanceof IFrame) {

				IFrame frame = (IFrame)value;

				processFromFrame(frame, extendPath(parentPath, frame.getType()));
			}
			else if (value instanceof CFrame) {

				processValue(extendPath(parentPath, (CFrame)value));
			}
			else {

				processValue(extendPath(parentPath, value.toString()));
			}
		}

		private List<String> extendPath(List<String> path, CIdentified next) {

			return extendPath(path, toPathValue(next.getIdentity()));
		}

		private List<String> extendPath(List<String> path, String value) {

			path = new ArrayList<String>(path);
			path.add(value);

			return path;
		}

		private String toPathValue(CIdentity id) {

			String label = id.getLabel();

			return label.length() == 0 ? id.getIdentifier() : label;
		}
	}

	private abstract class PrunedPathCandidateProcessor extends PathProcessor {

		boolean processSlot(List<String> path) {

			processCandidate(path);

			return true;
		}

		boolean processValue(List<String> path) {

			processCandidate(path);

			return true;
		}

		abstract void processCandidate(List<String> path);
	}

	private class PrunedPathCandidateAdder extends PrunedPathCandidateProcessor {

		void processCandidate(List<String> path) {

			prunedPrunedPathCandidates.add(path);
		}
	}

	private class PrunedPathCandidateRemover extends PrunedPathCandidateProcessor {

		void processCandidate(List<String> path) {

			prunedPrunedPathCandidates.remove(path);
		}
	}

	private class PrunedPathFinder extends PathProcessor {

		boolean processSlot(List<String> path) {

			return process(true, prunedSlotPaths, path);
		}

		boolean processValue(List<String> path) {

			return process(false, prunedValuePaths, path);
		}

		private boolean process(
							boolean slotPath,
							List<IPath> prunedTypePaths,
							List<String> path) {

			if (prunedPrunedPathCandidates.contains(path)) {

				IPath ipath = new IPath(path, slotPath);

				prunedPaths.add(ipath);
				prunedTypePaths.add(ipath);

				return false;
			}

			return true;
		}
	}

	void processPrePruned(IFrame rootFrame) {

		new PrunedPathCandidateAdder().processAll(rootFrame);
	}

	void processPostPruned(IFrame rootFrame) {

		new PrunedPathCandidateRemover().processAll(rootFrame);
		new PrunedPathFinder().processAll(rootFrame);
	}

	List<IPath> getAllPrunedPaths() {

		return prunedPaths;
	}

	List<IPath> getPrunedSlotPaths() {

		return prunedSlotPaths;
	}

	List<IPath> getPrunedValuePaths() {

		return prunedValuePaths;
	}
}
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
 * Responsible for finding the most-specific or most-general
 * members of a specified set of concept-level frames.
 *
 * @author Colin Puleston
 */
public abstract class MostExtremeCFrames {

	private List<CFrame> mostExtremes = new ArrayList<CFrame>();

	private Set<CFrame> totalExtremes = new HashSet<CFrame>();
	private Set<CFrame> intermediates = new HashSet<CFrame>();

	/**
	 * Uses the supplied set of frames to update the current set of
	 * most-general/specific frames.
	 *
	 * @param newFrames Frames for update
	 */
	public void update(Collection<CFrame> newFrames) {

		for (CFrame newFrame : newFrames) {

			update(newFrame);
		}
	}

	/**
	 * Uses the supplied frame to update the current set of
	 * most-general/specific frames.
	 *
	 * @param newFrame Frame for update
	 */
	public void update(CFrame newFrame) {

		if (getNextMoreExtremes(newFrame).isEmpty()) {

			if (totalExtremes.add(newFrame)) {

				mostExtremes.add(newFrame);
				removeLessExtremeIntermediates(newFrame);
			}
		}
		else {

			if (!intermediates.contains(newFrame)
				&& !excludedByTotalExtreme(newFrame)) {

				updateIntermediates(newFrame);
			}
		}
	}

	/**
	 * Retrieves the current set of most-general/specific frames.
	 *
	 * @return Current set of most-general/specific frames
	 */
	public List<CFrame> getCurrents() {

		return new ArrayList<CFrame>(mostExtremes);
	}

	MostExtremeCFrames() {
	}

	abstract boolean firstIsMoreExtreme(CFrame first, CFrame second);

	abstract List<CFrame> getNextMoreExtremes(CFrame current);

	private void updateIntermediates(CFrame newFrame) {

		for (CFrame intermediate : new HashSet<CFrame>(intermediates)) {

			if (firstIsMoreExtreme(intermediate, newFrame)) {

				return;
			}

			if (firstIsMoreExtreme(newFrame, intermediate)) {

				removeIntermediate(intermediate);
			}
		}

		addIntermediate(newFrame);
	}

	private void removeLessExtremeIntermediates(CFrame newFrame) {

		for (CFrame intermediate : new HashSet<CFrame>(intermediates)) {

			if (firstIsMoreExtreme(newFrame, intermediate)) {

				removeIntermediate(intermediate);
			}
		}
	}

	private boolean excludedByTotalExtreme(CFrame newFrame) {

		for (CFrame totalExtreme : totalExtremes) {

			if (firstIsMoreExtreme(totalExtreme, newFrame)) {

				return true;
			}
		}

		return false;
	}

	private void addIntermediate(CFrame intermediate) {

		mostExtremes.add(intermediate);
		intermediates.add(intermediate);
	}

	private void removeIntermediate(CFrame intermediate) {

		mostExtremes.remove(intermediate);
		intermediates.remove(intermediate);
	}
}

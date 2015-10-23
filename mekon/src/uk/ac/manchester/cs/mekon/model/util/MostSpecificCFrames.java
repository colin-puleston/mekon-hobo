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

package uk.ac.manchester.cs.mekon.model.util;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Responsible for finding the most-specific members of a specified
 * set of concept-level frames.
 *
 * @author Colin Puleston
 */
public class MostSpecificCFrames extends MostExtremeCFrames {

	private Set<CFrame> extensions = new HashSet<CFrame>();

	/**
	 * Constructor.
	 */
	public MostSpecificCFrames() {
	}

	/**
	 * Constructor.
	 *
	 * @param frames Frames with which to initialise the set of
	 * most-specific frames.
	 */
	public MostSpecificCFrames(Collection<CFrame> frames) {

		update(frames);
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(CFrame newFrame) {

		if (newFrame.getCategory().extension()) {

			if (extensions.contains(newFrame)) {

				return;
			}

			if (!updateExtensions(newFrame)) {

				return;
			}
		}

		super.update(newFrame);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CFrame> getCurrents() {

		List<CFrame> currents = super.getCurrents();

		for (CFrame leaf : getEndExtremes()) {

			if (subsumedLeaf(leaf)) {

				currents.remove(leaf);
			}
		}

		return currents;
	}

	boolean firstIsMoreExtreme(CFrame first, CFrame second) {

		return first.subsumedBy(second);
	}

	List<CFrame> getNextMoreExtremes(CFrame current) {

		return current.getSubs();
	}

	private boolean updateExtensions(CFrame newExtension) {

		for (CFrame extension : new HashSet<CFrame>(extensions)) {

			if (newExtension.subsumes(extension)) {

				return false;
			}

			if (extension.subsumes(newExtension)) {

				extensions.remove(extension);
			}
		}

		extensions.add(newExtension);

		return true;
	}

	private boolean subsumedLeaf(CFrame leaf) {

		if (leaf.getCategory().extension()) {

			return !extensions.contains(leaf);
		}

		return subsumesExtension(leaf);
	}

	private boolean subsumesExtension(CFrame frame) {

		for (CFrame extension : extensions) {

			if (frame.subsumes(extension)) {

				return true;
			}
		}

		return false;
	}
}
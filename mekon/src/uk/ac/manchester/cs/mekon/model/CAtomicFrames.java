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

import uk.ac.manchester.cs.mekon.util.*;

/**
 * @author Colin Puleston
 */
class CAtomicFrames {

	static final CAtomicFrames INERT_INSTANCE = new CAtomicFrames() {

		boolean add(CAtomicFrame frame) {

			throw new Error("Illegal updating of inert object!");
		}

		int remove(CAtomicFrame frame) {

			throw new Error("Illegal updating of inert object!");
		}
	};

	private KSimpleList<CAtomicFrame> frames = new KSimpleList<CAtomicFrame>();

	private abstract class Getter<F extends CFrame> {

		List<F> getAll(CVisibility visibility) {

			return visibility == CVisibility.ALL ? getAll() : select(visibility);
		}

		abstract List<F> getAll();

		abstract void addSelection(List<F> selected, CAtomicFrame frame);

		private List<F> select(CVisibility visibility) {

			List<F> selected = new ArrayList<F>();

			for (CAtomicFrame frame : frames.asList()) {

				if (visibility.coversHiddenStatus(frame.hidden())) {

					addSelection(selected, frame);
				}
			}

			return selected;
		}
	}

	private class FrameGetter extends Getter<CFrame> {

		List<CFrame> getAll() {

			return frames.asList(CFrame.class);
		}

		void addSelection(List<CFrame> selected, CAtomicFrame frame) {

			selected.add(frame);
		}
	}

	private class AtomicFrameGetter extends Getter<CAtomicFrame> {

		List<CAtomicFrame> getAll() {

			return frames.asList();
		}

		void addSelection(List<CAtomicFrame> selected, CAtomicFrame frame) {

			selected.add(frame);
		}
	}

	boolean add(CAtomicFrame frame) {

		return frames.addValue(frame);
	}

	int remove(CAtomicFrame frame) {

		return frames.removeValue(frame);
	}

	int insert(CAtomicFrame frame, int index) {

		int oldIndex = remove(frame);

		frames.insertValue(frame, index);

		return oldIndex;
	}

	boolean isEmpty() {

		return frames.isEmpty();
	}

	boolean contains(CAtomicFrame frame) {

		return frames.contains(frame);
	}

	List<CAtomicFrame> getAll() {

		return new AtomicFrameGetter().getAll();
	}

	List<CAtomicFrame> getAll(CVisibility visibility) {

		return new AtomicFrameGetter().getAll(visibility);
	}

	List<CFrame> asFrames(CVisibility visibility) {

		return new FrameGetter().getAll(visibility);
	}
}

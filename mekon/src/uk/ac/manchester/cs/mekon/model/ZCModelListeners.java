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

import uk.ac.manchester.cs.mekon.mechanism.core.*;

/**
 * @author Colin Puleston
 */
class ZCModelListeners  {

	private List<ZCModelListener> listeners = new ArrayList<ZCModelListener>();

	void add(ZCModelListener listener) {

		listeners.add(listener);
	}

	void remove(ZCModelListener listener) {

		listeners.remove(listener);
	}

	void onBuildComplete() {

		for (ZCModelListener listener : copyListeners()) {

			listener.onBuildComplete();
		}
	}

	void onFrameAdded(CFrame frame) {

		for (ZCModelListener listener : copyListeners()) {

			listener.onFrameAdded(frame);
		}
	}

	void onFrameRemoved(CFrame frame) {

		for (ZCModelListener listener : copyListeners()) {

			listener.onFrameRemoved(frame);
		}
	}

	void onSlotAdded(CSlot slot) {

		for (ZCModelListener listener : copyListeners()) {

			listener.onSlotAdded(slot);
		}
	}

	void onSlotRemoved(CSlot slot) {

		for (ZCModelListener listener : copyListeners()) {

			listener.onSlotRemoved(slot);
		}
	}

	private List<ZCModelListener> copyListeners() {

		return new ArrayList<ZCModelListener>(listeners);
	}
}

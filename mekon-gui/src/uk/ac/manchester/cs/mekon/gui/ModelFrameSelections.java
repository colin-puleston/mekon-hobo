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

package uk.ac.manchester.cs.mekon.gui;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class ModelFrameSelections {

	private CFrame current = null;
	private Stack<CFrame> previous = new Stack<CFrame>();
	private Stack<CFrame> subsequent = new Stack<CFrame>();
	private Stack<CFrame> lastTo = previous;

	private CFrameSelectionListeners selectionListeners
							= new CFrameSelectionListeners();
	private CFrameSelectionSynchroniser synchroniser
							= new CFrameSelectionSynchroniser();

	private class SelectionRelay extends CFrameSelectionRelay {

		void addUpdateListener(CFrameSelectionListener listener) {

			ModelFrameSelections.this.addSelectionListener(listener);
		}

		void update(CFrame selection) {

			ModelFrameSelections.this.select(selection);
		}
	}

	ModelFrameSelections() {

		synchroniser.add(new SelectionRelay());
	}

	void addSelectionListener(CFrameSelectionListener selectionListener) {

		selectionListeners.add(selectionListener);
	}

	void addSelectionRelay(CFrameSelectionRelay selectionRelay) {

		synchroniser.add(selectionRelay);
	}

	void select(CFrame selected) {

		if (current != null) {

			lastTo.push(current);
		}

		current = selected;
		lastTo = previous;

		selectionListeners.pollForSelected(current);
	}

	boolean canGoBack() {

		return !previous.empty();
	}

	boolean canGoForward() {

		return !subsequent.empty();
	}

	void goBack() {

		if (canGoBack()) {

			lastTo = subsequent;

			select(previous.pop());
		}
	}

	void goForward() {

		if (canGoForward()) {

			select(subsequent.pop());
		}
	}
}

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

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class CFramesList extends GList<CFrame> {

	static private final long serialVersionUID = -1;

	private SelectionRelay selectionRelay = new SelectionRelay();

	private class SelectionRelay extends CFrameSelectionRelay {

		void addUpdateListener(CFrameSelectionListener listener) {

			CFramesList.this.addSelectionListener(listener);
		}

		void update(CFrame selection) {

			CFramesList.this.select(selection);
		}
	}

	CFramesList(List<CFrame> frames) {

		super(true);

		addFrames(frames);
	}

	CFramesList(
		CFrame rootFrame,
		CFrameVisibility visibility,
		boolean includeRoot) {

		super(true);

		if (includeRoot) {

			addFrame(rootFrame);
		}

		addFrames(rootFrame.getDescendants(visibility));
	}

	CFrameSelectionRelay getSelectionRelay() {

		return selectionRelay;
	}

	private void addFrames(List<CFrame> frames) {

		for (CFrame frame : frames) {

			addFrame(frame);
		}
	}

	private void addFrame(CFrame frame) {

		addEntity(frame, EntityDisplays.get().get(frame, false));
	}
}

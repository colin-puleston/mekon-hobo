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

import uk.ac.manchester.cs.mekon.gui.util.icon.*;

/**
 * @author Colin Puleston
 */
class FullEditSlotIcons extends SlotIcons {

	static private final int MARKER_X_OFFSET = ENTITY_SIZE;
	static private final int MARKER_Y_OFFSET = (ENTITY_SIZE - FREED_SLOT_MARKER_SIZE) / 2;

	private GIconRenderer createMarkerRenderer() {

		GIconRenderer r = new GOvalRenderer(
								FREED_SLOT_MARKER_CLR,
								FREED_SLOT_MARKER_SIZE);

		r.setXOffset(MARKER_X_OFFSET);
		r.setYOffset(MARKER_Y_OFFSET);

		return r;
	}

	private GIconRenderer markerRenderer = createMarkerRenderer();

	FullEditSlotIcons() {

		initialise();
	}

	void addExtraRenderers(GIcon icon) {

		icon.addRenderer(markerRenderer);
	}
}

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

import java.awt.*;

import uk.ac.manchester.cs.mekon.gui.util.icon.*;

/**
 * @author Colin Puleston
 */
class SlotIcons extends EntityIconsBySource {

	static private final int BLOCKED_MARKER_OFFSET
								= ENTITY_SIZE - SLOT_CURB_MARKER_WIDTH;
	static private final int INACTIVE_MARKER_OFFSET =
								(ENTITY_SIZE - SLOT_CURB_MARKER_WIDTH) / 2;

	private boolean inactiveSlots;
	private boolean blockedSlots;

	SlotIcons(boolean inactiveSlots, boolean blockedSlots) {

		this.inactiveSlots = inactiveSlots;
		this.blockedSlots = blockedSlots;

		initialise();
	}

	GIcon create(Color mainClr, Color innerClr) {

		GIcon icon = super.create(mainClr, innerClr);

		if (inactiveSlots) {

			icon.addRenderer(
				createCurbMarkerRenderer(
					INACTIVE_MARKER_OFFSET));
		}
		else if (blockedSlots) {

			icon.addRenderer(
				createCurbMarkerRenderer(
					BLOCKED_MARKER_OFFSET));
		}

		return icon;
	}

	GIconRenderer createRenderer(Color clr, int size) {

		return new GTriangleRenderer(
						GTriangleRenderer.Type.RIGHTWARD,
						clr,
						size,
						size);
	}

	private GIconRenderer createCurbMarkerRenderer(int offset) {

		GIconRenderer r = new GRectangleRenderer(
								SLOT_CURB_MARKER_CLR,
								SLOT_CURB_MARKER_WIDTH,
								SLOT_CURB_MARKER_HEIGHT);

		r.setXOffset(offset);

		return r;
	}
}



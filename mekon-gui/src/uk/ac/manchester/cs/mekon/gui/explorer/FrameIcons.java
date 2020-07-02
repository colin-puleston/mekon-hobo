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

package uk.ac.manchester.cs.mekon.gui.explorer;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon_util.gui.icon.*;

/**
 * @author Colin Puleston
 */
class FrameIcons {

	private boolean hiddenFrames;
	private Map<EntityLevel, BySource> icons = new HashMap<EntityLevel, BySource>();

	private class BySource extends EntityIconsBySource {

		private EntityLevel level;

		BySource(EntityLevel level) {

			this.level = level;

			initialise();
		}

		GIcon create(Color mainClr, Color innerClr) {

			GIcon icon = super.create(mainClr, innerClr);

			if (hiddenFrames) {

				icon.addRenderer(createHiddenRenderer());
			}

			return icon;
		}

		GIconRenderer createRenderer(Color clr, int size) {

			return EntityRenderersByLevel.create(level, clr, size);
		}

		private GIconRenderer createHiddenRenderer() {

			GIconRenderer r = new GRectangleRenderer(
										HIDDEN_FRAME_MARKER_CLR,
										HIDDEN_FRAME_MARKER_WIDTH,
										HIDDEN_FRAME_MARKER_HEIGHT);

			r.setYOffset((ENTITY_SIZE - HIDDEN_FRAME_MARKER_HEIGHT) / 2);

			return r;
		}
	}

	FrameIcons(boolean hiddenFrames) {

		this.hiddenFrames = hiddenFrames;

		addForLevel(EntityLevel.INSTANCE);
		addForLevel(EntityLevel.CONCEPT);
		addForLevel(EntityLevel.META);
	}

	Icon get(CSource source, EntityLevel level) {

		return icons.get(level).get(source);
	}

	private void addForLevel(EntityLevel level) {

		icons.put(level, new BySource(level));
	}
}

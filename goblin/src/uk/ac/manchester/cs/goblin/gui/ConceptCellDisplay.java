/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 University of Manchester
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

package uk.ac.manchester.cs.goblin.gui;

import java.awt.Color;
import javax.swing.Icon;

import uk.ac.manchester.cs.mekon.gui.*;
import uk.ac.manchester.cs.mekon.gui.icon.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
enum ConceptCellDisplay {

	DEFAULT(Color.CYAN, false),
	MOVE_SUBJECT(Color.GRAY.brighter(), false),
	DIRECT_CONSTRAINT_TARGET(Color.GREEN, false),
	INDIRECT_CONSTRAINT_TARGET(Color.YELLOW, false),
	SELECTABLE(Color.GRAY, false),
	SELECTION(Color.GRAY, true);

	static private final int ICON_SIZE = 12;
	static private final Color SELECTED_BACKGROUND_CLR = new Color(255,237,160);

	private Icon icon;
	private boolean selected;

	GCellDisplay getFor(Concept concept) {

		String label = concept.getConceptId().getLabel();
		GCellDisplay display = new GCellDisplay(label, icon);

		if (selected) {

			display.setBackgroundColour(SELECTED_BACKGROUND_CLR);
		}

		return display;
	}

	ConceptCellDisplay(Color clr, boolean selected) {

		icon = createIcon(clr);

		this.selected = selected;
	}

	private GIcon createIcon(Color clr) {

		return new GIcon(new GOvalRenderer(clr, ICON_SIZE));
	}
}
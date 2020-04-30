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
import java.awt.Font;
import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.gui.*;
import uk.ac.manchester.cs.mekon.gui.icon.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
enum GoblinCellDisplay {

	CONCEPTS_DEFAULT(Color.CYAN, false),
	CONCEPTS_MOVE_SUBJECT(Color.GRAY.brighter(), false),
	CONCEPTS_CONSTRAINT(Color.GREEN, true),
	CONSTRAINTS_DIRECT_TARGET(Color.GREEN, false),
	CONSTRAINTS_INDIRECT_TARGET(Color.YELLOW, false),
	CONCEPT_SELECTOR(Color.GRAY, false);

	static private final int STANDARD_ICON_SIZE = 12;
	static private final int SMALL_ICON_SIZE = 10;

	static private final Color HIGHLIGHTED_BACKGROUND_CLR = new Color(255,237,160);

	private Icon icon;

	GCellDisplay forConcept(Concept concept) {

		GCellDisplay display = new GCellDisplay(concept.getConceptId().getLabel(), icon);

		display.setFontStyle(Font.BOLD);

		return display;
	}

	GCellDisplay forConcept(Concept concept, boolean highlight) {

		GCellDisplay display = forConcept(concept);

		if (highlight) {

			display.setBackgroundColour(HIGHLIGHTED_BACKGROUND_CLR);
		}

		return display;
	}

	GCellDisplay forConstraint(Constraint constraint) {

		GCellDisplay display = new GCellDisplay(constraint.getType().getName(), icon);

		display.setFontStyle(Font.ITALIC);
		display.setModifier(forConstraintTargets(constraint));

		return display;
	}

	private GoblinCellDisplay(Color clr, boolean smallIcon) {

		icon = createIcon(clr, smallIcon);
	}

	private GIcon createIcon(Color clr, boolean smallIcon) {

		int iconSize = smallIcon ? SMALL_ICON_SIZE : STANDARD_ICON_SIZE;

		return new GIcon(new GOvalRenderer(clr, iconSize));
	}

	private GCellDisplay forConstraintTargets(Constraint constraint) {

		GCellDisplay display = new GCellDisplay(createConstraintTargetsLabel(constraint));

		display.setFontStyle(Font.BOLD | Font.ITALIC);

		return display;
	}

	private String createConstraintTargetsLabel(Constraint constraint) {

		SortedSet<String> labels = new TreeSet<String>();

		for (Concept target : constraint.getTargetValues()) {

			labels.add(target.getConceptId().getLabel());
		}

		return stripListBrackets(labels.toString());
	}

	private String stripListBrackets(String list) {

		return list.substring(1, list.length() - 1);
	}
}
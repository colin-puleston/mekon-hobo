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

import javax.swing.tree.*;

import uk.ac.manchester.cs.mekon.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class HierarchyTree extends ConceptTree {

	static private final long serialVersionUID = -1;

	private ConceptMover conceptMover;

	HierarchyTree(Hierarchy hierarchy, ConceptMover conceptMover) {

		super(false);

		this.conceptMover = conceptMover;

		initialise(hierarchy.getRootConcept());
	}

	ConceptCellDisplay getConceptDisplay(Concept concept) {

		return conceptMover.movingConcept(concept)
					? ConceptCellDisplay.MOVE_SUBJECT
					: ConceptCellDisplay.DEFAULT;
	}

	void onConstraintChange() {

		reselect();
	}

	void update() {

		reselect();
		updateAllNodeDisplays();
	}

	private void reselect() {

		GNode selected = getSelectedNode();

		if (selected != null) {

			TreePath selectedPath = selected.getTreePath();

			setSelectionPath(null);
			setSelectionPath(selectedPath);
		}
	}
}
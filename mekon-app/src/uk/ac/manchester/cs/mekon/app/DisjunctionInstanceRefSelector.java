/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.mekon.app;

import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
class DisjunctionInstanceRefSelector extends DisjunctionEntitySelector<IFrame> {

	static private final long serialVersionUID = -1;

	private InstanceRefSelectionOptions selectionOptions;

	DisjunctionInstanceRefSelector(
		JComponent parent,
		Instantiator instantiator,
		CFrame type,
		boolean clearRequired) {

		super(parent, clearRequired);

		selectionOptions = new InstanceRefSelectionOptions(this, instantiator, type);
	}

	IFrame getSelection() {

		List<IFrame> sels = getDisjunctSelections();

		return sels.size() == 1 ? sels.get(0) : IFrame.createDisjunction(sels);
	}

	JComponent createOptionsComponent() {

		return selectionOptions.createOptionsComponent();
	}

	GCellDisplay getEntityCellDisplay(IFrame entity) {

		return selectionOptions.getRefIdCellDisplay(entity.getReferenceId());
	}

	void removeSelectionConflicts(GList<IFrame> selectionsList, IFrame newSelection) {
	}
}

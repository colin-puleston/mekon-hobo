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

import java.awt.Window;
import java.awt.Dimension;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
class InstanceRefSelector extends Selector<IFrame> {

	static private final long serialVersionUID = -1;

	static private final String SINGLE_SELECT_TITLE = "Select option";
	static private final String MULTI_SELECT_TITLE = SINGLE_SELECT_TITLE + "(s)";

	static private final Dimension WINDOW_SIZE = new Dimension(300, 200);

	static private String getTitle(boolean multiSelect) {

		return multiSelect ? MULTI_SELECT_TITLE : SINGLE_SELECT_TITLE;
	}

	private Instantiator instantiator;
	private CFrame type;
	private boolean multiSelect;

	private InstanceIdsList idsList;

	private class IdsSelectionListener implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent event) {

			if (!event.getValueIsAdjusting()) {

				onIdSelected();
			}
		}
	}

	InstanceRefSelector(
		Window rootWindow,
		Instantiator instantiator,
		CFrame type,
		boolean multiSelect,
		boolean clearRequired) {

		super(rootWindow, getTitle(multiSelect), multiSelect, clearRequired);

		this.instantiator = instantiator;
		this.type = type;
		this.multiSelect = multiSelect;

		idsList = createIdsList();
		idsList.addListSelectionListener(new IdsSelectionListener());
	}

	IFrame getSelection() {

		List<IFrame> refs = idSelectionsToRefs();

		if (refs.isEmpty()) {

			throw new Error("No current selections!");
		}

		return refs.size() == 1 ? refs.get(0) : IFrame.createDisjunction(refs);
	}

	JComponent getInputComponent() {

		return new JScrollPane(idsList);
	}

	Dimension getWindowSize() {

		return WINDOW_SIZE;
	}

	private InstanceIdsList createIdsList() {

		return getInstanceType().getAssertionIdsList().deriveList(multiSelect);
	}

	private InstanceType getInstanceType() {

		return instantiator.getController().getInstanceType(type);
	}

	private List<IFrame> idSelectionsToRefs() {

		List<IFrame> refs = new ArrayList<IFrame>();

		for (CIdentity id : idsList.getSelectedIds()) {

			refs.add(instantiator.instantiateRef(type, id));
		}

		return refs;
	}

	private void onIdSelected() {

		if (multiSelect) {

			setValidSelection(true);
		}
		else {

			setCompletedSelection();
			dispose();
		}
	}
}

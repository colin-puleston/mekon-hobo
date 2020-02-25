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

import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
abstract class InstanceRefSelector extends Selector<IFrame> {

	static private final long serialVersionUID = -1;

	static private final String SINGLE_SELECT_TITLE = "Select option";
	static private final String MULTI_SELECT_TITLE = SINGLE_SELECT_TITLE + "(s)";

	static private final Dimension WINDOW_SIZE = new Dimension(300, 200);

	static private String getTitle(boolean multiSelect) {

		return multiSelect ? MULTI_SELECT_TITLE : SINGLE_SELECT_TITLE;
	}

	private Instantiator instantiator;
	private CFrame type;

	private InstanceIdsList refIdsList;

	private class IdsSelectionListener extends GSelectionListener<CIdentity> {

		protected void onSelected(CIdentity refId) {

			onSelection(refId);
		}

		protected void onDeselected(CIdentity refId) {
		}

		IdsSelectionListener() {

			refIdsList.addSelectionListener(this);
		}
	}

	InstanceRefSelector(
		JComponent parent,
		Instantiator instantiator,
		CFrame type,
		boolean multiSelect,
		boolean clearRequired) {

		super(parent, getTitle(multiSelect), multiSelect, clearRequired);

		this.instantiator = instantiator;
		this.type = type;

		refIdsList = createRefIdsList();

		new IdsSelectionListener();
	}

	JComponent getInputComponent() {

		return new JScrollPane(refIdsList);
	}

	Dimension getWindowSize() {

		return WINDOW_SIZE;
	}

	abstract void onSelection(CIdentity selectedRefId);

	JPanel createSelectorPanel() {

		return new GListPanel<CIdentity>(refIdsList);
	}

	GCellDisplay getRefCellDisplay(CIdentity refId) {

		return refIdsList.getCellDisplay(refId);
	}

	IFrame createRef(CIdentity refId) {

		return instantiator.instantiateRef(type, refId);
	}

	private InstanceIdsList createRefIdsList() {

		return getInstanceType().getAssertionIdsList().deriveList(false);
	}

	private InstanceType getInstanceType() {

		return instantiator.getController().getInstanceType(type);
	}
}

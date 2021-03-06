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

package uk.ac.manchester.cs.mekon.user.app;

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class InstanceRefSelectionOptions extends EntitySelectionOptions<IFrame> {

	private Instantiator instantiator;
	private CFrame selectionType;

	private InstanceIdsList refIdOptionsList;

	private class IdsSelectionListener extends GSelectionListener<CIdentity> {

		protected void onSelected(CIdentity refId) {

			onSelectedOption(createRef(refId));
		}

		protected void onDeselected(CIdentity refId) {
		}

		IdsSelectionListener() {

			refIdOptionsList.addSelectionListener(this);
		}
	}

	InstanceRefSelectionOptions(
		EntitySelector<IFrame> selector,
		Instantiator instantiator,
		CFrame selectionType) {

		super(selector);

		this.instantiator = instantiator;
		this.selectionType = selectionType;

		refIdOptionsList = createOptionsList();

		new IdsSelectionListener();
	}

	JComponent createOptionsComponent() {

		return new GListPanel<CIdentity>(refIdOptionsList);
	}

	GCellDisplay getRefIdCellDisplay(CIdentity refId) {

		return refIdOptionsList.getCellDisplay(refId);
	}

	private InstanceIdsList createOptionsList() {

		CFrame standardType = resolveStandardType();
		InstanceGroup group = getGroup(standardType);

		return group.getAssertionSubGroup().createInstanceIdsList(standardType);
	}

	private InstanceGroup getGroup(CFrame standardType) {

		return instantiator.getController().getInstanceGroup(standardType);
	}

	private CFrame resolveStandardType() {

		InstanceSummariser summariser = getSummariser();

		if (summariser.summaryType(selectionType)) {

			return summariser.toInstanceType(selectionType);
		}

		return selectionType;
	}

	private InstanceSummariser getSummariser() {

		return instantiator.getCustomiser().getInstanceSummariser();
	}

	private IFrame createRef(CIdentity refId) {

		return instantiator.instantiateRef(selectionType, refId);
	}
}

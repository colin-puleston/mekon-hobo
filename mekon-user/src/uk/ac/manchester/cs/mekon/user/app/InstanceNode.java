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
abstract class InstanceNode extends GNode {

	private InstanceTree tree;

	InstanceNode(InstanceTree tree) {

		super(tree);

		this.tree = tree;
	}

	void initialiseExpansion() {

		for (InstanceNode child : getChildren(InstanceNode.class)) {

			child.initialiseExpansion();
		}

		if (!initialExpansionRequired()) {

			collapse();
		}
	}

	void updateFrom() {

		boolean wasCollapsed = collapsed();

		updateChildList();
		updateIndividualChildren();

		if (wasCollapsed) {

			collapse();
		}
	}

	void updateChildList() {
	}

	void checkUpdateArray(SlotDescriptors newSlotDescriptors) {
	}

	InstanceTree getInstanceTree() {

		return tree;
	}

	Instantiator getInstantiator() {

		return tree.getInstantiator();
	}

	boolean queryInstance() {

		return getInstantiator().queryInstance();
	}

	boolean summaryInstance() {

		return tree.summaryInstance();
	}

	boolean viewOnly() {

		return tree.viewOnly();
	}

	boolean showQuerySemantics() {

		return queryInstance() && tree.showQuerySemantics();
	}

	void onMousePresenceUpdate(boolean present) {
	}

	private boolean initialExpansionRequired() {

		return queryInstance() || summaryInstance() || getNodeLevel() <= 1;
	}

	private void updateIndividualChildren() {

		for (InstanceNode child : getChildren(InstanceNode.class)) {

			child.updateFrom();
		}
	}
}

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

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class DescriptorArrayNode extends InstanceNode {

	private SlotDescriptors slotDescriptors;
	private ChildNodeCreator childNodeCreator;

	protected void addInitialChildren() {

		for (Descriptor descriptor : slotDescriptors.getDescriptors()) {

			addChild(descriptor, -1);
		}
	}

	protected GCellDisplay getDisplay() {

		return new GCellDisplay(getDisplayLabel(), getIcon());
	}

	DescriptorArrayNode(InstanceTree tree, SlotDescriptors slotDescriptors) {

		super(tree);

		this.slotDescriptors = slotDescriptors;

		childNodeCreator = new ChildNodeCreator(tree);
	}

	void checkUpdateArray(SlotDescriptors newSlotDescriptors) {

		if (!slotDescriptors.equalDescriptors(newSlotDescriptors)) {

			boolean wasCollapsed = collapsed();

			removeOldChildren(newSlotDescriptors);
			addNewChildren(newSlotDescriptors);

			slotDescriptors = newSlotDescriptors;

			if (wasCollapsed) {

				collapse();
			}
		}
	}

	private void removeOldChildren(SlotDescriptors newSlotDescriptors) {

		int childIdx = 0;

		for (Descriptor descriptor : slotDescriptors.getDescriptors()) {

			if (!newSlotDescriptors.containsDescriptor(descriptor)) {

				removeChild(childIdx--);
			}

			childIdx++;
		}
	}

	private void addNewChildren(SlotDescriptors newSlotDescriptors) {

		int childIdx = 0;

		for (Descriptor descriptor : newSlotDescriptors.getDescriptors()) {

			if (!slotDescriptors.containsDescriptor(descriptor)) {

				addChild(descriptor, childIdx);
			}

			childIdx++;
		}
	}

	private void addChild(Descriptor descriptor, int index) {

		addChild(childNodeCreator.createFor(descriptor), index);
	}

	private String getDisplayLabel() {

		return slotDescriptors.getSlot().getType().getIdentity().getLabel() + " [...]";
	}

	private Icon getIcon() {

		return MekonAppIcons.ARRAY_ICONS.forTree(this, false);
	}
}

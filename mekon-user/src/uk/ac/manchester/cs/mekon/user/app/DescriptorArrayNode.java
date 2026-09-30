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

import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class DescriptorArrayNode extends InstanceNode {

	private ISlot slot;
	private SlotDescriptors slotDescriptors;

	private ChildNodeCreator childNodeCreator;
	private GNodeAction reorderAction = new ReorderAction();

	private Customiser customiser;

	private class ReorderAction extends GNodeAction {

		protected void perform() {

			if (reorderable()) {

				List<IValue> newVals = checkReorder(slot.getValues().asList());

				if (newVals != null) {

					ISlotValuesEditor ed = slot.getValuesEditor();

					ed.clear();
					ed.addAll(newVals);
				}
			}
		}

		private List<IValue> checkReorder(List<IValue> oldVals) {

			if (slotValueCount() == 2) {

				return swap2Values(oldVals);
			}

			ArrayReorderDialog dlg = new ArrayReorderDialog(customiser, oldVals);

			return dlg.reordered() ? dlg.getCurrentOrder() : null;
		}

		private List<IValue> swap2Values(List<IValue> oldVals) {

			return Arrays.asList(new IValue[]{oldVals.get(1), oldVals.get(0)});
		}
	}

	protected void addInitialChildren() {

		for (Descriptor descriptor : slotDescriptors.getDescriptors()) {

			addChild(descriptor, -1);
		}
	}

	protected GCellDisplay getDisplay() {

		return new GCellDisplay(getDisplayLabel(), getIcon());
	}

	protected GNodeAction getPositiveAction1() {

		return reorderAction;
	}

	DescriptorArrayNode(InstanceTree tree, SlotDescriptors slotDescriptors) {

		super(tree);

		this.slotDescriptors = slotDescriptors;

		slot = slotDescriptors.getSlot();
		childNodeCreator = new ChildNodeCreator(tree);
		customiser = tree.getInstance().getCustomiser();
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

		return DescriptorLabels.forArrayHeader(slot);
	}

	private Icon getIcon() {

		return reorderable()
				? InstanceTreeIcons.REORDERABLE_ARRAY
				: InstanceTreeIcons.VALUES.forTree(this, false);
	}

	private boolean reorderable() {

		return editableAssertion() && slotValueCount() > 1;
	}

	private boolean editableAssertion() {

		return assertionInstance() && editableSlot() && !viewOnly();
	}

	private boolean editableSlot() {

		return slot.getEditability().editable();
	}

	private int slotValueCount() {

		return slot.getValues().size();
	}
}

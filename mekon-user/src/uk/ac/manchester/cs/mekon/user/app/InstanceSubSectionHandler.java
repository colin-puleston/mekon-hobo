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

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class InstanceSubSectionHandler {

	static private class EditDisplayer {

		private InstanceTree tree;
		private IFrame rootFrame;
		private ISlot containerSlot;

		EditDisplayer(InstanceTree tree, IFrame rootFrame, ISlot containerSlot) {

			this.tree = tree;
			this.rootFrame = rootFrame;
			this.containerSlot = containerSlot;
		}

		boolean display() {

			InstanceSubSectionDialog dialog = createDialog();

			dialog.display();

			if (dialog.replaceSelected()) {

				return false;
			}

			if (dialog.clearSelected()) {

				removeValue();
			}

			return true;
		}

		private InstanceSubSectionDialog createDialog() {

			return new InstanceSubSectionDialog(tree, rootFrame, replacableValue());
		}

		private boolean replacableValue() {

			return !getSlotValueTypeFrame().getSubs(CVisibility.EXPOSED).isEmpty();
		}

		private void removeValue() {

			containerSlot.getValuesEditor().remove(rootFrame);
		}

		private CFrame getSlotValueTypeFrame() {

			return (CFrame)containerSlot.getValueType();
		}
	}

	static boolean checkDisplayForEdit(InstanceTree tree, ISlot frameValueSlot) {

		IFrame rootFrame = getStructuredFrameValueOrNull(frameValueSlot);

		if (rootFrame != null) {

			return new EditDisplayer(tree, rootFrame, frameValueSlot).display();
		}

		return false;
	}

	static boolean checkDisplayForEdit(InstanceTree tree, Descriptor descriptor) {

		IFrame rootFrame = getStructuredFrameValueOrNull(descriptor);

		if (rootFrame != null) {

			return new EditDisplayer(tree, rootFrame, descriptor.getSlot()).display();
		}

		return false;
	}

	static void checkDisplayForView(InstanceTree tree, Descriptor descriptor) {

		IFrame rootFrame = getStructuredFrameValueOrNull(descriptor);

		if (rootFrame != null) {

			displayForView(tree, rootFrame);
		}
	}

	static IFrame getStructuredFrameValueOrNull(ISlot frameValueSlot) {

		List<IValue> values = frameValueSlot.getValues().asList();

		return values.size() == 1 ? (IFrame)values.get(0) : null;
	}

	static IFrame getStructuredFrameValueOrNull(Descriptor descriptor) {

		return descriptor.hasStructuredValue() ? (IFrame)descriptor.getValue() : null;
	}

	static private void displayForView(InstanceTree tree, IFrame rootFrame) {

		if (!getCustomiser(tree).performStructuredValueViewAction(rootFrame)) {

			new InstanceSubSectionDialog(tree, rootFrame, false).display();
		}
	}

	static private Customiser getCustomiser(InstanceTree tree) {

		return tree.getInstantiator().getCustomiser();
	}
}

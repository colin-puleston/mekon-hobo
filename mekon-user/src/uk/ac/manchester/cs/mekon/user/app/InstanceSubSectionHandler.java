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

	static private class Displayer {

		private InstanceTree instanceTree;

		private IFrame rootFrame;
		private ISlot containerSlot;

		Displayer(
			InstanceTree instanceTree,
			IFrame rootFrame,
			ISlot containerSlot) {

			this.instanceTree = instanceTree;
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

			return new InstanceSubSectionDialog(instanceTree, rootFrame, replacableValue());
		}

		private void removeValue() {

			containerSlot.getValuesEditor().remove(rootFrame);
		}

		private boolean replacableValue() {

			return !getSlotValueTypeFrame().getSubs(CVisibility.EXPOSED).isEmpty();
		}

		private CFrame getSlotValueTypeFrame() {

			return (CFrame)containerSlot.getValueType();
		}
	}

	static boolean checkDisplay(InstanceTree instanceTree, ISlot containerSlot) {

		List<IValue> values = containerSlot.getValues().asList();

		if (values.size() == 1) {

			IFrame rootFrame = (IFrame)values.get(0);

			return new Displayer(instanceTree, rootFrame, containerSlot).display();
		}

		return false;
	}

	static boolean checkDisplay(InstanceTree instanceTree, Descriptor descriptor) {

		if (descriptor.hasStructuredValue()) {

			IFrame rootFrame = (IFrame)descriptor.getValue();
			ISlot containerSlot = descriptor.getSlot();

			return new Displayer(instanceTree, rootFrame, containerSlot).display();
		}

		return false;
	}
}

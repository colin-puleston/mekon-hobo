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

/**
 * @author Colin Puleston
 */
class InstanceSubSectionHandler {

	private JComponent parent;
	private Instantiator instantiator;

	private IFrame rootFrame = null;
	private ISlot containerSlot = null;

	private boolean replaceSelected = false;

	InstanceSubSectionHandler(
		JComponent parent,
		Instantiator instantiator,
		Descriptor descriptor) {

		this.parent = parent;
		this.instantiator = instantiator;

		if (descriptor.hasStructuredValue()) {

			rootFrame = (IFrame)descriptor.getValue();
			containerSlot = descriptor.getSlot();
		}
	}

	boolean checkDisplay(InstanceDisplayMode startMode) {

		if (rootFrame != null) {

			displayDialog(startMode);

			return true;
		}

		return false;
	}

	boolean replaceSelected() {

		return replaceSelected;
	}

	private void displayDialog(InstanceDisplayMode startMode) {

		InstanceSubSectionDialog dialog = createDialog(startMode);

		dialog.display();

		if (dialog.clearSelected()) {

			removeValue();
		}
		else if (dialog.replaceSelected()) {

			replaceSelected = true;
		}
	}

	private InstanceSubSectionDialog createDialog(InstanceDisplayMode startMode) {

		return new InstanceSubSectionDialog(parent, instantiator, rootFrame, startMode);
	}

	private void removeValue() {

		containerSlot.getValuesEditor().remove(rootFrame);
	}
}

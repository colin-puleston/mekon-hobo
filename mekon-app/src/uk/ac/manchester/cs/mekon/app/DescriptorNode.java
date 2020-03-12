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

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
class DescriptorNode extends InstanceNode {

	private GNodeAction editAction;
	private GCellDisplay display;

	private boolean editActive = true;

	private abstract class EditActiveAction extends GNodeAction {

		protected void perform() {

			if (!viewOnly()) {

				performEditActiveAction();
			}
		}

		abstract void performEditActiveAction();
	}

	private class EditAction extends EditActiveAction {

		private DescriptorEditor editor;

		EditAction(Descriptor descriptor) {

			editor = createEditor(descriptor);
		}

		void performEditActiveAction() {

			editor.performEditAction();
		}
	}

	private class NoEditAction extends EditActiveAction {

		void performEditActiveAction() {

			JOptionPane.showMessageDialog(null, "Automatically derived value!");
		}
	}

	protected GCellDisplay getDisplay() {

		return display;
	}

	protected GNodeAction getPositiveAction1() {

		return editAction;
	}

	DescriptorNode(InstanceTree tree, Descriptor descriptor) {

		super(tree);

		editAction = createEditAction(descriptor);
		display = createDisplay(descriptor);
	}

	private GNodeAction createEditAction(Descriptor descriptor) {

		return descriptor.userEditable() ? new EditAction(descriptor) : new NoEditAction();
	}

	private GCellDisplay createDisplay(Descriptor descriptor) {

		return new DescriptorCellDisplay(descriptor, queryInstance()).create();
	}

	private DescriptorEditor createEditor(Descriptor descriptor) {

		return new DescriptorEditor(getInstanceTree(), getInstantiator(), descriptor);
	}
}

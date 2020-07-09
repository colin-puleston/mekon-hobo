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

package uk.ac.manchester.cs.mekon.gui.app;

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class DescriptorNode extends InstanceNode {

	private Descriptor descriptor;

	private GNodeAction action;
	private DescriptorEditor editor;

	private abstract class DescriptorAction extends GNodeAction {

		protected void perform() {

			if (viewOnly()) {

				performViewAction();
			}
			else {

				performEditAction();
			}
		}

		abstract void performEditAction();
	}

	private class UserEditAction extends DescriptorAction {

		void performEditAction() {

			if (editor.performEditAction()) {

				getInstanceTree().updateAllNodeDisplays();
			}
		}
	}

	private class AutoEditAction extends DescriptorAction {

		void performEditAction() {

			JOptionPane.showMessageDialog(null, "Automatically derived value!");
		}
	}

	protected GCellDisplay getDisplay() {

		return new DescriptorCellDisplay(descriptor, queryInstance(), viewOnly()).create();
	}

	protected GNodeAction getPositiveAction1() {

		return action;
	}

	DescriptorNode(InstanceTree tree, Descriptor descriptor) {

		super(tree);

		this.descriptor = descriptor;

		action = createAction();
		editor = createEditor();
	}

	void performViewAction() {
	}

	private GNodeAction createAction() {

		return descriptor.userEditable() ? new UserEditAction() : new AutoEditAction();
	}

	private DescriptorEditor createEditor() {

		return new DescriptorEditor(getInstanceTree(), getInstantiator(), descriptor);
	}
}

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

import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
class DescriptorNode extends InstantiationNode {

	static private final Color NON_EDITABLE_BACKGROUND_CLR = new Color(220,220,200);

	private Descriptor descriptor;
	private GNodeAction editAction;

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

		private DescriptorEditor editor = createEditor();

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

		GCellDisplay display = getCoreDisplay();

		if (descriptor.hasValue()) {

			display.setModifier(getValueDisplay());
		}

		return display;
	}

	protected GNodeAction getPositiveAction1() {

		return editAction;
	}

	DescriptorNode(InstantiationTree tree, Descriptor descriptor) {

		super(tree);

		this.descriptor = descriptor;

		editAction = editable() ? new EditAction() : new NoEditAction();
	}

	Icon getValueIcon() {

		if (descriptor.instanceRefType()) {

			return MekonAppIcons.ASSERTION_REF;
		}

		if (queryInstantiation()) {

			return MekonAppIcons.QUERY_VALUE;
		}

		return MekonAppIcons.ASSERTION_VALUE;
	}

	private GCellDisplay getCoreDisplay() {

		return new GCellDisplay(descriptor.getIdentityLabel(), getIcon());
	}

	private GCellDisplay getValueDisplay() {

		GCellDisplay display = new GCellDisplay(descriptor.getValueLabel());

		display.setFontStyle(Font.BOLD);

		if (!editable()) {

			display.setBackgroundColour(NON_EDITABLE_BACKGROUND_CLR);
		}

		return display;
	}

	private Icon getIcon() {

		return descriptor.hasValue() ? getValueIcon() : MekonAppIcons.NO_VALUE;
	}

	private DescriptorEditor createEditor() {

		return new DescriptorEditor(getRootWindow(), getInstantiator(), descriptor);
	}

	private boolean editable() {

		return descriptor.userEditable();
	}
}

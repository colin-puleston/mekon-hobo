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

	private Descriptor descriptor;
	private GNodeAction editAction;

	private class EditAction extends GNodeAction {

		private DescriptorEditor editor = createEditor();

		protected void perform() {

			editor.performEditAction();
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

		editAction = editable() ? new EditAction() : GNodeAction.INERT_ACTION;
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

		display.setFontStyle(editable() ? Font.BOLD : (Font.BOLD | Font.ITALIC));

		return display;
	}

	private Icon getIcon() {

		return descriptor.hasValue() ? getValueIcon() : MekonAppIcons.NO_VALUE;
	}

	private DescriptorEditor createEditor() {

		return new DescriptorEditor(getRootWindow(), getInstantiator(), descriptor);
	}

	private boolean editable() {

		return descriptor.getSlot().getEditability().editable();
	}
}

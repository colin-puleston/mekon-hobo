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

import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class DescriptorCellDisplay {

	static private final String OR_LABEL = "OR";

	private DescriptorNode node;
	private Descriptor descriptor;

	private class IdentityCellDisplay extends GCellDisplay {

		protected void onLabelAdded(JLabel label) {

			onIdentityLabelAdded(label);
		}

		IdentityCellDisplay(String text, Icon icon) {

			super(text, icon);
		}
	}

	DescriptorCellDisplay(DescriptorNode node, Descriptor descriptor) {

		this.node = node;
		this.descriptor = descriptor;
	}

	GCellDisplay create() {

		GCellDisplay display = createForIdentity();

		if (descriptor.hasValue()) {

			display.setModifier(createForValue());
		}

		return display;
	}

	void onIdentityLabelAdded(JLabel label) {
	}

	private GCellDisplay createForIdentity() {

		return new IdentityCellDisplay(descriptor.getIdentityLabel(), getIcon());
	}

	private GCellDisplay createForValue() {

		GCellDisplay first = null;
		GCellDisplay previous = null;

		for (String label : descriptor.getValueDisjunctLabels()) {

			if (first == null) {

				first = createDisjunctComponent(label);
				previous = first;
			}
			else {

				GCellDisplay or = createSyntaxComponent(OR_LABEL);
				GCellDisplay value = createDisjunctComponent(label);

				previous.setModifier(or);
				or.setModifier(value);

				previous = value;
			}
		}

		return first;
	}

	private GCellDisplay createDisjunctComponent(String label) {

		GCellDisplay comp = new GCellDisplay(label);

		comp.setFontStyle(Font.BOLD);

		return comp;
	}

	private GCellDisplay createSyntaxComponent(String label) {

		GCellDisplay comp = new GCellDisplay(label);

		comp.setFontStyle(Font.ITALIC);

		return comp;
	}

	private Icon getIcon() {

		return descriptor.hasValue() ? getValueIcon() : MekonAppIcons.VALUE_ENTRY;
	}

	private Icon getValueIcon() {

		return getValueIcons().get(query(), editable());
	}

	private MekonAppIcons.TreeIcons getValueIcons() {

		return descriptor.hasInstanceRefValue()
				? MekonAppIcons.REF_ICONS
				: MekonAppIcons.VALUE_ICONS;
	}

	private boolean query() {

		return getInstanceTree().getInstantiator().queryInstance();
	}

	private boolean editable() {

		return descriptor.userEditable() && !getInstanceTree().viewOnly();
	}

	private InstanceTree getInstanceTree() {

		return node.getInstanceTree();
	}
}

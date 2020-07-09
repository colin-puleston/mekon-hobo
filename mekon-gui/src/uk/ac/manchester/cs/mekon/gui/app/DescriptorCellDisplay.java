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

import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class DescriptorCellDisplay {

	static private final Color NON_EDITABLE_BACKGROUND_CLR = new Color(220,220,200);

	static private final String OR_LABEL = "or";

	private Descriptor descriptor;
	private boolean queryInstance;

	private class MonitoredCellDisplay extends GCellDisplay {

		protected void onLabelAdded(JLabel label) {

			DescriptorCellDisplay.this.onLabelAdded(label);
		}

		MonitoredCellDisplay(String text) {

			super(text);
		}

		MonitoredCellDisplay(String text, Icon icon) {

			super(text, icon);
		}
	}

	DescriptorCellDisplay(Descriptor descriptor, boolean queryInstance) {

		this.descriptor = descriptor;
		this.queryInstance = queryInstance;
	}

	GCellDisplay create() {

		GCellDisplay display = createForId();

		if (descriptor.hasValue()) {

			display.setModifier(createForValue());
		}

		return display;
	}

	void onLabelAdded(JLabel label) {
	}

	private GCellDisplay createForId() {

		return new MonitoredCellDisplay(descriptor.getIdentityLabel(), getIcon());
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

				GCellDisplay or = createValueComponent(OR_LABEL);
				GCellDisplay value = createDisjunctComponent(label);

				previous.setModifier(or);
				or.setModifier(value);

				previous = value;
			}
		}

		return first;
	}

	private GCellDisplay createDisjunctComponent(String label) {

		GCellDisplay comp = createValueComponent(label);

		comp.setFontStyle(Font.BOLD);

		return comp;
	}

	private GCellDisplay createValueComponent(String label) {

		GCellDisplay comp = new MonitoredCellDisplay(label);

		if (!editable()) {

			comp.setBackgroundColour(NON_EDITABLE_BACKGROUND_CLR);
		}

		return comp;
	}

	private Icon getIcon() {

		return descriptor.hasValue() ? getValueIcon() : MekonAppIcons.NO_VALUE;
	}

	private Icon getValueIcon() {

		if (descriptor.hasInstanceRefValue()) {

			return queryInstance ? MekonAppIcons.QUERY_REF : MekonAppIcons.ASSERTION_REF;
		}

		return queryInstance ? MekonAppIcons.QUERY_VALUE : MekonAppIcons.ASSERTION_VALUE;
	}

	private boolean editable() {

		return descriptor.userEditable();
	}
}

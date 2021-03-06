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

import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class URLDescriptorNode extends DescriptorNode {

	static private final Color SELECTABLE_VALUE_CLR = Color.BLUE;
	static private final Color SELECTED_VALUE_CLR = Color.CYAN;

	static private final int VALUE_FONT_STYLE = Font.BOLD;

	private Descriptor descriptor;
	private URI url;

	private boolean mousePresent = false;

	private class ValueCellDisplay extends GCellDisplay {

		protected void onLabelAdded(JLabel label) {

			label.setText(abreviateURL(label.getText()));

			if (viewOnly()) {

				label.setForeground(getValueColour());
			}
		}

		ValueCellDisplay(String label) {

			super(label);

			setFontStyle(VALUE_FONT_STYLE);
		}

		private String abreviateURL(String text) {

			int last = text.replace("//", "  ").indexOf('/');

			return last != -1 ? (text.substring(0, last + 1) + "...") : text;
		}

		private Color getValueColour() {

			return mousePresent ? SELECTED_VALUE_CLR : SELECTABLE_VALUE_CLR;
		}
	}

	private class URLCellDisplay extends DescriptorCellDisplay {

		URLCellDisplay() {

			super(URLDescriptorNode.this, descriptor);
		}

		GCellDisplay createForValue(String label) {

			return new ValueCellDisplay(label);
		}
	}

	protected GCellDisplay getDisplay() {

		return new URLCellDisplay().create();
	}

	URLDescriptorNode(InstanceTree tree, Descriptor descriptor) {

		super(tree, descriptor);

		this.descriptor = descriptor;

		url = extractURLOrNull();
	}

	void performViewAction() {

		if (url != null) {

			try {

				Desktop.getDesktop().browse(url);
			}
			catch (IOException e) {

				System.out.println("DESKTOP ACCESS ERROR: " + e.getMessage());
			}
		}
	}

	void onMousePresenceUpdate(boolean present) {

		if (viewOnly()) {

			mousePresent = present;

			updateNodeDisplay();
		}
	}

	private URI extractURLOrNull() {

		if (descriptor.hasValue()) {

			IString value = (IString)descriptor.getValue();

			return URI.create(value.get());
		}

		return null;
	}
}

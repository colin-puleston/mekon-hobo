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

	static private final Color SELECTABLE_URL_CLR = Color.BLUE;
	static private final Color SELECTED_URL_CLR = Color.CYAN;

	private Descriptor descriptor;
	private URI url;

	private boolean mousePresent = false;

	private class CellDisplay extends DescriptorCellDisplay {

		CellDisplay() {

			super(descriptor, queryInstance());
		}

		void onLabelAdded(JLabel label) {

			if (viewOnly() && urlLabel(label)) {

				label.setForeground(mousePresent ? SELECTED_URL_CLR : SELECTABLE_URL_CLR);
			}
		}

		private boolean urlLabel(JLabel label) {

			return url != null && label.getText().equals(url.toString());
		}
	}

	protected GCellDisplay getDisplay() {

		return new CellDisplay().create();
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

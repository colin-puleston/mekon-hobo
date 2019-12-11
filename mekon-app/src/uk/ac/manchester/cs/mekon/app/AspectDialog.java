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
class AspectDialog extends GDialog implements AspectWindow {

	static private final long serialVersionUID = -1;

	static private final int FRAME_WIDTH = 700;

	private AspectWindow parentWindow;
	private AspectEditManager editManager;

	private EditStatus status = EditStatus.NONE;

	private class ControlPanel extends EditControlPanel {

		static private final long serialVersionUID = -1;

		ControlPanel(boolean clearRequired) {

			configure(true, clearRequired, false);
		}

		void onEditComplete(EditStatus status) {

			AspectDialog.this.status = status;

			dispose();
		}
	}

	public Dimension getPreferredSize() {

		return new Dimension(FRAME_WIDTH, getPreferredHeight());
	}

	public Window getRootWindow() {

		return parentWindow.getRootWindow();
	}

	public Instantiator getInstantiator() {

		return parentWindow.getInstantiator();
	}

	public void displayCopy() {

		editManager.update();
		editManager.invokeEdit();
	}

	AspectDialog(AspectWindow parentWindow, AspectEditManager editManager, ISlot slot) {

		super(parentWindow.getRootWindow(), slot.getValueType().getDisplayLabel(), true);

		this.parentWindow = parentWindow;
		this.editManager = editManager;
	}

	EditStatus display(DescriptorsList descriptors, boolean clearRequired) {

		display(createDisplay(new DescriptorsTable(this, descriptors), clearRequired));

		return status;
	}

	private JPanel createDisplay(DescriptorsTable table, boolean clearRequired) {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		panel.add(new ControlPanel(clearRequired), BorderLayout.SOUTH);

		return panel;
	}

	private int getPreferredHeight() {

		return (int)super.getPreferredSize().getHeight();
	}
}



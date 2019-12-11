/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package uk.ac.manchester.cs.mekon.explorer;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon.store.*;

import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
class InstanceStorePanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String TITLE = "Instance Store";
	static private final String RETRIEVE_BUTTON_LABEL = "Retrieve...";
	static private final String REMOVE_BUTTON_LABEL = "Remove...";

	private InstanceStoreActions actions;

	private abstract class ActionButton extends GButton {

		static private final long serialVersionUID = -1;

		ActionButton(String label) {

			super(label);

			actions.setInstanceStoreComponentEnabling(this);
		}
	}

	private class RetrieveButton extends ActionButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			actions.retrieveAndDisplaySelectedInstance();
		}

		RetrieveButton() {

			super(RETRIEVE_BUTTON_LABEL);
		}
	}

	private class RemoveButton extends ActionButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			actions.removeSelectedInstance();
		}

		RemoveButton() {

			super(REMOVE_BUTTON_LABEL);
		}
	}

	InstanceStorePanel(InstanceStoreActions actions) {

		super(new BorderLayout());

		this.actions = actions;

		setBorder(createBorder());
		add(createButtonsComponent(), BorderLayout.EAST);
	}

	private TitledBorder createBorder() {

		TitledBorder border = new TitledBorder(TITLE);
		Font font = border.getTitleFont();

		if (font != null) {

			border.setTitleFont(GFonts.toMedium(font));
		}

		return border;
	}

	private JComponent createButtonsComponent() {

		JPanel panel = new JPanel();

		panel.add(new RetrieveButton());
		panel.add(new RemoveButton());

		return panel;
	}
}

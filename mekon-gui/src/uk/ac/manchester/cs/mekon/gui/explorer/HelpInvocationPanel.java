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
package uk.ac.manchester.cs.mekon.gui.explorer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class HelpInvocationPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String BUTTON_LABEL = "Help...";
	static private final String FRAME_TITLE = MekonModelExplorer.getSystemTitle("Help");
	static private final int FRAME_WIDTH = 900;
	static private final int FRAME_HEIGHT = 400;

	static private class HelpFrame extends GFrame {

		static private final long serialVersionUID = -1;

		HelpFrame(HelpPanel helpPanel, WindowListener buttonEnabler) {

			super(FRAME_TITLE, FRAME_WIDTH, FRAME_HEIGHT);

			addWindowListener(buttonEnabler);
			display(helpPanel);
		}
	}

	private HelpPanel helpPanel = new HelpPanel();
	private HelpButton helpButton = new HelpButton();

	private class HelpButton extends GButton {

		static private final long serialVersionUID = -1;

		private ButtonEnabler buttonEnabler = new ButtonEnabler();

		private class ButtonEnabler extends WindowAdapter {

			public void windowClosing(WindowEvent e) {

				setEnabled(true);
			}
		}

		protected void doButtonThing() {

			setEnabled(false);

			new HelpFrame(helpPanel, buttonEnabler);
		}

		HelpButton() {

			super(BUTTON_LABEL);
		}
	}

	HelpInvocationPanel() {

		add(helpButton);
	}
}

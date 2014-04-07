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
package uk.ac.manchester.cs.mekon.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class InstantiatonsPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String INSTANTIATE_BUTTON_LABEL = "Instantiate";
	static private final String ABSTRACT_SELECTOR_LABEL = "Abstract Instances";

	static private final float ABSTRACT_SELECTOR_FONT_SIZE = 14;

	private CModel model;
	private CFrame frame = null;

	private int instantiationCount = 0;
	private InstantiationEndListener instantiationEndListener
										= new InstantiationEndListener();

	private InstantiateButton instantiateButton = new InstantiateButton();
	private AbstractToggler abstractToggler = new AbstractToggler();

	private class AbstractToggler
					extends JCheckBox
					implements ActionListener {

		static private final long serialVersionUID = -1;

		public void actionPerformed(ActionEvent event) {

			toggleAbstract();
		}

		AbstractToggler() {

			super(ABSTRACT_SELECTOR_LABEL);

			setFontSize();
			addActionListener(this);
		}

		private void setFontSize() {

			setFont(getFont().deriveFont(ABSTRACT_SELECTOR_FONT_SIZE));
		}
	}

	private class InstantiateButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			instantiate();
		}

		InstantiateButton() {

			super(INSTANTIATE_BUTTON_LABEL + "...");

			setEnabled(false);
		}
	}

	private class InstantiationEndListener extends WindowAdapter {

		public void windowClosing(WindowEvent e) {

			onInstantiationEnd();
		}
	}

	InstantiatonsPanel(CModel model) {

		super(new BorderLayout());

		this.model = model;

		add(abstractToggler, BorderLayout.WEST);
		add(instantiateButton, BorderLayout.EAST);
	}

	void setSelectedFrame(CFrame frame) {

		this.frame = frame;

		instantiateButton.setEnabled(frame.instantiable());
	}

	private void toggleAbstract() {

		boolean current = model.abstractInstantiations();

		model.setAbstractInstantiations(!current);
	}

	private void instantiate() {

		InstantiationFrame instFrame = new InstantiationFrame(frame);

		instFrame.addWindowListener(instantiationEndListener);
		instFrame.display();

		if (++instantiationCount == 1) {

			abstractToggler.setEnabled(false);
		}
	}

	private void onInstantiationEnd() {

		if (--instantiationCount == 0) {

			abstractToggler.setEnabled(true);
		}
	}
}

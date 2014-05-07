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
package uk.ac.manchester.cs.mekon.gui.util;

import java.awt.*;
import javax.swing.*;

public class GVerticalPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private GridBagConstraints createConstraints(boolean verticalStretch) {

		GridBagConstraints constraints = new GridBagConstraints();

		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.weightx = 1;

		if (verticalStretch) {

			constraints.weighty = 1;
			constraints.fill = GridBagConstraints.BOTH;
		}
		else {

			constraints.fill = GridBagConstraints.HORIZONTAL;
		}

		return constraints;
	}

	private GridBagConstraints defaultConstraints;

	public GVerticalPanel(boolean verticalStretch) {

		this(createConstraints(verticalStretch));
	}

	public void addComponent(JComponent component) {

		add(component, defaultConstraints);
	}

	public void addComponent(JComponent component, boolean verticalStretch) {

		add(component, createConstraints(verticalStretch));
	}

	private GVerticalPanel(GridBagConstraints defaultConstraints) {

		super(new GridBagLayout());

		this.defaultConstraints = defaultConstraints;
	}

	private void addComponent(JComponent component, GridBagConstraints constraints) {

		add(component, constraints);
	}
}

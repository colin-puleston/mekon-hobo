/*
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

package uk.ac.manchester.cs.mekon_util.gui;

import java.awt.*;
import javax.swing.*;

/**
 * @author Colin Puleston
 */
public class GDialog extends JDialog {

	static private final long serialVersionUID = -1;

	static private Window findOwnerWindow(JComponent parent) {

		return (Window)SwingUtilities.getAncestorOfClass(Window.class, parent);
	}

	static private ModalityType getModalityType(boolean modal) {

		return modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS;
	}

	public GDialog(Window ownerWindow, String title, boolean modal) {

		super(ownerWindow, title, getModalityType(modal));
	}

	public GDialog(JComponent parent, String title, boolean modal) {

		this(findOwnerWindow(parent), title, modal);
	}

	public void display(JComponent content) {

		getContentPane().add(content);
		pack();
		centreOnScreen();
		setVisible(true);
	}

	private void centreOnScreen() {

		Dimension d = getSize();
		Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();

		double w = (sd.getWidth() - d.getWidth()) / 2;
		double h = (sd.getHeight() - d.getHeight()) / 2;

		setLocation((int)w, (int)h);
	}
}

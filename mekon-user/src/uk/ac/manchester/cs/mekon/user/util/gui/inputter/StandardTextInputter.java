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

package uk.ac.manchester.cs.mekon.user.util.gui.inputter;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
public abstract class StandardTextInputter<I> extends TextInputter<I> {

	static private final long serialVersionUID = -1;

	static private final int WINDOW_WIDTH = 300;
	static private final int WINDOW_HEIGHT_BASE = 40;
	static private final int PLAIN_INPUT_HEIGHT = 30;
	static private final int TITLED_INPUT_HEIGHT = 60;

	private JPanel inputFieldsPanel = new JPanel();

	private int windowHeight = WINDOW_HEIGHT_BASE;

	public I getInput() {

		return resolveInput();
	}

	protected StandardTextInputter(
					JComponent parent,
					String title,
					boolean canOk,
					boolean canClear) {

		super(parent, title, canOk, canClear);

		inputFieldsPanel.setLayout(new BoxLayout(inputFieldsPanel, BoxLayout.Y_AXIS));
	}

	protected GTextField addInputField(TextInputHandler<I> inputHandler) {

		GTextField field = createInputField(inputHandler);

		addInputComponent(field, PLAIN_INPUT_HEIGHT);

		return field;
	}

	protected GTextField addInputField(String title, TextInputHandler<I> inputHandler) {

		GTextField field = createInputField(inputHandler);
		JComponent fieldComponent = createTitledFieldComponent(title, field);

		inputHandler.resetFieldComponent(fieldComponent);

		addInputComponent(fieldComponent, TITLED_INPUT_HEIGHT);

		return field;
	}

	protected JComponent getInputComponent() {

		return inputFieldsPanel;
	}

	protected Dimension getWindowSize() {

		return new Dimension(WINDOW_WIDTH, windowHeight);
	}

	private void addInputComponent(JComponent fieldComponent, int height) {

		inputFieldsPanel.add(fieldComponent);

		windowHeight += height;
	}

	private JComponent createTitledFieldComponent(String title, GTextField field) {

		JPanel panel = new JPanel(new GridLayout(1, 1));

		panel.setBorder(new TitledBorder(title));
		panel.add(field);

		return panel;
	}
}



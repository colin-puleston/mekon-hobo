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

package uk.ac.manchester.cs.mekon.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Colin Puleston
 */
public abstract class GFilterPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String PANEL_TITLE = "Filter";
	static private final String START_ONLY_TITLE = "begins with";

	private JTextField filterField = new JTextField();
	private StartOnlyBox startOnlyBox = new StartOnlyBox();

	private class FilterListener extends KeyAdapter {

		public void keyReleased(KeyEvent event) {

			updateFiltering(startOnlyBox.isSelected());
		}
	}

	private class StartOnlyBox extends GCheckBox {

		static private final long serialVersionUID = -1;

		protected void onSelectionUpdate(boolean selected) {

			updateFiltering(selected);
		}

		StartOnlyBox() {

			super(START_ONLY_TITLE);

			setSelected(false);
		}
	}

	public GFilterPanel() {

		super(new BorderLayout());

		setBorder(new TitledBorder(PANEL_TITLE));

		add(filterField, BorderLayout.CENTER);
		add(startOnlyBox, BorderLayout.EAST);

		filterField.addKeyListener(new FilterListener());
	}

	public void setFocusToFilterField() {

		filterField.requestFocusInWindow();
	}

	protected abstract void applyFilter(GLexicalFilter filter);

	protected abstract void clearFilter();

	private void updateFiltering(boolean startOnly) {

		String filter = filterField.getText();

		if (filter.isEmpty()) {

			clearFilter();
		}
		else {

			applyFilter(new GLexicalFilter(filter, startOnly));
		}
	}
}

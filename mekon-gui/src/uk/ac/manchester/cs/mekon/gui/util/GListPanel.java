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

package uk.ac.manchester.cs.mekon.gui.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @author Colin Puleston
 */
public class GListPanel<E> extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String START_ONLY_SEL_TITLE = "begins with";

	private JTextField filterField = new JTextField();
	private JCheckBox startOnlySelector = new JCheckBox(START_ONLY_SEL_TITLE);

	private GList<E> list;

	private class FilterListener extends KeyAdapter {

		public void keyReleased(KeyEvent event) {

			update();
		}
	}

	private class StartOnlyListener implements ActionListener {

		public void actionPerformed(ActionEvent event) {

			update();
		}
	}

	public GListPanel(GList<E> list) {

		super(new BorderLayout());

		this.list = list;

		add(new JScrollPane(list), BorderLayout.CENTER);
		add(createFilterPanel(), BorderLayout.SOUTH);

		filterField.addKeyListener(new FilterListener());
		startOnlySelector.addActionListener(new StartOnlyListener());
		startOnlySelector.setSelected(true);
	}

	public void setFocusToFilterField() {

		filterField.requestFocusInWindow();
	}

	private JPanel createFilterPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(filterField, BorderLayout.CENTER);
		panel.add(startOnlySelector, BorderLayout.EAST);

		return panel;
	}

	private void update() {

		String filter = filterField.getText();

		if (!filter.isEmpty()) {

			boolean startOnly = startOnlySelector.isSelected();

			list.applyFilter(new GLexicalFilter(filter, startOnly));
		}
		else {

			list.clearFilter();
		}
	}
}

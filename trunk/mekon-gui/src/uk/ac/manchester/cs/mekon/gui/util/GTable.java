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

import java.util.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Colin Puleston
 */
public class GTable extends JPanel {

	static private final long serialVersionUID = -1;

	static private final Color TITLE_COLOUR = Color.lightGray;
	static private final Color CELL_COLOUR = Color.white;

	static private final int CELL_MARGIN = 10;

	private List<Column> columns = new ArrayList<Column>();

	private class Column extends JPanel {

		static private final long serialVersionUID = -1;

		Column(String title) {

			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			addLabelBox(createTitleLabel(title), TITLE_COLOUR);

			GTable.this.add(this);
			columns.add(this);
		}

		void addRowCell(Object cell) {

			addLabelBox(createCellLabel(cell), CELL_COLOUR);
		}

		private void addLabelBox(JLabel label, Color colour) {

			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

			setLabelSize(label);

			panel.add(label);
			panel.setBackground(colour);

			add(createLabelBox(panel));
		}

		private JLabel createTitleLabel(String title) {

			JLabel label = new JLabel(title);

			GFonts.setLarge(label);

			return label;
		}

		private JLabel createCellLabel(Object cell) {

			if (cell instanceof String) {

				return new JLabel((String)cell);
			}

			if (cell instanceof Icon) {

				return createLabelForIcon((Icon)cell);
			}

			throw new Error("Cannot create cell for type: " + cell.getClass());
		}

		private JComponent createLabelBox(JComponent comp) {

			return new JScrollPane(
						comp,
						JScrollPane.VERTICAL_SCROLLBAR_NEVER,
						JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		}

		private void setLabelSize(JLabel label) {

			Dimension s = label.getPreferredSize();
			int w = (int)s.getWidth() + CELL_MARGIN;
			int h = (int)s.getHeight() + CELL_MARGIN;

			label.setPreferredSize(new Dimension(w, h));
		}
	}

	public GTable() {

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}

	public void addColumns(String... titles) {

		for (String title : titles) {

			addColumn(title);
		}
	}

	public void addColumn(String title) {

		new Column(title);
	}

	public void addRow(Object... cells) {

		checkRowLength(cells.length);

		Iterator<Column> i = columns.iterator();

		for (Object cell : cells) {

			i.next().addRowCell(cell);
		}
	}

	private JLabel createLabelForIcon(Icon icon) {

		JLabel label = new JLabel();

		label.setIcon(icon);

		return label;
	}

	private void checkRowLength(int length) {

		if (length != columns.size()) {

			throw new Error(
						"Incorrect number of columns: "
						+ "expected: " + columns.size()
						+ "got: " + length);
		}
	}
}

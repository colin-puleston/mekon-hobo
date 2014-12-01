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
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * @author Colin Puleston
 */
public class GTable extends JTable {

	static private final long serialVersionUID = -1;

	static final int HDR_HEIGHT = 40;
	static final Color HDR_BACKGROUND = Color.LIGHT_GRAY;
	static final float HDR_FONT_SIZE = 16;
	static final int HDR_FONT_STYLE = Font.BOLD;

	static final int CELL_HEIGHT = 30;
	static final Color CELL_BACKGROUND = Color.WHITE;
	static final float CELL_FONT_SIZE = 14;
	static final int CELL_FONT_STYLE = Font.PLAIN;

	private CustomTableModel model = new CustomTableModel();

	private class CustomTableModel extends DefaultTableModel {

		static private final long serialVersionUID = -1;

		public boolean isCellEditable(int row, int column) {

			return false;
		}
	}

	private class CellRenderer extends DefaultTableCellRenderer {

		static private final long serialVersionUID = -1;

		public Component getTableCellRendererComponent(
								JTable table,
								Object value,
								boolean isSelected,
								boolean hasFocus,
								int row,
								int column) {

			JLabel label = (JLabel)super.getTableCellRendererComponent(
									table, value, isSelected, hasFocus, row, column);

			setCellAttributes(label);

			if (value instanceof Icon) {

				label.setIcon((Icon)value);
				label.setText("");
			}
			else {

				label.setIcon(null);
			}

			return label;
		}
	}

	public GTable() {

		setModel(model);
		setHeaderAttributes();
		setRowHeight(CELL_HEIGHT);
		setDefaultRenderer(Object.class, new CellRenderer());
	}

	public void addColumns(String... titles) {

		for (String title : titles) {

			addColumn(title);
		}
	}

	public void addColumn(String title) {

		model.addColumn(title);
	}

	public void addRow(Object... cells) {

		model.addRow(cells);

		setPreferredScrollableViewportSize(getPreferredSize());
	}

	private void setHeaderAttributes() {

		JTableHeader header = getTableHeader();

		setHeaderHeight(header);
		setAttributes(header, HDR_BACKGROUND, HDR_FONT_SIZE, HDR_FONT_STYLE);
	}

	private void setCellAttributes(JLabel cellLabel) {

		setAttributes(cellLabel, CELL_BACKGROUND, CELL_FONT_SIZE, CELL_FONT_STYLE);
	}

	private void setHeaderHeight(JTableHeader header) {

		int width = (int)header.getPreferredSize().getWidth();

		header.setPreferredSize(new Dimension(width, HDR_HEIGHT));
	}

	private void setAttributes(
					JComponent component,
					Color colour,
					float fontSize,
					int fontStyle) {

		component.setBackground(colour);

		Font font = component.getFont();

		font = font.deriveFont(fontSize);
		font = font.deriveFont(fontStyle);

		component.setFont(font);
	}
}

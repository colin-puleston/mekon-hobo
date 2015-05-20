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

	static final int HDR_HEIGHT = 30;
	static final Color HDR_BACKGROUND = Color.LIGHT_GRAY;
	static final float HDR_FONT_SIZE = 14;
	static final int HDR_FONT_STYLE = Font.BOLD;

	static final int CELL_HEIGHT = 30;
	static final float CELL_FONT_SIZE = 14;
	static final int CELL_FONT_STYLE = Font.PLAIN;

	private CustomTableModel model = new CustomTableModel();

	private class CustomTableModel extends DefaultTableModel {

		static private final long serialVersionUID = -1;

		public boolean isCellEditable(int row, int column) {

			return false;
		}
	}

	private class CellRenderer implements TableCellRenderer {

		static private final long serialVersionUID = -1;

		public Component getTableCellRendererComponent(
								JTable table,
								Object value,
								boolean isSelected,
								boolean hasFocus,
								int row,
								int column) {

			JComponent component = getCellComponent(value);

			setCellAttributes(component);

			return component;
		}

		private JComponent getCellComponent(Object value) {

			if (value instanceof JComponent) {

				return (JComponent)value;
			}

			JLabel label = new JLabel();

			if (value instanceof Icon) {

				label.setIcon((Icon)value);
			}
			else {

				label.setText(value.toString());
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
		resizeForNewColumn();
	}

	public void addRow(Object... cells) {

		model.addRow(cells);
		resizeForNewRow();
	}

	private void setHeaderAttributes() {

		JTableHeader header = getTableHeader();

		setHeaderHeight(header);
		header.setBackground(HDR_BACKGROUND);
		setFont(header, HDR_FONT_SIZE, HDR_FONT_STYLE);
	}

	private void setCellAttributes(JComponent component) {

		setFont(component, CELL_FONT_SIZE, CELL_FONT_STYLE);
	}

	private void setHeaderHeight(JTableHeader header) {

		int width = (int)header.getPreferredSize().getWidth();

		header.setPreferredSize(new Dimension(width, HDR_HEIGHT));
	}

	private void setFont(
					JComponent component,
					float fontSize,
					int fontStyle) {

		Font font = component.getFont();

		if (font != null) {

			font = font.deriveFont(fontSize);
			font = font.deriveFont(fontStyle);

			component.setFont(font);
		}
		else {

			setChildFonts(component, fontSize, fontStyle);
		}
	}

	private void setChildFonts(
					JComponent component,
					float fontSize,
					int fontStyle) {

		for (Component childComp : component.getComponents()) {

			if (component instanceof JComponent) {

				setFont((JComponent)childComp, fontSize, fontStyle);
			}
		}
	}

	private void resizeForNewColumn() {

		resizeColumnsForNewRow(-1);
	}

	private void resizeForNewRow() {

		resizeColumnsForNewRow(getRowCount() - 1);
		setPreferredScrollableViewportSize(getPreferredSize());
	}

	private void resizeColumnsForNewRow(int row) {

		for (int col = 0 ; col < getColumnCount() ; col++) {

			resizeColumnForRow(col, row);
		}
	}

	private void resizeColumnForRow(int col, int row) {

		TableColumn c = getColumnModel().getColumn(col);
		int cellWidth = getPreferredCellWidth(col, row);

		if (cellWidth > c.getPreferredWidth()) {

			c.setPreferredWidth(cellWidth);
		}
	}

	private int getPreferredCellWidth(int col, int row) {

		Component comp = getCellRendererComponent(col, row);

		return (int)comp.getPreferredSize().getWidth();
	}

	private Component getCellRendererComponent(int col, int row) {

		if (row == -1) {

			return getHeaderCellRendererComponent(col);
		}

		return prepareRenderer(getCellRenderer(row, col), row, col);
	}

	private Component getHeaderCellRendererComponent(int col) {

		TableColumn c = columnModel.getColumn(col);
		TableCellRenderer r = c.getHeaderRenderer();
		Object v = c.getHeaderValue();

		if (r == null) {

			r = getTableHeader().getDefaultRenderer();
		}

		return r.getTableCellRendererComponent(this, v, false, false, -1, col);
	}
}

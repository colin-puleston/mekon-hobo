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

	static private class CustomTableModel extends DefaultTableModel {

		static private final long serialVersionUID = -1;

		public boolean isCellEditable(int row, int column) {

			return false;
		}
	}

	private CustomTableModel model = new CustomTableModel();

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

			if (value != null) {

				if (value instanceof Icon) {

					label.setIcon((Icon)value);
				}
				else {

					label.setText(value.toString());
				}
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
		initialiseColumnSizes();
	}

	public void addRow(Object... cells) {

		model.addRow(cells);
		resizeForNewRow();
	}

	public void insertRow(int index, Object... cells) {

		model.insertRow(index, cells);
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

			if (childComp instanceof JComponent) {

				setFont((JComponent)childComp, fontSize, fontStyle);
			}
		}
	}

	private void initialiseColumnSizes() {

		for (int col = 0 ; col < getColumnCount() ; col++) {

			initialiseColumnSize(col);
		}
	}

	private void initialiseColumnSize(int col) {

		getColumn(col).setPreferredWidth(getPreferredHeaderWidth(col));
	}

	private void resizeForNewRow() {

		resizeColumnsForNewRow();
		setPreferredScrollableViewportSize(getPreferredSize());
	}

	private void resizeColumnsForNewRow() {

		int row = getRowCount() - 1;

		for (int col = 0 ; col < getColumnCount() ; col++) {

			resizeColumnForRow(col, row);
		}
	}

	private void resizeColumnForRow(int col, int row) {

		TableColumn column = getColumn(col);
		int cellWidth = getPreferredCellWidth(col, row);

		if (cellWidth > column.getPreferredWidth()) {

			column.setPreferredWidth(cellWidth);
		}
	}

	private int getPreferredHeaderWidth(int col) {

		return getPreferredWidth(getHeaderRendererComponent(col));
	}

	private int getPreferredCellWidth(int col, int row) {

		return getPreferredWidth(getCellRendererComponent(col, row));
	}

	private int getPreferredWidth(Component component) {

		return (int)component.getPreferredSize().getWidth();
	}

	private Component getHeaderRendererComponent(int col) {

		TableCellRenderer r = getTableHeader().getDefaultRenderer();
		Object v = getColumn(col).getHeaderValue();

		return r.getTableCellRendererComponent(this, v, false, false, -1, col);
	}

	private Component getCellRendererComponent(int col, int row) {

		return prepareRenderer(getCellRenderer(row, col), row, col);
	}

	private TableColumn getColumn(int index) {

		return getColumnModel().getColumn(index);
	}
}

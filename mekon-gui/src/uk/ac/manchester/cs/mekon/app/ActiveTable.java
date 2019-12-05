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

package uk.ac.manchester.cs.mekon.app;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
abstract class ActiveTable extends GTable {

	static private final long serialVersionUID = -1;

	static final float CELL_FONT_SIZE = 14;

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

			if (value instanceof ActiveTableCell) {

				configureLabel(label, (ActiveTableCell)value);
			}

			return label;
		}

		private void configureLabel(JLabel label, ActiveTableCell cell) {

			label.setForeground(cell.getForeground());
			label.setBackground(cell.getBackground());

			configureFont(label, cell.getFontStyle());
		}

		private void configureFont(JLabel label, int style) {

			Font font = label.getFont();

			font = font.deriveFont(style);
			font = font.deriveFont(CELL_FONT_SIZE);

			label.setFont(font);
		}
	}

	private class CellActionInvoker implements ListSelectionListener {

		private ListSelectionModel rowSelection;
		private ListSelectionModel colSelection;

		public void valueChanged(ListSelectionEvent e) {

			if (!e.getValueIsAdjusting()) {

				ActiveTableCell cell = getSelectedCellOrNull(e);

				if (cell != null && cell.userActionable()) {

					performCellAction(cell);
				}
			}
		}

		CellActionInvoker() {

			rowSelection = getSelectionModel();
			colSelection = columnModel.getSelectionModel();

			rowSelection.addListSelectionListener(this);
		}

		private ActiveTableCell getSelectedCellOrNull(ListSelectionEvent e) {

			int row = getSelectedRow();
			int col = getSelectedColumn();

			if (row != -1 && col != -1) {

				Object value = getValueAt(row, col);

				if (value instanceof ActiveTableCell) {

					return (ActiveTableCell)value;
				}
			}

			return null;
		}

		private void performCellAction(ActiveTableCell cell) {

			rowSelection.removeListSelectionListener(this);

			cell.performCellAction();

			rowSelection.clearSelection();
			colSelection.clearSelection();

			rowSelection.addListSelectionListener(this);

			repaint();
		}
	}

	ActiveTable(String[] titles) {

		addColumns(titles);

		for (int i = 0 ; i < getColumnCount() ; i++) {

			columnModel.getColumn(i).setCellRenderer(new CellRenderer());
		}

		new CellActionInvoker();
	}

	private void rebuild() {
	}
}

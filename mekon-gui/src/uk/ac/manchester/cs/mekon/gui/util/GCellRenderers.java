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
import javax.swing.*;
import javax.swing.tree.*;

/**
 * @author Colin Puleston
 */
class GCellRenderers {

	static private final GCellRenderers singleton = new GCellRenderers();

	static GCellRenderers get() {

		return singleton;
	}

	private TreeCellRenderer treeCellRenderer = new GTreeCellRenderer();
	private ListCellRenderer listCellRenderer = new GListCellRenderer();

	void set(GTree tree) {

		tree.setCellRenderer(treeCellRenderer);
	}

	void set(GList list) {

		list.setCellRenderer(listCellRenderer);
	}

	private class GTreeCellRenderer extends DefaultTreeCellRenderer {

		static private final long serialVersionUID = -1;

		public Component getTreeCellRendererComponent(
							JTree tree,
							Object value,
							boolean sel,
							boolean expanded,
							boolean leaf,
							int row,
							boolean hasFocus) {

			super.getTreeCellRendererComponent(
					tree, value, sel, expanded, leaf, row, hasFocus);

			customiseDisplayLabel(this, ((GNode)value).getDisplay());

			return this;
		}
	}

	private class GListCellRenderer extends DefaultListCellRenderer {

		static private final long serialVersionUID = -1;

		public Component getListCellRendererComponent(
							JList list,
							Object value,
							int index,
							boolean isSelected,
							boolean cellHasFocus) {

			GCellDisplay display = (GCellDisplay)value;
			JLabel label = (JLabel)super.getListCellRendererComponent(
									list, value, index, isSelected, cellHasFocus);

			customiseDisplayLabel(label, display);
			label.setText(display.getLabel());

			return label;
		}
	}

	private void customiseDisplayLabel(JLabel label, GCellDisplay display) {

		label.setFont(customiseDisplayFont(label.getFont(), display));
		label.setIcon(display.getIcon());
	}

	private Font customiseDisplayFont(Font font, GCellDisplay display) {

		return display.customiseFont(GFonts.toLarge(font));
	}
}
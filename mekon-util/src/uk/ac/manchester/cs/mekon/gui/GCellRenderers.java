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
import javax.swing.*;
import javax.swing.tree.*;

/**
 * @author Colin Puleston
 */
class GCellRenderers {

	static private final GCellRenderers singleton = new GCellRenderers();

	static private GTreeCellRenderer treeCellRenderer = new GTreeCellRenderer();
	static private GListCellRenderer listCellRenderer = new GListCellRenderer();

	static private class GTreeCellRenderer implements TreeCellRenderer {

		static private final long serialVersionUID = -1;

		public Component getTreeCellRendererComponent(
							JTree tree,
							Object value,
							boolean sel,
							boolean expanded,
							boolean leaf,
							int row,
							boolean hasFocus) {

			return GNode.cast(value).getDisplay().createComponent(sel);
		}
	}

	static private class GListCellRenderer implements ListCellRenderer<GCellDisplay> {

		static private final long serialVersionUID = -1;

		public Component getListCellRendererComponent(
							JList<? extends GCellDisplay> list,
							GCellDisplay value,
							int index,
							boolean sel,
							boolean hasFocus) {

			return value.createComponent(sel);
		}
	}

	static GCellRenderers get() {

		return singleton;
	}

	void set(GTree tree) {

		tree.setCellRenderer(treeCellRenderer);
	}

	void set(GList<?> list) {

		list.setCellRenderer(listCellRenderer);
	}
}

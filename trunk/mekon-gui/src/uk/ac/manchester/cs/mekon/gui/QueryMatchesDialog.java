/**
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

import java.awt.Dimension;
import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class QueryMatchesDialog extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE = "Query Matches";
	static private final Dimension WINDOW_SIZE = new Dimension(150, 300);

	QueryMatchesDialog(JComponent parent, List<CIdentity> matches) {

		super(parent, TITLE, true);

		setPreferredSize(WINDOW_SIZE);
		display(new JScrollPane(createGList(matches)));
	}

	private GList<CIdentity> createGList(List<CIdentity> matches) {

		GList<CIdentity> list = new GList<CIdentity>();

		for (CIdentity match : matches) {

			list.addEntity(match, new GCellDisplay(match.getLabel()));
		}

		return list;
	}
}

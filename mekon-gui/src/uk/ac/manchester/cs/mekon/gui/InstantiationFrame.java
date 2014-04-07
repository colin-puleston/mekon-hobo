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

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class InstantiationFrame extends GFrame {

	static private final long serialVersionUID = -1;

	static private final int WIDTH = 800;
	static private final int HEIGHT = 600;

	static private final String MAIN_TITLE_FORMAT = "\"%s\" Instantiation";

	static private final String INSTANCE_TAB_TITLE = "Instance";
	static private final String INFERRED_TYPES_TAB_TITLE = "Inferred Types";

	static private String getTitle(CFrame frameType) {

		String label = frameType.getIdentity().getLabel();
		String localTitle = String.format(MAIN_TITLE_FORMAT, label);

		return MekonModelExplorer.getSystemTitle(localTitle);
	}

	private CFrame frameType;

	InstantiationFrame(CFrame frameType) {

		super(getTitle(frameType), WIDTH, HEIGHT);

		this.frameType = frameType;
	}

	void display() {

		display(createMainComponent(frameType.instantiate()));
	}

	private JComponent createMainComponent(IFrame frame) {

		JTabbedPane panel = new JTabbedPane();

		panel.addTab(INSTANCE_TAB_TITLE, createInstanceComponent(frame));
		panel.addTab(INFERRED_TYPES_TAB_TITLE, createInferredTypesComponent(frame));

		return panel;
	}

	private JComponent createInstanceComponent(IFrame frame) {

		return new JScrollPane(new ITree(frame));
	}

	private JComponent createInferredTypesComponent(IFrame frame) {

		return new JScrollPane(new InferredTypesList(frame));
	}
}

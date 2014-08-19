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

import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class InstantiationFrame extends GFrame {

	static private final long serialVersionUID = -1;

	static private final int WIDTH = 800;
	static private final int HEIGHT = 600;

	static private final String MAIN_TITLE_FORMAT = "\"%s\" %s";
	static private final String CONCRETE_LABEL = "Instance";
	static private final String QUERY_LABEL = "Query";
	static private final String INFERRED_TYPES_TITLE = "Inferred Types";
	static private final String SUGGESTED_TYPES_TITLE = "Suggested Types";

	static private String getTitle(IFrame frame) {

		String frameLabel = frame.getDisplayLabel();
		String typeLabel = instanceTypeLabel(frame);
		String mainTitle = String.format(MAIN_TITLE_FORMAT, frameLabel, typeLabel);

		return MekonModelExplorer.getSystemTitle(mainTitle);
	}

	static private String instanceTypeLabel(IFrame frame) {

		return frame.getCategory().query() ? QUERY_LABEL : CONCRETE_LABEL;
	}

	private CFramesTree modelTree;
	private IFrame frame;

	InstantiationFrame(CFramesTree modelTree, IFrame frame) {

		super(getTitle(frame), WIDTH, HEIGHT);

		this.modelTree = modelTree;
		this.frame = frame;
	}

	void display() {

		display(createTopLevelComponent());
	}

	private JComponent createTopLevelComponent() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createMainComponent(), BorderLayout.CENTER);
		panel.add(new InstantiationOptionsPanel(frame), BorderLayout.SOUTH);

		return panel;
	}

	private JComponent createMainComponent() {

		JTabbedPane panel = new JTabbedPane();

		panel.addTab(instanceTypeLabel(frame), createInstanceComponent());

		addTypesTab(panel, INFERRED_TYPES_TITLE, true);
		addTypesTab(panel, SUGGESTED_TYPES_TITLE, false);

		return panel;
	}

	private void addTypesTab(JTabbedPane panel, String title, boolean inferreds) {

		int tabIndex = panel.getTabCount();

		panel.addTab(title, createTypesComponent(inferreds));
		panel.setEnabledAt(tabIndex, enableTypesTab(inferreds));
	}

	private JComponent createInstanceComponent() {

		return new JScrollPane(new ITree(frame));
	}

	private JComponent createTypesComponent(boolean inferreds) {

		return new JScrollPane(new InstanceTypesPanel(modelTree, frame, inferreds));
	}

	private boolean enableTypesTab(boolean inferreds) {

		return defaultUpdateOp(
					inferreds
					? IUpdateOp.INFERRED_TYPES
					: IUpdateOp.SUGGESTED_TYPES);
	}

	private boolean defaultUpdateOp(IUpdateOp updateOp) {

		return getModel().getIUpdating().defaultOp(updateOp);
	}

	private CModel getModel() {

		return frame.getType().getModel();
	}
}

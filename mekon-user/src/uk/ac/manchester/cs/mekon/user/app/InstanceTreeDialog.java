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

package uk.ac.manchester.cs.mekon.user.app;

import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
abstract class InstanceTreeDialog extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE_FORMAT = "%s %s (%s)";
	static private final String SUFFIXED_TITLE_FORMAT = "%s %s";

	static private final String ASSERTION_FUNCTION_LABEL = "Instance";
	static private final String QUERY_FUNCTION_LABEL = "Query";

	static private final int FRAME_WIDTH = 600;

	static private String createTitle(Instance instance, String suffix) {

		String type = getTypeLabel(instance);
		String function = getFunctionLabel(instance);
		String storeId = instance.getStoreId().getLabel();

		String title = String.format(TITLE_FORMAT, type, function, storeId);

		if (suffix == null) {

			return title;
		}

		return String.format(SUFFIXED_TITLE_FORMAT, title, suffix);
	}

	static private String getTypeLabel(Instance instance) {

		return instance.getRootFrame().getType().getIdentity().getLabel();
	}

	static private String getFunctionLabel(Instance instance) {

		return instance.queryInstance() ? QUERY_FUNCTION_LABEL : ASSERTION_FUNCTION_LABEL;
	}

	private InstanceTree tree = null;
	private Instance instance;

	public Dimension getPreferredSize() {

		return new Dimension(FRAME_WIDTH, getPreferredHeight());
	}

	InstanceTreeDialog(JComponent parent, Instance instance, String titleSuffix) {

		super(parent, createTitle(instance, titleSuffix), true);

		this.instance = instance;
	}

	void initialise(boolean summaryInstance, InstanceDisplayMode startMode) {

		initialise(instance.getRootFrame(), summaryInstance, startMode);
	}

	void initialise(IFrame displayRootFrame, boolean summaryInstance, InstanceDisplayMode startMode) {

		tree = new InstanceTree(instance, displayRootFrame, summaryInstance, startMode);
	}

	void display() {

		display(createDisplay());
	}

	InstanceTree getTree() {

		return tree;
	}

	Instance getInstance() {

		return instance;
	}

	InstanceGroup getGroup() {

		return instance.getGroup();
	}

	boolean queryInstance() {

		return instance.queryInstance();
	}

	JPanel checkCreateHeaderPanel() {

		return null;
	}

	ControlsPanel checkCreateControlsPanel() {

		return null;
	}

	private JComponent createDisplay() {

		JPanel panel = new JPanel(new BorderLayout());

		JPanel header = checkCreateHeaderPanel();
		ControlsPanel controls = checkCreateControlsPanel();

		if (header != null) {

			panel.add(header, BorderLayout.NORTH);
		}

		panel.add(new JScrollPane(tree), BorderLayout.CENTER);

		if (controls != null) {

			panel.add(controls, BorderLayout.SOUTH);
		}

		return panel;
	}

	private int getPreferredHeight() {

		return (int)super.getPreferredSize().getHeight();
	}
}

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

import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
abstract class InstanceSectionDialog extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE_FORMAT = "%s %s";
	static private final String ASSERTION_FUNCTION_LABEL = "Instance";
	static private final String QUERY_FUNCTION_LABEL = "Query";

	static private final String MODE_SELECTOR_LABEL = "View only";

	static private final int FRAME_WIDTH = 600;

	static String createSectionTitle(Instantiator instantiator) {

		return String.format(
					TITLE_FORMAT,
					getTypeLabel(instantiator),
					getFunctionLabel(instantiator));
	}

	static private String getTypeLabel(Instantiator instantiator) {

		Customiser customiser = instantiator.getController().getCustomiser();

		return customiser.getValueDisplayLabel(instantiator.getInstance());
	}

	static private String getFunctionLabel(Instantiator instantiator) {

		return instantiator.queryInstance() ? QUERY_FUNCTION_LABEL : ASSERTION_FUNCTION_LABEL;
	}

	private Instantiator instantiator;
	private InstanceTree instanceTree;

	private class ViewOnlySelector extends GCheckBox {

		static private final long serialVersionUID = -1;

		protected void onSelectionUpdate(boolean selected) {

			instanceTree.setViewOnly(selected);
		}

		ViewOnlySelector() {

			super(MODE_SELECTOR_LABEL);

			setSelected(instanceTree.viewOnly());
		}
	}

	public Dimension getPreferredSize() {

		return new Dimension(FRAME_WIDTH, getPreferredHeight());
	}

	InstanceSectionDialog(
		JComponent parent,
		Instantiator instantiator,
		IFrame rootFrame,
		String title) {

		super(parent, title, true);

		this.instantiator = instantiator;

		instanceTree = new InstanceTree(instantiator, rootFrame);
	}

	void display(boolean startAsViewOnly) {

		instanceTree.setViewOnly(startAsViewOnly);

		display(createDisplay());
	}

	Instantiator getInstantiator() {

		return instantiator;
	}

	abstract ControlsPanel checkCreateControlsPanel();

	private JComponent createDisplay() {

		JPanel panel = new JPanel(new BorderLayout());
		ControlsPanel controls = checkCreateControlsPanel();

		if (instantiator.editableInstance()) {

			panel.add(new ViewOnlySelector(), BorderLayout.NORTH);
		}

		panel.add(new JScrollPane(instanceTree), BorderLayout.CENTER);

		if (controls != null) {

			panel.add(controls, BorderLayout.SOUTH);
		}

		return panel;
	}

	private int getPreferredHeight() {

		return (int)super.getPreferredSize().getHeight();
	}
}

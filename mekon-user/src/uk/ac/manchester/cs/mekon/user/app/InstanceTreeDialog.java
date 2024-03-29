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

import java.util.*;
import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class InstanceTreeDialog extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE_FORMAT = "%s %s (%s)";
	static private final String SUFFIXED_TITLE_FORMAT = "%s %s";

	static private final String ASSERTION_FUNCTION_LABEL = "Instance";
	static private final String QUERY_FUNCTION_LABEL = "Query";

	static private final int FRAME_WIDTH = 600;

	static String createTitle(Instantiator instantiator, String suffix) {

		String type = getTypeLabel(instantiator);
		String function = getFunctionLabel(instantiator);
		String storeId = instantiator.getStoreId().getLabel();

		String title = String.format(TITLE_FORMAT, type, function, storeId);

		if (suffix == null) {

			return title;
		}

		return String.format(SUFFIXED_TITLE_FORMAT, title, suffix);
	}

	static private String getTypeLabel(Instantiator instantiator) {

		return instantiator.getInstance().getType().getIdentity().getLabel();
	}

	static private String getFunctionLabel(Instantiator instantiator) {

		return instantiator.queryInstance() ? QUERY_FUNCTION_LABEL : ASSERTION_FUNCTION_LABEL;
	}

	private Instantiator instantiator;
	private InstanceTree tree = null;

	private InstanceEditMode editMode = InstanceEditMode.FULL;
	private List<EditButton> editButtons = new ArrayList<EditButton>();

	abstract class EditListener extends GTreeListener {

		protected void onNodeAdded(GNode node) {

			onTreeEdited();
		}

		protected void onNodeRemoved(GNode node) {

			onTreeEdited();
		}

		abstract void onTreeEdited();
	}

	abstract class EditButton extends GButton {

		static private final long serialVersionUID = -1;

		EditButton(String label) {

			super(label);

			editButtons.add(this);
			updateEnabling();
		}

		void updateEnabling() {

			setEnabled(enableButton());
		}

		boolean enableButton() {

			return !viewOnly();
		}
	}

	private class ModeSelector extends InstanceDisplayModeSelector {

		static private final long serialVersionUID = -1;

		ModeSelector() {

			super(tree, getSelectableDisplayModes());
		}

		void onModeUpdate() {

			updateEditButtonEnabling();
		}
	}

	public Dimension getPreferredSize() {

		return new Dimension(FRAME_WIDTH, getPreferredHeight());
	}

	InstanceTreeDialog(JComponent parent, Instantiator instantiator, String titleSuffix) {

		super(parent, createTitle(instantiator, titleSuffix), true);

		this.instantiator = instantiator;
	}

	void initialise(boolean summaryInstance, InstanceDisplayMode startMode) {

		initialise(instantiator.getInstance(), summaryInstance, startMode);
	}

	void initialise(IFrame rootFrame, boolean summaryInstance, InstanceDisplayMode startMode) {

		tree = new InstanceTree(instantiator, rootFrame, summaryInstance, startMode);
	}

	void setEditMode(InstanceEditMode editMode) {

		this.editMode = editMode;
	}

	void addEditListener(EditListener editListener) {

		tree.addTreeListener(editListener);
	}

	void display() {

		display(createDisplay());
	}

	Instantiator getInstantiator() {

		return instantiator;
	}

	InstanceGroup getGroup() {

		return instantiator.getGroup();
	}

	InstanceTree getTree() {

		return tree;
	}

	InstanceDisplayMode getDisplayMode() {

		return tree.getDisplayMode();
	}

	InstanceEditMode getEditMode() {

		return editMode;
	}

	boolean fixedDisplayMode() {

		return getSelectableDisplayModes().size() == 1;
	}

	boolean viewOnly() {

		return tree.viewOnly();
	}

	ControlsPanel checkCreateControlsPanel() {

		return null;
	}

	GButton checkCreateAlternativeViewButton() {

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

	private JPanel checkCreateHeaderPanel() {

		ModeSelector modeSelector = fixedDisplayMode() ? null : new ModeSelector();
		GButton altViewButton = checkCreateAlternativeViewButton();

		if (modeSelector == null && altViewButton == null) {

			return null;
		}

		JPanel panel = new JPanel(new BorderLayout());

		if (modeSelector != null) {

			panel.add(modeSelector, BorderLayout.WEST);
		}

		if (altViewButton != null) {

			panel.add(altViewButton, BorderLayout.EAST);
		}

		return panel;
	}

	private List<InstanceDisplayMode> getSelectableDisplayModes() {

		List<InstanceDisplayMode> modes = new ArrayList<InstanceDisplayMode>();

		if (editMode == InstanceEditMode.FULL) {

			modes.add(InstanceDisplayMode.EDIT);
		}

		modes.add(InstanceDisplayMode.VIEW);

		if (instantiator.queryInstance()) {

			modes.add(InstanceDisplayMode.SEMANTICS);
		}

		return modes;
	}

	private void updateEditButtonEnabling() {

		for (EditButton editButton : editButtons) {

			editButton.updateEnabling();
		}
	}

	private int getPreferredHeight() {

		return (int)super.getPreferredSize().getHeight();
	}
}

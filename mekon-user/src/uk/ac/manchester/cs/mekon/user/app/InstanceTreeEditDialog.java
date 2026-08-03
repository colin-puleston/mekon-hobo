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
import java.awt.BorderLayout;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class InstanceTreeEditDialog extends InstanceTreeDialog {

	static private final long serialVersionUID = -1;

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

			super(getTree(), getSelectableDisplayModes());
		}

		void onModeUpdate() {

			updateEditButtonEnabling();
		}
	}

	InstanceTreeEditDialog(
		JComponent parent,
		Instantiator instantiator,
		String titleSuffix) {

		super(parent, instantiator, titleSuffix);
	}

	void setEditMode(InstanceEditMode editMode) {

		this.editMode = editMode;
	}

	void addEditListener(EditListener editListener) {

		getTree().addTreeListener(editListener);
	}

	InstanceDisplayMode getDisplayMode() {

		return getTree().getDisplayMode();
	}

	InstanceEditMode getEditMode() {

		return editMode;
	}

	boolean fixedDisplayMode() {

		return getSelectableDisplayModes().size() == 1;
	}

	boolean viewOnly() {

		return getTree().viewOnly();
	}

	JPanel checkCreateHeaderPanel() {

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

	GButton checkCreateAlternativeViewButton() {

		return null;
	}

	private List<InstanceDisplayMode> getSelectableDisplayModes() {

		List<InstanceDisplayMode> modes = new ArrayList<InstanceDisplayMode>();

		if (editMode == InstanceEditMode.FULL) {

			modes.add(InstanceDisplayMode.EDIT);
		}

		modes.add(InstanceDisplayMode.VIEW);

		if (queryInstance()) {

			modes.add(InstanceDisplayMode.SEMANTICS);
		}

		return modes;
	}

	private void updateEditButtonEnabling() {

		for (EditButton editButton : editButtons) {

			editButton.updateEnabling();
		}
	}
}

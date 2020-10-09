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

import java.awt.event.*;
import javax.swing.*;

/**
 * @author Colin Puleston
 */
abstract class InstanceDisplayModeSelector extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String EDIT_MODE_LABEL = "Edit";
	static private final String VIEW_MODE_LABEL = "View";
	static private final String SEMANTICS_MODE_LABEL = "Semantic view";

	private InstanceTree instanceTree;
	private ModeButton selected = null;

	private class ModeButton extends JRadioButton implements ActionListener {

		static private final long serialVersionUID = -1;

		private InstanceDisplayMode mode;

		public void actionPerformed(ActionEvent e) {

			if (isSelected()) {

				instanceTree.setMode(mode);

				selected.setSelected(false);
				selected = this;

				onModeUpdate();
			}
			else {

				setSelected(true);
			}
		}

		ModeButton(InstanceDisplayMode mode, String label) {

			super(label, startMode(mode));

			this.mode = mode;

			addActionListener(this);

			if (startMode(mode)) {

				selected = this;
			}

			InstanceDisplayModeSelector.this.add(this);
		}
	}

	InstanceDisplayModeSelector(InstanceTree instanceTree) {

		this.instanceTree = instanceTree;

		populate(instanceTree.getInstantiator());
	}

	boolean singleMode() {

		return getComponentCount() == 1;
	}

	abstract void onModeUpdate();

	private void populate(Instantiator instantiator) {

		if (instantiator.editableInstance()) {

			new ModeButton(InstanceDisplayMode.EDIT, EDIT_MODE_LABEL);
		}

		new ModeButton(InstanceDisplayMode.VIEW, VIEW_MODE_LABEL);

		if (instantiator.queryInstance()) {

			new ModeButton(InstanceDisplayMode.SEMANTICS, SEMANTICS_MODE_LABEL);
		}
	}

	private boolean startMode(InstanceDisplayMode mode) {

		return mode == instanceTree.getMode();
	}
}

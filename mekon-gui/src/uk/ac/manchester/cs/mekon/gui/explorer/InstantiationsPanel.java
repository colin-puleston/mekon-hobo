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
package uk.ac.manchester.cs.mekon.gui.explorer;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class InstantiationsPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String TITLE_FORMAT = "Instantiate \"%s\"";
	static private final String ASSERTION_BUTTON_LABEL = "Assertion...";
	static private final String QUERY_BUTTON_LABEL = "Query...";

	private CFramesTree modelTree;
	private InstanceStoreActions storeActions;
	private CFrame frame;

	private class InstantiateAssertionButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			instantiateAssertion();
		}

		InstantiateAssertionButton() {

			super(ASSERTION_BUTTON_LABEL);

			setEnabled(frameHasSlots());
		}
	}

	private class InstantiateQueryButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			instantiateQuery();
		}

		InstantiateQueryButton() {

			super(QUERY_BUTTON_LABEL);

			setEnabled(queriesEnabled() && frameHasSlots());
		}
	}

	InstantiationsPanel(
		CFramesTree modelTree,
		CFrame frame,
		InstanceStoreActions storeActions) {

		super(new BorderLayout());

		this.modelTree = modelTree;
		this.frame = frame;
		this.storeActions = storeActions;

		setBorder(createBorder());
		add(createButtonsComponent(), BorderLayout.EAST);
	}

	private TitledBorder createBorder() {

		TitledBorder border = new TitledBorder(createTitle());
		Font font = border.getTitleFont();

		if (font != null) {

			border.setTitleFont(GFonts.toMedium(font));
		}

		return border;
	}

	private String createTitle() {

		return String.format(TITLE_FORMAT, frame.getIdentity().getLabel());
	}

	private JComponent createButtonsComponent() {

		JPanel panel = new JPanel();

		panel.add(new InstantiateAssertionButton());
		panel.add(new InstantiateQueryButton());

		return panel;
	}

	private void instantiateAssertion() {

		new AssertionFrame(modelTree, storeActions, frame.instantiate()).display();
	}

	private void instantiateQuery() {

		new QueryFrame(modelTree, storeActions, frame.instantiateQuery()).display();
	}

	private boolean frameHasSlots() {

		return !frame.instantiate().getSlots().isEmpty();
	}

	private boolean queriesEnabled() {

		return frame.getModel().queriesEnabled();
	}
}

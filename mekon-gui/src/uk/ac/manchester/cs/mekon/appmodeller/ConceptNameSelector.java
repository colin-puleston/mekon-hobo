/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.mekon.appmodeller;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
class ConceptNameSelector extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE = "Enter Concept Name";

	static private final String OK_LABEL = "Ok";
	static private final String CANCEL_LABEL = "Cancel";

	static private final Dimension WINDOW_SIZE = new Dimension(250, 70);

	private String selection = null;
	private ControlButton okButton = new ControlButton(OK_LABEL);

	private class InputField extends GTextField {

		static private final long serialVersionUID = -1;

		protected boolean acceptKey(KeyEvent event) {

			return Character.isLetterOrDigit(event.getKeyChar());
		}

		protected void onKeyEntered(KeyEvent event) {

			selection = processCurrentInput();

			okButton.setEnabled(selection != null);
		}

		protected void onTextEntered(String text) {

			dispose();
		}

		private String processCurrentInput() {

			String text = getText();

			if (text.isEmpty()) {

				return null;
			}

			if (text.length() == 1) {

				text = text.toUpperCase();

				setText(text);
			}

			return text;
		}
	}

	private class ControlButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			if (getText().equals(CANCEL_LABEL)) {

				selection = null;
			}

			dispose();
		}

		ControlButton(String label) {

			super(label);
		}
	}

	ConceptNameSelector(JComponent parent) {

		super(parent, TITLE, true);

		okButton.setEnabled(false);

		display(createDisplay());
	}

	String getSelection() {

		return selection;
	}

	private JComponent createDisplay() {

		JPanel panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setPreferredSize(WINDOW_SIZE);
		panel.add(new InputField());
		panel.add(createButtonsPanel());

		return panel;
	}

	private JPanel createButtonsPanel() {

		return ControlsPanel.horizontal(okButton, new ControlButton(CANCEL_LABEL));
	}
}



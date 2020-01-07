/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.goblin.gui;

import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class ConceptIdSelector extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE = "Enter Concept Identity";

	static private final String OK_LABEL = "Ok";
	static private final String CANCEL_LABEL = "Cancel";

	static private final Dimension WINDOW_SIZE = new Dimension(250, 150);

	private EntityIdSpec selection = null;
	private ControlButton okButton = new ControlButton(OK_LABEL);

	private abstract class InputField extends GTextField {

		static private final long serialVersionUID = -1;

		private InputField otherField = null;
		private boolean userEdited = false;

		protected void onKeyEntered(KeyEvent event) {

			selection = resolveSelection();
			userEdited = anyText();

			updateForSelection();
			otherField.updateForSelection();

			okButton.setEnabled(userEdited);
		}

		protected void onTextEntered(String text) {

			dispose();
		}

		InputField() {

			userEdited = previousIndependentUserEdits();

			updateForSelection();
		}

		void setOtherField(InputField otherField) {

			this.otherField = otherField;
		}

		JPanel createPanel(String title) {

			return TitledPanels.create(this, title);
		}

		abstract EntityIdSpec resolveSelection(String text);

		abstract EntityIdSpec resolveSelection(String text, String otherText);

		abstract String getTextFromSelection();

		private boolean previousIndependentUserEdits() {

			return selection != null && selection.independentNameAndLabel();
		}

		private EntityIdSpec resolveSelection() {

			String otherText = otherField.checkForUserText();

			if (anyText()) {

				String text = getText();

				return otherText != null
						? resolveSelection(text, otherText)
						: resolveSelection(text);
			}

			return otherText != null ? otherField.resolveSelection(otherText) : null;
		}

		private boolean anyText() {

			return !getText().isEmpty();
		}

		private String checkForUserText() {

			return userEdited ? getText() : null;
		}

		private void updateForSelection() {

			setText(selection != null ? getTextFromSelection() : "");
		}
	}

	private class NameField extends InputField {

		static private final long serialVersionUID = -1;

		protected boolean acceptKey(KeyEvent event) {

			if (event.isActionKey()) {

				return true;
			}

			return validURIFragment(getText() + event.getKeyChar());
		}

		protected void onKeyEntered(KeyEvent event) {

			String text = getText();

			if (text.length() == 1) {

				setText(text.toUpperCase());
			}

			super.onKeyEntered(event);
		}

		EntityIdSpec resolveSelection(String text) {

			return EntityIdSpec.fromName(text);
		}

		EntityIdSpec resolveSelection(String text, String otherText) {

			return new EntityIdSpec(text, otherText);
		}

		String getTextFromSelection() {

			return selection.getName();
		}

		private boolean validURIFragment(String name) {

			try {

				new URI(name);

				return true;
			}
			catch (URISyntaxException e) {

				return false;
			}
		}
	}

	private class LabelField extends InputField {

		static private final long serialVersionUID = -1;

		EntityIdSpec resolveSelection(String text) {

			return EntityIdSpec.fromLabel(text);
		}

		EntityIdSpec resolveSelection(String text, String otherText) {

			return new EntityIdSpec(otherText, text);
		}

		String getTextFromSelection() {

			return selection.getLabel();
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

	ConceptIdSelector(JComponent parent, EntityIdSpec initialSelection) {

		super(parent, TITLE, true);

		selection = initialSelection;

		okButton.setEnabled(false);
		display(createDisplay());
	}

	EntityIdSpec getSelection() {

		return selection;
	}

	private JComponent createDisplay() {

		JPanel panel = new JPanel();

		InputField nameField = new NameField();
		InputField labelField = new LabelField();

		nameField.setOtherField(labelField);
		labelField.setOtherField(nameField);

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setPreferredSize(WINDOW_SIZE);
		panel.add(nameField.createPanel("Name"));
		panel.add(labelField.createPanel("Label"));
		panel.add(createButtonsPanel());

		return panel;
	}

	private JPanel createButtonsPanel() {

		return ControlsPanel.horizontal(okButton, new ControlButton(CANCEL_LABEL));
	}
}



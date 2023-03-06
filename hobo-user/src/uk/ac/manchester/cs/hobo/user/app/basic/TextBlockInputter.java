package uk.ac.manchester.cs.hobo.user.app.basic;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.user.util.gui.inputter.*;

import uk.ac.manchester.cs.hobo.user.app.basic.model.*;

/**
 * @author Colin Puleston
 */
class TextBlockInputter extends Inputter<String> {

	static private final long serialVersionUID = -1;

	static private final int WINDOW_WIDTH = 500;
	static private final int WINDOW_HEIGHT = 300;

	static private final String TITLE = "Enter Text";

	private JTextArea textArea = new JTextArea();

	private class InputValidityUpdater extends KeyAdapter {

		public void keyReleased(KeyEvent event) {

			setValidInput(!getInput().isEmpty());
		}

		InputValidityUpdater() {

			textArea.addKeyListener(this);
		}
	}

	public String getInput() {

		return textArea.getText();
	}

	protected JComponent getInputComponent() {

		return new JScrollPane(textArea);
	}

	protected Dimension getWindowSize() {

		return new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT);
	}

	TextBlockInputter(JComponent parent, TextBlock currentValueObj) {

		super(parent, TITLE, true, true);

		if (currentValueObj != null && currentValueObj.text.isSet()) {

			textArea.setText(currentValueObj.text.get());
		}

		new InputValidityUpdater();
	}
}

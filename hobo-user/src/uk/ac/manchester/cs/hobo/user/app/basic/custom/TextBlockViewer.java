package uk.ac.manchester.cs.hobo.user.app.basic.custom;

import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.hobo.user.app.basic.model.*;

/**
 * @author Colin Puleston
 */
class TextBlockViewer extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE = "Text";

	static private final Dimension WINDOW_SIZE = new Dimension(500, 300);

	private class Disposer extends WindowAdapter {

		public void windowClosing(WindowEvent e) {

			dispose();
		}
	}

	TextBlockViewer(TextBlock valueObj) {

		super(TITLE, true);

		setPreferredSize(WINDOW_SIZE);
		addWindowListener(new Disposer());

		display(new JScrollPane(createTextArea(valueObj)));
	}

	private JTextArea createTextArea(TextBlock valueObj) {

		JTextArea area = new JTextArea();

		area.setEditable(false);
		area.setFont(GFonts.toMedium(area.getFont()));

		if (valueObj.text.isSet()) {

			area.setText(valueObj.text.get());
		}

		return area;
	}
}

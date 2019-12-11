/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.goblin;

import java.awt.*;
import javax.swing.*;

/**
 * @author Colin Puleston
 */
class ControlsPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final int STRUT_SIZE = 10;

	static ControlsPanel horizontal(JComponent... control) {

		return new ControlsPanel(true, control);
	}

	static ControlsPanel vertical(JComponent... control) {

		return new ControlsPanel(false, control);
	}

	private boolean horizontal;

	ControlsPanel(boolean horizontal) {

		this.horizontal = horizontal;

		setLayout(new BoxLayout(this, getAxis()));
	}

	ControlsPanel(boolean horizontal, JComponent... controls) {

		this(horizontal);

		for (JComponent control : controls) {

			addControl(control);
		}
	}

	void addControl(JComponent control) {

		if (getComponentCount() != 0) {

			add(createStrut());
		}

		add(createControlPanel(control));
	}

	private int getAxis() {

		return horizontal ? BoxLayout.X_AXIS : BoxLayout.Y_AXIS;
	}

	private Component createStrut() {

		return horizontal
				? Box.createHorizontalStrut(STRUT_SIZE)
				: Box.createVerticalStrut(STRUT_SIZE);
	}

	private JComponent createControlPanel(JComponent control) {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(control, BorderLayout.CENTER);

		return panel;
	}
}

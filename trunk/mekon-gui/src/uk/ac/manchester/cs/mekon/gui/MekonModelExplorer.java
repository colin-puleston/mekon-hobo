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
package uk.ac.manchester.cs.mekon.gui;

import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
public class MekonModelExplorer extends GFrame {

	static private final long serialVersionUID = -1;

	static private final String SYSTEM_TITLE = "Mekon Model Explorer";
	static private final String MAIN_TITLE = getSystemTitle("Current Model");

	static private final int FRAME_WIDTH = 900;
	static private final int FRAME_HEIGHT = 700;

	static public void main(String[] args) {

		new MekonModelExplorer(CManager.createBuilder().build());
	}

	static String getSystemTitle(String title) {

		return SYSTEM_TITLE + ": " + title;
	}

	private class Initialiser {

		private ModelFrameSelections selections;
		private ModelFramesPanel framesPanel;
		private ModelFrameSelectionPanel selectionsPanel;

		Initialiser(CModel model) {

			selections = new ModelFrameSelections();
			framesPanel = new ModelFramesPanel(model);
			selectionsPanel = new ModelFrameSelectionPanel(model);

			selections.addSelectionRelay(framesPanel.getSelectionRelay());
			selections.addSelectionRelay(selectionsPanel.getSelectionRelay());
		}

		JComponent createMainPanel() {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(createControlPanel(), BorderLayout.NORTH);
			panel.add(createModelPanel(), BorderLayout.CENTER);

			return panel;
		}

		private JComponent createModelPanel() {

			GSplitPane panel = new GSplitPane();

			panel.setLeftComponent(framesPanel);
			panel.setRightComponent(selectionsPanel);

			return panel;
		}

		private JComponent createControlPanel() {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(new HelpInvocationPanel(), BorderLayout.EAST);
			panel.add(new NavigationPanel(selections), BorderLayout.WEST);

			return panel;
		}
	}

	public MekonModelExplorer(CModel model) {

		super(MAIN_TITLE, FRAME_WIDTH, FRAME_HEIGHT);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		display(new Initialiser(model).createMainPanel());
	}
}

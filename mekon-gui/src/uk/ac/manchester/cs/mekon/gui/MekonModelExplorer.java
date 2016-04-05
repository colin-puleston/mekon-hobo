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
import java.awt.event.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.store.*;

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

		new MekonModelExplorer();
	}

	static String getSystemTitle(String title) {

		return SYSTEM_TITLE + ": " + title;
	}

	static private class Initialiser {

		private ModelFramesPanel modelPanel;
		private ModelFrameSelectionPanel selectionPanel;
		private InstanceStorePanel instanceStorePanel;

		private ModelFrameSelections selections = new ModelFrameSelections();

		Initialiser(CModel model) {

			modelPanel = new ModelFramesPanel(model);

			CFramesTree modelTree = modelPanel.getTree();

			selectionPanel = new ModelFrameSelectionPanel(model, modelTree);
			instanceStorePanel = new InstanceStorePanel(model, modelTree);

			selections.addSelectionRelay(modelPanel.getSelectionRelay());
			selections.addSelectionRelay(selectionPanel.getSelectionRelay());
		}

		JComponent createTopLevelComponent() {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(createControlComponent(), BorderLayout.NORTH);
			panel.add(createMainComponent(), BorderLayout.CENTER);

			return panel;
		}

		private JComponent createMainComponent() {

			GSplitPane panel = new GSplitPane();

			panel.setLeftComponent(createModelAndInstancesPanel());
			panel.setRightComponent(selectionPanel);

			return panel;
		}

		private JComponent createControlComponent() {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(new HelpInvocationPanel(), BorderLayout.EAST);
			panel.add(new NavigationPanel(selections), BorderLayout.WEST);

			return panel;
		}

		private JPanel createModelAndInstancesPanel() {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(modelPanel, BorderLayout.CENTER);
			panel.add(instanceStorePanel, BorderLayout.SOUTH);

			return panel;
		}
	}

	static private class ModelClearUpper extends WindowAdapter {

		private CModel model;

		public void windowClosing(WindowEvent e) {

			IStoreManager.checkStop(model);
		}

		ModelClearUpper(CModel model) {

			this.model = model;
		}
	}

	public MekonModelExplorer() {

		this(CManager.createBuilder());
	}

	public MekonModelExplorer(CBuilder builder) {

		this(builder.build(), builder);
	}

	public MekonModelExplorer(CModel model, CBuilder builder) {

		super(MAIN_TITLE, FRAME_WIDTH, FRAME_HEIGHT);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		addWindowListener(new ModelClearUpper(model));

		if (builder != null ) {

			IStoreManager.getBuilder(builder).build();
		}

		display(new Initialiser(model).createTopLevelComponent());
	}

	public MekonModelExplorer(CModel model) {

		this(model, null);
	}
}

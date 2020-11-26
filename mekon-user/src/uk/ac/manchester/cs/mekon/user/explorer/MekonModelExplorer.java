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
package uk.ac.manchester.cs.mekon.user.explorer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.store.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
public class MekonModelExplorer extends GFrame {

	static private final long serialVersionUID = -1;

	static private final String SYSTEM_TITLE = "Mekon Model Explorer";
	static private final String MAIN_TITLE = getSystemTitle("Current Model");

	static private final int FRAME_WIDTH = 900;
	static private final int FRAME_HEIGHT = 700;

	static private class Initialiser {

		private ModelFramesPanel modelPanel;
		private ModelFrameSelectionPanel selectionPanel;
		private InstanceStorePanel instanceStorePanel;

		private ModelFrameSelections selections = new ModelFrameSelections();

		Initialiser(CModel model, IStore store) {

			modelPanel = new ModelFramesPanel(model);

			CFramesTree modelTree = modelPanel.getTree();
			InstanceStoreActions storeActions = new InstanceStoreActions(store, modelTree);

			selectionPanel = new ModelFrameSelectionPanel(modelTree, storeActions);
			instanceStorePanel = new InstanceStorePanel(storeActions);

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

			IDiskStoreManager.checkStopStore(model);
		}

		ModelClearUpper(CModel model) {

			this.model = model;
		}
	}

	static public void main(String[] args) {

		CBuilder builder = CManager.createBuilder();

		createWithDiskStore(builder.build(), builder);
	}

	static public void createWithDiskStore(CModel model, CBuilder builder) {

		IStore store = IDiskStoreManager.getBuilder(builder).build();

		new MekonModelExplorer(model, store);
	}

	static String getSystemTitle(String title) {

		return SYSTEM_TITLE + ": " + title;
	}

	public MekonModelExplorer(CModel model) {

		this(model, null);
	}

	public MekonModelExplorer(CModel model, IStore store) {

		super(MAIN_TITLE, FRAME_WIDTH, FRAME_HEIGHT);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new ModelClearUpper(model));

		display(new Initialiser(model, store).createTopLevelComponent());
	}
}

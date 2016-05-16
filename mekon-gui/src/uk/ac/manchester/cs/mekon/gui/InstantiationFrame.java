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
import uk.ac.manchester.cs.mekon.store.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
abstract class InstantiationFrame extends GFrame {

	static private final long serialVersionUID = -1;

	static private final int WIDTH = 800;
	static private final int HEIGHT = 600;

	static private final String MAIN_TITLE_FORMAT = "\"%s\" %s";
	static private final String INFERRED_TYPES_TITLE = "Inferred Types";
	static private final String SUGGESTED_TYPES_TITLE = "Suggested Types";

	private IFrame frame;

	private JTabbedPane aspectTabs = new JTabbedPane();

	private JComponent instanceComponent;
	private InferredTypesPanel inferredTypesPanel;
	private SuggestedTypesPanel suggestedTypesPanel;

	private class TypesPanelsUpdater implements IFrameListener {

		public void onUpdatedInferredTypes(CIdentifieds<CFrame> updates) {

			inferredTypesPanel.update(updates);
		}

		public void onUpdatedSuggestedTypes(CIdentifieds<CFrame> updates) {

			suggestedTypesPanel.update(updates);
		}

		public void onSlotAdded(ISlot slot) {
		}

		public void onSlotRemoved(ISlot slot) {
		}
	}

	InstantiationFrame(CFramesTree modelTree, IFrame frame) {

		super("", WIDTH, HEIGHT);

		this.frame = frame;

		instanceComponent = createInstanceComponent();
		inferredTypesPanel = new InferredTypesPanel(modelTree);
		suggestedTypesPanel = new SuggestedTypesPanel(modelTree);

		frame.addListener(new TypesPanelsUpdater());
	}

	void display() {

		setTitle(getCategoryTitle());
		setTabs(instanceComponent);

		display(createTopLevelComponent());
	}

	abstract String getCategoryLabel();

	abstract JComponent createControlsPanel();

	IFrame getFrame() {

		return frame;
	}

	void resetMainComponent(JComponent mainComponent) {

		setTabs(mainComponent);
		aspectTabs.setSelectedIndex(0);
	}

	void checkResetInstanceComponent() {

		if (aspectTabs.getComponentAt(0) != instanceComponent) {

			setTabs(instanceComponent);
		}
	}

	JComponent getInstanceComponent() {

		return instanceComponent;
	}

	private JComponent createTopLevelComponent() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(aspectTabs, BorderLayout.CENTER);
		panel.add(createControlsPanel(), BorderLayout.SOUTH);

		return panel;
	}

	private JComponent createInstanceComponent() {

		return new JScrollPane(new ITree(frame));
	}

	private void setTabs(JComponent mainTabComponent) {

		aspectTabs.removeAll();

		addAspectTab(
			getCategoryLabel(),
			mainTabComponent);

		addTypesTab(
			INFERRED_TYPES_TITLE,
			inferredTypesPanel,
			IUpdateOp.INFERRED_TYPES);

		addTypesTab(
			SUGGESTED_TYPES_TITLE,
			suggestedTypesPanel,
			IUpdateOp.SUGGESTED_TYPES);
	}

	private void addTypesTab(
					String title,
					InstanceTypesPanel panel,
					IUpdateOp updateOp) {

		int index = addAspectTab(title, new JScrollPane(panel));

		aspectTabs.setEnabledAt(index, enableTypesTab(updateOp));
	}

	private int addAspectTab(String title, JComponent component) {

		int index = aspectTabs.getTabCount();

		aspectTabs.addTab(title, component);

		return index;
	}

	private boolean enableTypesTab(IUpdateOp updateOp) {

		return getModel().getIUpdating().defaultOp(updateOp);
	}

	private String getCategoryTitle() {

		String label = frame.getDisplayLabel();
		String catLabel = getCategoryLabel();
		String mainTitle = String.format(MAIN_TITLE_FORMAT, label, catLabel);

		return MekonModelExplorer.getSystemTitle(mainTitle);
	}

	private CModel getModel() {

		return frame.getType().getModel();
	}
}

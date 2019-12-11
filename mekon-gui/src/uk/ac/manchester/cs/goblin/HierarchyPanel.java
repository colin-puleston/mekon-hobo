/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.goblin;

import javax.swing.*;

import uk.ac.manchester.cs.mekon.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class HierarchyPanel extends GSplitPane {

	static private final long serialVersionUID = -1;

	static private final String TREE_PANEL_TITLE = "Selected hierarchy";
	static private final String CONSTRAINTS_PANEL_TITLE = "Dependent hierarchies";

	HierarchyPanel(Hierarchy hierarchy) {

		HierarchyEditPanel treePanel = new HierarchyEditPanel(hierarchy);
		HierarchyEditTree tree = treePanel.getTree();

		setLeftComponent(createTreeComponent(treePanel));
		setRightComponent(createConstraintsComponent(hierarchy, tree));
	}

	private JComponent createTreeComponent(HierarchyEditPanel treePanel) {

		return TitledPanels.create(treePanel, TREE_PANEL_TITLE);
	}

	private JComponent createConstraintsComponent(
							Hierarchy hierarchy,
							HierarchyEditTree tree) {

		JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);

		for (ConstraintType type : hierarchy.getConstraintTypes()) {

			String title = getConstraintPanelTitle(type);

			tabs.addTab(title, new ConstraintPanel(type, tree));
		}

		return TitledPanels.create(tabs, CONSTRAINTS_PANEL_TITLE);
	}

	private String getConstraintPanelTitle(ConstraintType type) {

		return type.getRootTargetConcept().getConceptId().getLabel();
	}
}

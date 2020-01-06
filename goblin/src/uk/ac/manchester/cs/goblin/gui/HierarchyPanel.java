/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.goblin.gui;

import java.util.*;

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

	private Hierarchy hierarchy;

	private HierarchyTree hierarchyTree;
	private ConstraintsPanel constraintsPanel;

	private class ConstraintsPanel extends ConceptTreesPanel<ConstraintType> {

		static private final long serialVersionUID = -1;

		ConstraintsPanel() {

			super(JTabbedPane.TOP);

			populate();
		}

		List<ConstraintType> getSources() {

			return hierarchy.getConstraintTypes();
		}

		Concept getRootConcept(ConstraintType type) {

			return type.getRootTargetConcept();
		}

		JComponent createComponent(ConstraintType type) {

			return new ConstraintPanel(type, hierarchyTree);
		}
	}

	HierarchyPanel(Hierarchy hierarchy) {

		this.hierarchy = hierarchy;

		HierarchyTreePanel treePanel = new HierarchyTreePanel(hierarchy);

		hierarchyTree = treePanel.getTree();
		constraintsPanel = new ConstraintsPanel();

		setLeftComponent(createTreeComponent(treePanel));
		setRightComponent(createConstraintsComponent());
	}

	void makeConstraintVisible(Constraint constraint) {

		hierarchyTree.selectConcept(constraint.getSourceValue());
		constraintsPanel.makeSourceVisible(constraint.getType());
	}

	private JComponent createTreeComponent(HierarchyTreePanel treePanel) {

		return TitledPanels.create(treePanel, TREE_PANEL_TITLE);
	}

	private JComponent createConstraintsComponent() {

		return TitledPanels.create(constraintsPanel, CONSTRAINTS_PANEL_TITLE);
	}
}

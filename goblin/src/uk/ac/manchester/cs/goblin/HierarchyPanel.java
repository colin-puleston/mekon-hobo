/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.goblin;

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

	private HierarchyEditTree hierarchyEditTree;
	private ConstraintsPanel constraintsPanel;

	private class ConstraintsPanel extends HierarchiesPanel<ConstraintType> {

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

			return new ConstraintPanel(type, hierarchyEditTree);
		}
	}

	HierarchyPanel(Hierarchy hierarchy) {

		this.hierarchy = hierarchy;

		HierarchyEditPanel treePanel = new HierarchyEditPanel(hierarchy);

		hierarchyEditTree = treePanel.getTree();
		constraintsPanel = new ConstraintsPanel();

		setLeftComponent(createTreeComponent(treePanel));
		setRightComponent(createConstraintsComponent());
	}

	void makeEditVisible(PrimaryEdit edit) {

		hierarchyEditTree.selectConcept(edit.getConcept());

		if (edit.constraintEdit()) {

			constraintsPanel.makeSourceVisible(edit.getConstraint().getType());
		}
	}

	private JComponent createTreeComponent(HierarchyEditPanel treePanel) {

		return TitledPanels.create(treePanel, TREE_PANEL_TITLE);
	}

	private JComponent createConstraintsComponent() {

		return TitledPanels.create(constraintsPanel, CONSTRAINTS_PANEL_TITLE);
	}
}

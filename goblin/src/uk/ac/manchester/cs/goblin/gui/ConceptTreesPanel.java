/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.goblin.gui;

import java.util.*;

import javax.swing.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
abstract class ConceptTreesPanel<S> extends JTabbedPane {

	static private final long serialVersionUID = -1;

	ConceptTreesPanel(int tabPlacement) {

		super(tabPlacement);
	}

	void populate() {

		int i = 0;

		for (S source : getSources()) {

			addTab(getTitle(source), createComponent(source));
		}
	}

	void repopulate() {

		removeAll();
		populate();
	}

	void makeSourceVisible(S source) {

		setSelectedIndex(getSources().indexOf(source));
	}

	int makeSourceVisible(Concept rootConcept) {

		int i = 0;

		for (S source : getSources()) {

			if (getRootConcept(source).equals(rootConcept)) {

				setSelectedIndex(i);

				break;
			}

			i++;
		}

		return i;
	}

	abstract List<S> getSources();

	abstract Concept getRootConcept(S treeSource);

	abstract JComponent createComponent(S treeSource);

	private String getTitle(S treeSource) {

		return getRootConcept(treeSource).getConceptId().getLabel();
	}
}

/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.goblin;

import java.util.*;

import javax.swing.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
abstract class HierarchiesPanel<S> extends JTabbedPane {

	static private final long serialVersionUID = -1;

	HierarchiesPanel(int tabPlacement) {

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

	int makeConceptVisible(Concept concept) {

		int i = 0;

		for (S source : getSources()) {

			if (concept.subsumedBy(getRootConcept(source))) {

				setSelectedIndex(i);

				break;
			}

			i++;
		}

		return i;
	}

	void makeSourceVisible(S source) {

		setSelectedIndex(getSources().indexOf(source));
	}

	abstract List<S> getSources();

	abstract Concept getRootConcept(S hierarchySource);

	abstract JComponent createComponent(S hierarchySource);

	private String getTitle(S hierarchySource) {

		return getRootConcept(hierarchySource).getConceptId().getLabel();
	}
}

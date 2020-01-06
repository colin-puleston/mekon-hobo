/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.goblin.gui;

import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */

abstract class SelectionDependentButton<E> extends GButton {

	static private final long serialVersionUID = -1;

	private class Enabler extends GSelectionListener<E> {

		protected void onSelected(E entity) {

			setEnabled(enableOnSelection(entity));
		}

		protected void onSelectionCleared() {

			setEnabled(enableOnNoSelection());
		}
	}

	SelectionDependentButton(String label) {

		super(label);

		setEnabled(enableOnNoSelection());
	}

	GSelectionListener<E> initialise() {

		return new Enabler();
	}

	abstract boolean enableOnSelection(E entity);

	boolean enableOnNoSelection() {

		return false;
	}
}

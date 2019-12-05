/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.mekon.appmodeller;

import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */

abstract class ListSelectionDependentButton<E> extends SelectionDependentButton<E> {

	static private final long serialVersionUID = -1;

	ListSelectionDependentButton(String label, GList<E> list) {

		super(label);

		list.addSelectionListener(initialise());
	}

	boolean enableOnSelection(E entity) {

		return true;
	}
}

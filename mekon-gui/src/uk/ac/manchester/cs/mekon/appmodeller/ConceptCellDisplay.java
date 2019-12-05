/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.mekon.appmodeller;

import java.awt.Color;
import javax.swing.Icon;

import uk.ac.manchester.cs.mekon.gui.util.*;
import uk.ac.manchester.cs.mekon.gui.util.icon.*;

import uk.ac.manchester.cs.mekon.appmodeller.model.*;

/**
 * @author Colin Puleston
 */
enum ConceptCellDisplay {

	FULL_HIERARCHY_DEFAULT(Color.CYAN),
	FULL_HIERARCHY_MOVE_SUBJECT(Color.GRAY.brighter()),
	DIRECT_CONSTRAINT_TARGETS(Color.GREEN),
	INDIRECT_CONSTRAINT_TARGETS(Color.YELLOW);

	static private final int ICON_SIZE = 12;

	private Icon icon;

	GCellDisplay getFor(Concept concept) {

		return new GCellDisplay(concept.getConceptId().getLabel(), icon);
	}

	ConceptCellDisplay(Color clr) {

		icon = createIcon(clr);
	}

	private GIcon createIcon(Color clr) {

		return new GIcon(new GOvalRenderer(clr, ICON_SIZE));
	}
}
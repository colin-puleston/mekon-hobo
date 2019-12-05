package uk.ac.manchester.cs.mekon.appmodeller.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class AutoConfirmations implements Confirmations {

	public boolean confirmConceptMove(List<Constraint> invalidatedConstraints) {

		return true;
	}

	public boolean confirmConstraintAddition(List<Constraint> conflicts) {

		return true;
	}
}

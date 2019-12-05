package uk.ac.manchester.cs.mekon.appmodeller.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public interface Confirmations {

	public boolean confirmConceptMove(List<Constraint> invalidatedConstraints);

	public boolean confirmConstraintAddition(List<Constraint> conflicts);
}

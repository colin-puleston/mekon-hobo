package uk.ac.manchester.cs.mekon.appmodeller.model;

/**
 * @author Colin Puleston
 */
public interface ConceptConstraintsListener {

	public void onConstraintAdded(Constraint constraint);

	public void onConstraintRemoved(Constraint constraint);
}
